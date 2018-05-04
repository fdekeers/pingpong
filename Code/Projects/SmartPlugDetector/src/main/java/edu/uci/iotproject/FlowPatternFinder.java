package edu.uci.iotproject;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.DnsPacket;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides functionality for searching for the presence of a {@link FlowPattern} in a PCAP trace.
 * We use 2 threads:
 *  1) The first thread (main thread) collects conversations from the PCAP stream and put them into our data structure.
 *  2) The second thread (checker thread) checks the collected conversation.
 *
 * @author Janus Varmarken
 * @author Rahmadi Trimananda
 */
public class FlowPatternFinder {

    /* Class properties */
    private Map<Conversation, List<PcapPacket>> connections;
    private Queue<Conversation> conversations;
    private DnsMap dnsMap;
    private PcapHandle pcap;
    private FlowPattern pattern;
    private AtomicBoolean isEoF;
   
    
    /* Constructor */
    public FlowPatternFinder(PcapHandle _pcap, FlowPattern _pattern) {

        this.connections = new ConcurrentHashMap<Conversation, List<PcapPacket>>();
        this.conversations = new ConcurrentLinkedQueue<Conversation>();
        this.dnsMap = new DnsMap();
        this.isEoF = new AtomicBoolean(false);

        // Get input parameters
        this.pcap = _pcap;
        this.pattern = _pattern;
    }
    
    
    public void start() {
    
        // Spawn the main thread
        Thread mainThread = new Thread(new Runnable() {
            public void run() {
                findFlowPattern();
            }
        });
        mainThread.start();

        // Spawn the checker thread
        Thread checkerThread = new Thread(new Runnable() {
            public void run() {
                find();
            }
        });
        checkerThread.start();

        /* TODO: Join the threads if we want it to be blocking
        try {
            mainThread.join();
            checkerThread.join();
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }*/
        System.out.println("[ start ] Main and checker threads started!");
    }


    /**
     * Find patterns based on the FlowPattern object (run by a thread)
     */
    private void findFlowPattern() {
        int counter = 0;
        try {
            PcapPacket packet;
            Set<Integer> seqNumberSet = new HashSet<Integer>();
            int patternLength = pattern.getLength();
            while ((packet = pcap.getNextPacketEx()) != null) {

                // Check if this is a valid DNS packet
                dnsMap.validateAndAddNewEntry(packet);
                // For now, we only work support pattern search in TCP over IPv4.
                IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                if (ipPacket == null || tcpPacket == null)
                    continue;
                String srcAddress = ipPacket.getHeader().getSrcAddr().getHostAddress();
                String dstAddress = ipPacket.getHeader().getDstAddr().getHostAddress();
                int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
                int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
                // Is this packet related to the pattern and coming to/from the cloud server?
                boolean fromServer = dnsMap.isRelatedToCloudServer(srcAddress, pattern.getHostname());
                boolean fromClient = dnsMap.isRelatedToCloudServer(dstAddress, pattern.getHostname());
                if (!fromServer && !fromClient)  // Packet not related to pattern, skip it.
                    continue;
                if (tcpPacket.getPayload() == null) // We skip non-payload control packets as these are less predictable
                    continue; 
                // Identify conversations (connections/sessions) by the four-tuple (clientIp, clientPort, serverIp, serverPort).
                // TODO: this is strictly not sufficient to differentiate one TCP session from another, but should suffice for now.
                Conversation conversation = fromClient ? new Conversation(srcAddress, srcPort, dstAddress, dstPort) :
                        new Conversation(dstAddress, dstPort, srcAddress, srcPort);
                // Create new conversation entry, or append packet to existing.
                List<PcapPacket> listPcapPacket = connections.get(conversation);
                if (listPcapPacket == null) {
                    listPcapPacket = new ArrayList<PcapPacket>();
                    connections.put(conversation, listPcapPacket);
                }
                int seqNumber = packet.get(TcpPacket.class).getHeader().getSequenceNumber();
                boolean retransmission = seqNumberSet.contains(seqNumber);
                if (!retransmission) { // Do not add if retransmission -> avoid duplicate packets in flow
                    listPcapPacket.add(packet);
                    // End of conversation -> trigger thread to check
                    if (listPcapPacket.size() == patternLength)
                        conversations.add(conversation);
                    seqNumberSet.add(seqNumber);
                }
            }
        } catch (EOFException eofe) {
            while (isEoF.compareAndSet(false, true) == false);  // Try to signal EoF!
            System.out.println("[ findFlowPattern ] Finished processing entire PCAP stream!");
        } catch (UnknownHostException |
                 PcapNativeException  |
                 NotOpenException     |
                 TimeoutException ex) {
            ex.printStackTrace();
        }
    }
    

    /**
     * Checker to match collected patterns (run by a thread)
     */
    private void find() {

        while (isEoF.get() == false) {  // Continue until EoF
            // Get the object from the queue
            while(conversations.peek() == null) {  // Wait until queue is not empty
                if (isEoF.get() == true)    // Return if EoF
                    return;
            }            
            Conversation conversation = conversations.poll();
            // Get the object and remove it from the Map (housekeeping)
            List<PcapPacket> packets = connections.remove(conversation);
            boolean completeMatch = true;
            for (int i = 0; i < packets.size(); i++) {
                TcpPacket tcpPacket = packets.get(i).get(TcpPacket.class);
                if (tcpPacket.getPayload().length() != pattern.getPacketOrder().get(i)) {
                    completeMatch = false;
                    break;
                }
            }
            if (completeMatch) {
                PcapPacket firstPacketInFlow = packets.get(0);
                System.out.println(
                        String.format("[ find ] Detected a complete match of pattern '%s' at %s!",
                                pattern.getPatternId(), firstPacketInFlow.getTimestamp().toString()));
            } /*else {
                PcapPacket firstPacketInFlow = packets.get(0);
                System.out.println(
                        String.format("[ detected a mismatch of pattern '%s' at %s]",
                                pattern.getPatternId(), firstPacketInFlow.getTimestamp().toString()));
            }*/
        }
    }


    /**
     * Immutable class used for identifying a conversation/connection/session/flow (packet's belonging to the same
     * session between a client and a server).
     */
    private static class Conversation {

        private final String clientIp;
        private final int clientPort;
        private final String serverIp;
        private final int serverPort;

        public Conversation(String clientIp, int clientPort, String serverIp, int serverPort) {
            this.clientIp = clientIp;
            this.clientPort = clientPort;
            this.serverIp = serverIp;
            this.serverPort = serverPort;
        }


        // =========================================================================================================
        // We simply reuse equals and hashCode methods of String.class to be able to use this immutable class as a key
        // in a Map.
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Conversation && this.toString().equals(obj.toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        // =========================================================================================================

        @Override
        public String toString() {
            return String.format("%s:%d %s:%d", clientIp, clientPort, serverIp, serverPort);
        }
    }
}
