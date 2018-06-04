package edu.uci.iotproject;

import edu.uci.iotproject.comparison.ComparisonFunctions;
import edu.uci.iotproject.comparison.CompleteMatchPatternComparisonResult;
import edu.uci.iotproject.comparison.PatternComparisonTask;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.DnsPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;


/**
 * <p>Provides functionality for searching for the presence of a {@link FlowPattern} in a PCAP trace.</p>
 *
 * <p>
 * The (entire) PCAP trace is traversed and parsed on one thread (specifically, the thread that calls
 * {@link #findFlowPattern()}). This thread builds a {@link DnsMap} using the DNS packets present in the trace and uses
 * that {@code DnsMap} to reassemble {@link Conversation}s that <em>potentially</em> match the provided
 * {@link FlowPattern} (in that one end/party of said conversations matches the hostname(s) specified by the given
 * {@code FlowPattern}).
 * These potential matches are then examined on background worker thread(s) to determine if they are indeed a (complete)
 * match of the provided {@code FlowPattern}.
 * </p>
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class FlowPatternFinder {

    /* Begin class properties */
    /**
     * {@link ExecutorService} responsible for parallelizing pattern searches.
     * Declared as static to allow for reuse of threads across different instances of {@code FlowPatternFinder} and to
     * avoid the overhead of initializing a new thread pool for each {@code FlowPatternFinder} instance.
     */
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    /* End class properties */

    /* Begin instance properties */
    /**
     * Holds a set of {@link Conversation}s that <em>potentially</em> match {@link #mPattern} since each individual
     * {@code Conversation} is communication with the hostname identified by {@code mPattern.getHostname()}.
     * Note that due to limitations of the {@link Set} interface (specifically, there is no {@code get(T t)} method),
     * we have to resort to a {@link Map} (in which keys map to themselves) to "mimic" a set with {@code get(T t)}
     * functionality.
     *
     * @see <a href="https://stackoverflow.com/questions/7283338/getting-an-element-from-a-set">this question on StackOverflow.com</a>
     */
    private final Map<Conversation, Conversation> mConversations;

    private final DnsMap mDnsMap;
    private final PcapHandle mPcap;
    private final FlowPattern mPattern;

    private final List<Future<CompleteMatchPatternComparisonResult>> mPendingComparisons = new ArrayList<>();
    /* End instance properties */

    /**
     * Constructs a new {@code FlowPatternFinder}.
     * @param pcap an <em>open</em> {@link PcapHandle} that provides access to the trace that is to be examined.
     * @param pattern the {@link FlowPattern} to search for.
     */
    public FlowPatternFinder(PcapHandle pcap, FlowPattern pattern) {
        this.mConversations = new HashMap<>();
        this.mDnsMap = new DnsMap();
        this.mPcap = Objects.requireNonNull(pcap,
                String.format("Argument of type '%s' cannot be null", PcapHandle.class.getSimpleName()));
        this.mPattern = Objects.requireNonNull(pattern,
                String.format("Argument of type '%s' cannot be null", FlowPattern.class.getSimpleName()));
    }

    /**
     * Starts the pattern search.
     */
    public void start() {
        findFlowPattern();
    }

    /**
     * Find patterns based on the FlowPattern object (run by a thread)
     */
    private void findFlowPattern() {
        try {
            PcapPacket packet;
//            TODO: The new comparison method is pending
//            TODO: For now, just compare using one hostname and one list per FlowPattern
//            List<String> hostnameList = mPattern.getHostnameList();
//            int hostIndex = 0;
            while ((packet = mPcap.getNextPacketEx()) != null) {
                // Let DnsMap handle DNS packets.
                if (packet.get(DnsPacket.class) != null) {
                    // Check if this is a valid DNS packet
                    mDnsMap.validateAndAddNewEntry(packet);
                    continue;
                }
                // For now, we only work support pattern search in TCP over IPv4.
                final IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                final TcpPacket tcpPacket = packet.get(TcpPacket.class);
                if (ipPacket == null || tcpPacket == null) {
                    continue;
                }

                String srcAddress = ipPacket.getHeader().getSrcAddr().getHostAddress();
                String dstAddress = ipPacket.getHeader().getDstAddr().getHostAddress();
                int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
                int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
                // Is this packet related to the pattern; i.e. is it going to (or coming from) the cloud server?
                boolean fromServer = mDnsMap.isRelatedToCloudServer(srcAddress, mPattern.getHostname());
                boolean fromClient = mDnsMap.isRelatedToCloudServer(dstAddress, mPattern.getHostname());
//                String currentHostname = hostnameList.get(hostIndex);
//                boolean fromServer = mDnsMap.isRelatedToCloudServer(srcAddress, currentHostname);
//                boolean fromClient = mDnsMap.isRelatedToCloudServer(dstAddress, currentHostname);
                if (!fromServer && !fromClient) {
                    // Packet not related to pattern, skip it.
                    continue;
                }

                // Conversations (connections/sessions) are identified by the four-tuple
                // (clientIp, clientPort, serverIp, serverPort) (see Conversation Javadoc).
                // Create "dummy" conversation for looking up an existing entry.
                Conversation conversation = fromClient ? new Conversation(srcAddress, srcPort, dstAddress, dstPort) :
                        new Conversation(dstAddress, dstPort, srcAddress, srcPort);
                // Add the packet so that the "dummy" conversation can be immediately added to the map if no entry
                // exists for the conversation that the current packet belongs to.
                if (tcpPacket.getHeader().getFin()) {
                    // Record FIN packets.
                    conversation.addFinPacket(packet);
                }
                if (tcpPacket.getPayload() != null) {
                    // Record regular payload packets.
                    conversation.addPacket(packet, true);
                }
                // Note: does not make sense to call attemptAcknowledgementOfFin here as the new packet has no FINs
                // in its list, so if this packet is an ACK, it would not be added anyway.

                // Need to retain a final reference to get access to the packet in the lambda below.
                final PcapPacket finalPacket = packet;
                // Add the new conversation to the map if an equal entry is not already present.
                // If an existing entry is already present, the current packet is simply added to that conversation.
                mConversations.merge(conversation, conversation, (existingEntry, toMerge) -> {
                    // toMerge may not have any payload packets if the current packet is a FIN packet.
                    if (toMerge.getPackets().size() > 0) {
                        existingEntry.addPacket(toMerge.getPackets().get(0), true);
                    }
                    if (toMerge.getFinAckPairs().size() > 0) {
                        // Add the FIN packet to the existing entry.
                        existingEntry.addFinPacket(toMerge.getFinAckPairs().get(0).getFinPacket());
                    }
                    if (finalPacket.get(TcpPacket.class).getHeader().getAck()) {
                        existingEntry.attemptAcknowledgementOfFin(finalPacket);
                    }
                    return existingEntry;
                });
                // Refresh reference to point to entry in map (in case packet was added to existing entry).
                conversation = mConversations.get(conversation);
                if (conversation.isGracefullyShutdown()) {
                    // Conversation terminated gracefully, so we can now start analyzing it.
                    // Remove the Conversation from the map and start the analysis.
                    // Any future packets identified by the same four tuple will be tied to a new Conversation instance.
                    mConversations.remove(conversation);
                    // Create comparison task and send to executor service.
                    PatternComparisonTask<CompleteMatchPatternComparisonResult> comparisonTask =
                            new PatternComparisonTask<>(conversation, mPattern, ComparisonFunctions.SUB_SEQUENCE_COMPLETE_MATCH);
                    mPendingComparisons.add(EXECUTOR_SERVICE.submit(comparisonTask));
                    // Increment hostIndex to find the next
                    
                }
            }
        } catch (EOFException eofe) {
            // TODO should check for leftover conversations in map here and fire tasks for those.
            // TODO [cont'd] such tasks may be present if connections did not terminate gracefully or if there are longlived connections.
            System.out.println("[ findFlowPattern ] Finished processing entire PCAP stream!");
            System.out.println("[ findFlowPattern ] Now waiting for comparisons to finish...");
            // Wait for all comparisons to finish, then output their results to std.out.
            for(Future<CompleteMatchPatternComparisonResult> comparisonTask : mPendingComparisons) {
                try {
                    // Blocks until result is ready.
                    CompleteMatchPatternComparisonResult comparisonResult = comparisonTask.get();
                    if (comparisonResult.getResult()) {
                        System.out.println(comparisonResult.getTextualDescription());
                    }
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException |
                 PcapNativeException  |
                 NotOpenException     |
                 TimeoutException ex) {
            ex.printStackTrace();
        }
    }

}
