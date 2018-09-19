package edu.uci.iotproject.analysis;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.DnsMap;
import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.uci.iotproject.util.PcapPacketUtils.*;

/**
 * Utility functions for analyzing and structuring (sets of) {@link Conversation}s.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class TcpConversationUtils {

    /**
     * <p>
     *      Given a {@link Conversation}, extract its set of "packet pairs", i.e., pairs of request-reply packets.
     *      <em>The extracted pairs are formed from the full set of payload-carrying TCP packets.</em>
     * </p>
     *
     * <b>Note:</b> in the current implementation, if one endpoint sends multiple packets back-to-back with no
     * interleaved reply packets from the other endpoint, such packets are converted to one-item pairs (i.e., instances
     * of {@link PcapPacketPair} where {@link PcapPacketPair#getSecond()} is {@code null}).
     *
     * @param conv The {@code Conversation} for which packet pairs are to be extracted.
     * @return The packet pairs extracted from {@code conv}.
     */
    public static List<PcapPacketPair> extractPacketPairs(Conversation conv) {
        return extractPacketPairs(conv.getPackets());
    }


    /**
     * <p>
     *      Given a {@link Conversation}, extract its set of "packet pairs", i.e., pairs of request-reply packets.
     *      <em>The extracted pairs are formed from the full set of TLS Application Data packets.</em>
     * </p>
     *
     * <b>Note:</b> in the current implementation, if one endpoint sends multiple packets back-to-back with no
     * interleaved reply packets from the other endpoint, such packets are converted to one-item pairs (i.e., instances
     * of {@link PcapPacketPair} where {@link PcapPacketPair#getSecond()} is {@code null}).
     *
     * @param conv The {@code Conversation} for which packet pairs are to be extracted.
     * @return The packet pairs extracted from {@code conv}.
     */
    public static List<PcapPacketPair> extractTlsAppDataPacketPairs(Conversation conv) {
        if (!conv.isTls()) {
            throw new IllegalArgumentException(String.format("Provided %s argument is not a TLS session"));
        }
        return extractPacketPairs(conv.getTlsApplicationDataPackets());
    }

    // Helper method for implementing the public API of similarly named methods.
    private static List<PcapPacketPair> extractPacketPairs(List<PcapPacket> packets) {
        List<PcapPacketPair> pairs = new ArrayList<>();
        for(PcapPacket pp : packets) {
            System.out.print(pp.length() + " ");
        }
        System.out.println();

        int i = 0;
        while (i < packets.size()) {
            PcapPacket p1 = packets.get(i);
            String p1SrcIp = p1.get(IpV4Packet.class).getHeader().getSrcAddr().getHostAddress();
            int p1SrcPort = p1.get(TcpPacket.class).getHeader().getSrcPort().valueAsInt();
            if (i+1 < packets.size()) {
                PcapPacket p2 = packets.get(i+1);
                if (PcapPacketUtils.isSource(p2, p1SrcIp, p1SrcPort)) {
                    // Two packets in a row going in the same direction -> create one item pair for p1
                    pairs.add(new PcapPacketPair(p1, null));
                    // Advance one packet as the following two packets may form a valid two-item pair.
                    i++;
                } else {
                    // The two packets form a response-reply pair, create two-item pair.
                    pairs.add(new PcapPacketPair(p1, p2));
                    // Advance two packets as we have already processed the packet at index i+1 in order to create the pair.
                    i += 2;
                    //i++;
                }
            } else {
                // Last packet of conversation => one item pair
                pairs.add(new PcapPacketPair(p1, null));
                // Advance i to ensure termination.
                i++;
            }
        }
        return pairs;
        // TODO: what if there is long time between response and reply packet? Should we add a threshold and exclude those cases?
    }

    /**
     * Given a collection of TCP conversations and associated DNS mappings, groups the conversations by hostname.
     * @param tcpConversations The collection of TCP conversations.
     * @param ipHostnameMappings The associated DNS mappings.
     * @return A map where each key is a hostname and its associated value is a list of conversations where one of the
     *         two communicating hosts is that hostname (i.e. its IP maps to the hostname).
     */
    public static Map<String, List<Conversation>> groupConversationsByHostname(Collection<Conversation> tcpConversations, DnsMap ipHostnameMappings) {
        HashMap<String, List<Conversation>> result = new HashMap<>();
        for (Conversation c : tcpConversations) {
            if (c.getPackets().size() == 0) {
                String warningStr = String.format("Detected a %s [%s] with no payload packets.",
                        c.getClass().getSimpleName(), c.toString());
                System.err.println(warningStr);
                continue;
            }
            IpV4Packet firstPacketIp = c.getPackets().get(0).get(IpV4Packet.class);
            String ipSrc = firstPacketIp.getHeader().getSrcAddr().getHostAddress();
            String ipDst = firstPacketIp.getHeader().getDstAddr().getHostAddress();
            // Check if src or dst IP is associated with one or more hostnames.
            Set<String> hostnames = ipHostnameMappings.getHostnamesForIp(ipSrc);
            if (hostnames == null) {
                // No luck with src ip (possibly because it's a client->srv packet), try dst ip.
                hostnames = ipHostnameMappings.getHostnamesForIp(ipDst);
            }
            if (hostnames != null) {
                // Put a reference to the conversation for each of the hostnames that the conversation's IP maps to.
                for (String hostname : hostnames) {
                    List<Conversation> newValue = new ArrayList<>();
                    newValue.add(c);
                    result.merge(hostname, newValue, (l1, l2) -> { l1.addAll(l2); return l1; });
                }
                if (hostnames.size() > 1) {
                    // Print notice of IP mapping to multiple hostnames (debugging)
                    System.err.println(String.format("%s: encountered an IP that maps to multiple (%d) hostnames",
                            TcpConversationUtils.class.getSimpleName(), hostnames.size()));
                }
            } else {
                // If no hostname mapping, store conversation under the key that is the concatenation of the two IPs.
                // In order to ensure consistency when mapping conversations, use lexicographic order to select which IP
                // goes first.
                String delimiter = "_";
                // Note that the in case the comparison returns 0, the strings are equal, so it doesn't matter which of
                // ipSrc and ipDst go first (also, this case should not occur in practice as it means that the device is
                // communicating with itself!)
                String key = ipSrc.compareTo(ipDst) <= 0 ? ipSrc + delimiter + ipDst : ipDst + delimiter + ipSrc;
                List<Conversation> newValue = new ArrayList<>();
                newValue.add(c);
                result.merge(key, newValue, (l1, l2) -> { l1.addAll(l2); return l1; });
            }
        }
        return result;
    }

    public static Map<String, Integer> countPacketSequenceFrequencies(Collection<Conversation> conversations) {
        Map<String, Integer> result = new HashMap<>();
        for (Conversation conv : conversations) {
            if (conv.getPackets().size() == 0) {
                // Skip conversations with no payload packets.
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (PcapPacket pp : conv.getPackets()) {
                sb.append(pp.length() + " ");
            }
            result.merge(sb.toString(), 1, (i1, i2) -> i1+i2);
        }
        return result;
    }

    /**
     * Given a {@link Collection} of {@link Conversation}s, builds a {@link Map} from {@link String} to {@link List}
     * of {@link Conversation}s such that each key is the <em>concatenation of the packet lengths of all payload packets
     * (i.e., the set of packets returned by {@link Conversation#getPackets()}) separated by a delimiter</em> of any
     * {@link Conversation} pointed to by that key. In other words, what the {@link Conversation}s {@code cs} pointed to
     * by the key {@code s} have in common is that they all contain exactly the same number of payload packets <em>and
     * </em> these payload packets are identical across all {@code Conversation}s in {@code cs} in terms of packet
     * length and packet order. For example, if the key is "152 440 550", this means that every individual
     * {@code Conversation} in the list of {@code Conversation}s pointed to by that key contain exactly three payload
     * packet of lengths 152, 440, and 550, and these three packets are ordered in the order prescribed by the key.
     *
     * @param conversations The collection of {@code Conversation}s to group by packet sequence.
     * @param verbose If set to {@code true}, the grouping (and therefore the key) will also include SYN/SYNACK,
     *                FIN/FINACK, RST packets, and each payload-carrying packet will have an indication of the direction
     *                of the packet prepended.
     * @return a {@link Map} from {@link String} to {@link List} of {@link Conversation}s such that each key is the
     *         <em>concatenation of the packet lengths of all payload packets (i.e., the set of packets returned by
     *         {@link Conversation#getPackets()}) separated by a delimiter</em> of any {@link Conversation} pointed to
     *         by that key.
     */
    public static Map<String, List<Conversation>> groupConversationsByPacketSequence(Collection<Conversation> conversations, boolean verbose) {
        return conversations.stream().collect(Collectors.groupingBy(c -> toSequenceString(c, verbose)));
    }

    public static Map<String, List<Conversation>> groupConversationsByTlsApplicationDataPacketSequence(Collection<Conversation> conversations) {
        return conversations.stream().collect(Collectors.groupingBy(
                c -> c.getTlsApplicationDataPackets().stream().map(p -> Integer.toString(p.getOriginalLength())).
                        reduce("", (s1, s2) -> s1.length() == 0 ? s2 : s1 + " " + s2))
        );
    }

    /**
     * Given a {@link Conversation}, counts the frequencies of each unique packet length seen as part of the
     * {@code Conversation}.
     * @param c The {@code Conversation} for which unique packet length frequencies are to be determined.
     * @return A mapping from packet length to its frequency.
     */
    public static Map<Integer, Integer> countPacketLengthFrequencies(Conversation c) {
        Map<Integer, Integer> result = new HashMap<>();
        for (PcapPacket packet : c.getPackets()) {
            result.merge(packet.length(), 1, (i1, i2) -> i1 + i2);
        }
        return result;
    }

    /**
     * Like {@link #countPacketLengthFrequencies(Conversation)}, but counts packet length frequencies for a collection
     * of {@code Conversation}s, i.e., the frequency of a packet length becomes the total number of packets with that
     * length across <em>all</em> {@code Conversation}s in {@code conversations}.
     * @param conversations The collection of {@code Conversation}s for which packet length frequencies are to be
     *                      counted.
     * @return A mapping from packet length to its frequency.
     */
    public static Map<Integer, Integer> countPacketLengthFrequencies(Collection<Conversation> conversations) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Conversation c : conversations) {
            Map<Integer, Integer> intermediateResult = countPacketLengthFrequencies(c);
            for (Map.Entry<Integer, Integer> entry : intermediateResult.entrySet()) {
                result.merge(entry.getKey(), entry.getValue(), (i1, i2) -> i1 + i2);
            }
        }
        return result;
    }

    public static Map<String, Integer> countPacketPairFrequencies(Collection<PcapPacketPair> pairs) {
        Map<String, Integer> result = new HashMap<>();
        for (PcapPacketPair ppp : pairs) {
            result.merge(ppp.toString(), 1, (i1, i2) -> i1 + i2);
        }
        return result;
    }

    public static Map<String, Map<String, Integer>> countPacketPairFrequenciesByHostname(Collection<Conversation> tcpConversations, DnsMap ipHostnameMappings) {
        Map<String, List<Conversation>> convsByHostname = groupConversationsByHostname(tcpConversations, ipHostnameMappings);
        HashMap<String, Map<String, Integer>> result = new HashMap<>();
        for (Map.Entry<String, List<Conversation>> entry : convsByHostname.entrySet()) {
            // Merge all packet pairs exchanged during the course of all conversations with hostname into one list
            List<PcapPacketPair> allPairsExchangedWithHostname = new ArrayList<>();
            entry.getValue().forEach(conversation -> allPairsExchangedWithHostname.addAll(extractPacketPairs(conversation)));
            // Then count the frequencies of packet pairs exchanged with the hostname, irrespective of individual
            // conversations
            result.put(entry.getKey(), countPacketPairFrequencies(allPairsExchangedWithHostname));
        }
        return result;
    }

    /**
     * Given a {@link Conversation}, extract its packet length sequence.
     * @param c The {@link Conversation} from which a packet length sequence is to be extracted.
     * @return An {@code Integer[]} that holds the packet lengths of all payload-carrying packets in {@code c}. The
     *         packet lengths in the returned array are ordered by packet timestamp.
     */
    public static Integer[] getPacketLengthSequence(Conversation c) {
        return getPacketLengthSequence(c.getPackets());
    }


    /**
     * Given a {@link Conversation}, extract its packet length sequence, but only include packet lengths of those
     * packets that carry TLS Application Data.
     * @param c The {@link Conversation} from which a TLS Application Data packet length sequence is to be extracted.
     * @return An {@code Integer[]} that holds the packet lengths of all packets in {@code c} that carry TLS Application
     *         Data. The packet lengths in the returned array are ordered by packet timestamp.
     */
    public static Integer[] getPacketLengthSequenceTlsAppDataOnly(Conversation c) {
        if (!c.isTls()) {
            throw new IllegalArgumentException("Provided " + c.getClass().getSimpleName() + " was not a TLS session");
        }
        return getPacketLengthSequence(c.getTlsApplicationDataPackets());
    }

    /**
     * Given a list of packets, extract the packet lengths and wrap them in an array such that the packet lengths in the
     * resulting array appear in the same order as their corresponding packets in the input list.
     * @param packets The list of packets for which the packet lengths are to be extracted.
     * @return An array containing the packet lengths in the same order as their corresponding packets in the input list.
     */
    private static Integer[] getPacketLengthSequence(List<PcapPacket> packets) {
        return packets.stream().map(pkt -> pkt.getOriginalLength()).toArray(Integer[]::new);
    }

    /**
     * Builds a string representation of the sequence of packets exchanged as part of {@code c}.
     * @param c The {@link Conversation} for which a string representation of the packet sequence is to be constructed.
     * @param verbose {@code true} if set to true, the returned sequence string will also include SYN/SYNACK,
     *                FIN/FINACK, RST packets, as well as an indication of the direction of payload-carrying packets.
     * @return a string representation of the sequence of packets exchanged as part of {@code c}.
     */
    private static String toSequenceString(Conversation c, boolean verbose) {
        // Payload-parrying packets are always included, but only prepend direction if verbose output is chosen.
        Stream<String> s = c.getPackets().stream().map(p -> verbose ? c.getDirection(p).toCompactString() + p.getOriginalLength() : Integer.toString(p.getOriginalLength()));
        if (verbose) {
            // In the verbose case, we also print SYN, FIN and RST packets.
            // Convert the SYN packets to a string representation and prepend them in front of the payload packets.
            s = Stream.concat(c.getSynPackets().stream().map(p -> isSyn(p) && isAck(p) ? "SYNACK" : "SYN"), s);
            // Convert the FIN packets to a string representation and append them after the payload packets.
            s = Stream.concat(s, c.getFinAckPairs().stream().map(f -> f.isAcknowledged() ? "FINACK" : "FIN"));
            // Convert the RST packets to a string representation and append at the end.
            s = Stream.concat(s, c.getRstPackets().stream().map(r -> "RST"));
        }
        /*
         * Note: the collector internally uses a StringBuilder, which is more efficient than simply doing string
         * concatenation as in the following example:
         * s.reduce("", (s1, s2) -> s1.length() == 0 ? s2 : s1 + " " + s2);
         * (above code is O(N^2) where N is the number of characters)
         */
        return s.collect(Collectors.joining(" "));
    }

    /**
     * Appends a space to {@code sb} <em>iff</em> {@code sb} already contains some content.
     * @param sb A {@link StringBuilder} that should have a space appended <em>iff</em> it is not empty.
     */
    private static void appendSpaceIfNotEmpty(StringBuilder sb) {
        if (sb.length() != 0) {
            sb.append(" ");
        }
    }
}
