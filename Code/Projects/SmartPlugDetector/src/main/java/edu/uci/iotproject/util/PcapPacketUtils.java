package edu.uci.iotproject.util;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.analysis.PcapPacketPair;
import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.*;

/**
 * Utility methods for inspecting {@link PcapPacket} properties.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public final class PcapPacketUtils {

    /**
     * This is the threshold value for a signature's number of members
     * If after a merging the number of members of a signature falls below this threshold, then we can boldly
     * get rid of that signature.
     */
    private static final int SIGNATURE_MERGE_THRESHOLD = 5;

    /**
     * Determines if a given {@link PcapPacket} wraps a {@link TcpPacket}.
     * @param packet The {@link PcapPacket} to inspect.
     * @return {@code true} if {@code packet} wraps a {@link TcpPacket}, {@code false} otherwise.
     */
    public static boolean isTcp(PcapPacket packet) {
        return packet.get(TcpPacket.class) != null;
    }

    /**
     * Gets the source IP (in decimal format) of an IPv4 packet.
     * @param packet The packet for which the IPv4 source address is to be extracted.
     * @return The decimal representation of the source IP of {@code packet} <em>iff</em> {@code packet} wraps an
     *         {@link IpV4Packet}.
     * @throws NullPointerException if {@code packet} does not encapsulate an {@link IpV4Packet}.
     */
    public static String getSourceIp(PcapPacket packet) {
        return getIpV4PacketOrThrow(packet).getHeader().getSrcAddr().getHostAddress();
    }

    /**
     * Gets the destination IP (in decimal format) of an IPv4 packet.
     * @param packet The packet for which the IPv4 source address is to be extracted.
     * @return The decimal representation of the destination IP of {@code packet} <em>iff</em> {@code packet} wraps an
     *         {@link IpV4Packet}.
     * @throws NullPointerException if {@code packet} does not encapsulate an {@link IpV4Packet}.
     */
    public static String getDestinationIp(PcapPacket packet) {
        return getIpV4PacketOrThrow(packet).getHeader().getDstAddr().getHostAddress();
    }

    /**
     * Gets the source port of a TCP packet.
     * @param packet The packet for which the source port is to be extracted.
     * @return The source port of the {@link TcpPacket} encapsulated by {@code packet}.
     * @throws IllegalArgumentException if {@code packet} does not encapsulate a {@link TcpPacket}.
     */
    public static int getSourcePort(PcapPacket packet) {
        TcpPacket tcpPacket = packet.get(TcpPacket.class);
        if (tcpPacket == null) {
            throw new IllegalArgumentException("not a TCP packet");
        }
        return tcpPacket.getHeader().getSrcPort().valueAsInt();
    }

    /**
     * Gets the destination port of a TCP packet.
     * @param packet The packet for which the destination port is to be extracted.
     * @return The destination port of the {@link TcpPacket} encapsulated by {@code packet}.
     * @throws IllegalArgumentException if {@code packet} does not encapsulate a {@link TcpPacket}.
     */
    public static int getDestinationPort(PcapPacket packet) {
        TcpPacket tcpPacket = packet.get(TcpPacket.class);
        if (tcpPacket == null) {
            throw new IllegalArgumentException("not a TCP packet");
        }
        return tcpPacket.getHeader().getDstPort().valueAsInt();
    }

    /**
     * Helper method to determine if the given combination of IP and port matches the source of the given packet.
     * @param packet The packet to check.
     * @param ip The IP to look for in the ip.src field of {@code packet}.
     * @param port The port to look for in the tcp.port field of {@code packet}.
     * @return {@code true} if the given ip+port match the corresponding fields in {@code packet}.
     */
    public static boolean isSource(PcapPacket packet, String ip, int port) {
        IpV4Packet ipPacket = Objects.requireNonNull(packet.get(IpV4Packet.class));
        // For now we only support TCP flows.
        TcpPacket tcpPacket = Objects.requireNonNull(packet.get(TcpPacket.class));
        String ipSrc = ipPacket.getHeader().getSrcAddr().getHostAddress();
        int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        return ipSrc.equals(ip) && srcPort == port;
    }

    /**
     * Helper method to determine if the given combination of IP and port matches the destination of the given packet.
     * @param packet The packet to check.
     * @param ip The IP to look for in the ip.dst field of {@code packet}.
     * @param port The port to look for in the tcp.dstport field of {@code packet}.
     * @return {@code true} if the given ip+port match the corresponding fields in {@code packet}.
     */
    public static boolean isDestination(PcapPacket packet, String ip, int port) {
        IpV4Packet ipPacket = Objects.requireNonNull(packet.get(IpV4Packet.class));
        // For now we only support TCP flows.
        TcpPacket tcpPacket = Objects.requireNonNull(packet.get(TcpPacket.class));
        String ipDst = ipPacket.getHeader().getDstAddr().getHostAddress();
        int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
        return ipDst.equals(ip) && dstPort == port;
    }

    /**
     * Checks if the source IP address of the {@link IpV4Packet} contained in {@code packet} is a local address, i.e.,
     * if it pertains to subnet 10.0.0.0/8, 172.16.0.0/16, or 192.168.0.0/16.
     * @param packet The packet for which the source IP address is to be examined.
     * @return {@code true} if {@code packet} wraps a {@link IpV4Packet} for which the source IP address is a local IP
     *         address, {@code false} otherwise.
     * @throws NullPointerException if {@code packet} does not encapsulate an {@link IpV4Packet}.
     */
    public static boolean isSrcIpLocal(PcapPacket packet) {
        return getIpV4PacketOrThrow(packet).getHeader().getSrcAddr().isSiteLocalAddress();
    }

    /**
     * Checks if the destination IP address of the {@link IpV4Packet} contained in {@code packet} is a local address,
     * i.e., if it pertains to subnet 10.0.0.0/8, 172.16.0.0/16, or 192.168.0.0/16.
     * @param packet The packet for which the destination IP address is to be examined.
     * @return {@code true} if {@code packet} wraps a {@link IpV4Packet} for which the destination IP address is a local
     *         IP address, {@code false} otherwise.
     * @throws NullPointerException if {@code packet} does not encapsulate an {@link IpV4Packet}.
     */
    public static boolean isDstIpLocal(PcapPacket packet) {
        return getIpV4PacketOrThrow(packet).getHeader().getDstAddr().isSiteLocalAddress();
    }

    /**
     * Checks if {@code packet} wraps a TCP packet that has the SYN flag set.
     * @param packet A {@link PcapPacket} that is suspected to contain a {@link TcpPacket} for which the SYN flag is set.
     * @return {@code true} <em>iff</em> {@code packet} contains a {@code TcpPacket} for which the SYN flag is set,
     *         {@code false} otherwise.
     */
    public static boolean isSyn(PcapPacket packet) {
        TcpPacket tcp = packet.get(TcpPacket.class);
        return tcp != null && tcp.getHeader().getSyn();
    }

    /**
     * Checks if {@code packet} wraps a TCP packet that has the ACK flag set.
     * @param packet A {@link PcapPacket} that is suspected to contain a {@link TcpPacket} for which the ACK flag is set.
     * @return {@code true} <em>iff</em> {@code packet} contains a {@code TcpPacket} for which the ACK flag is set,
     *         {@code false} otherwise.
     */
    public static boolean isAck(PcapPacket packet) {
        TcpPacket tcp = packet.get(TcpPacket.class);
        return tcp != null && tcp.getHeader().getAck();
    }

    /**
     * Transform a {@code Cluster} of {@code PcapPacketPair} objects into a {@code List} of {@code List} of
     * {@code PcapPacket} objects.
     * @param cluster A {@link Cluster} of {@link PcapPacketPair} objects that needs to be transformed.
     * @return A {@link List} of {@link List} of {@link PcapPacket} objects as the result of the transformation.
     */
    public static List<List<PcapPacket>> clusterToListOfPcapPackets(Cluster<PcapPacketPair> cluster) {
        List<List<PcapPacket>> ppListOfList = new ArrayList<>();
        for (PcapPacketPair ppp: cluster.getPoints()) {
            // Create a list of PcapPacket objects (list of two members).
            List<PcapPacket> ppList = new ArrayList<>();
            ppList.add(ppp.getFirst());
            if(ppp.getSecond().isPresent())
                ppList.add(ppp.getSecond().get());
            else
                ppList.add(null);
            // Create a list of list of PcapPacket objects.
            ppListOfList.add(ppList);
        }
        // Sort the list of lists based on the first packet's timestamp!
        Collections.sort(ppListOfList, (p1, p2) -> p1.get(0).getTimestamp().compareTo(p2.get(0).getTimestamp()));
        return ppListOfList;
    }

    /**
     * Merge signatures in {@code List} of {@code List} of {@code List} of {@code PcapPacket} objects.
     * We cross-check these with {@code List} of {@code Conversation} objects to see
     * if two {@code List} of {@code PcapPacket} objects actually belong to the same {@code Conversation}.
     * @param signatures A {@link List} of {@link List} of {@link List} of
     *          {@link PcapPacket} objects that needs to be checked and merged.
     * @param conversations A {@link List} of {@link Conversation} objects as reference for merging.
     * @return A {@link List} of {@link List} of {@link List} of
     *          {@link PcapPacket} objects as the result of the merging.
     */
    public static List<List<List<PcapPacket>>>
            mergeSignatures(List<List<List<PcapPacket>>> signatures, List<Conversation> conversations) {
        // Make a copy first.
        List<List<List<PcapPacket>>> copySignatures = new ArrayList<>(signatures);
        // Traverse and look into the pairs of signatures.
        for (int first = 0; first < signatures.size(); first++) {
            List<List<PcapPacket>> firstList = signatures.get(first);
            for (int second = first+1; second < signatures.size(); second++) {
                int maxSignatureEl = 0; // Number of maximum signature elements.
                List<List<PcapPacket>> secondList = signatures.get(second);
                int initialSecondListMembers = secondList.size();
                // Iterate over the signatures in the first list.
                for (List<PcapPacket> signature : firstList) {
                    signature.removeIf(el -> el == null); // Clean up null elements.
                    // Return the Conversation that the signature is part of.
                    Conversation conv = TcpConversationUtils.returnConversation(signature, conversations);
                    // Find the element of the second list that is a match for that Conversation.
                    for (List<PcapPacket> ppList : secondList) {
                        ppList.removeIf(el -> el == null); // Clean up null elements.
                        // Check if they are part of a Conversation and are adjacent to the first signature.
                        // If yes then merge into the first list.
                        TcpConversationUtils.SignaturePosition position =
                                TcpConversationUtils.isPartOfConversationAndAdjacent(signature, ppList, conv);
                        if (position == TcpConversationUtils.SignaturePosition.LEFT_ADJACENT) {
                            // Merge to the left side of the first signature.
                            ppList.addAll(signature);
                            signature = ppList;
                            maxSignatureEl = signature.size() > maxSignatureEl ? signature.size() : maxSignatureEl;
                            secondList.remove(ppList); // Remove as we merge.
                            break;
                        } else if (position == TcpConversationUtils.SignaturePosition.RIGHT_ADJACENT) {
                            // Merge to the right side of the first signature.
                            signature.addAll(ppList);
                            maxSignatureEl = signature.size() > maxSignatureEl ? signature.size() : maxSignatureEl;
                            secondList.remove(ppList); // Remove as we merge.
                            break;
                        } // TcpConversationUtils.SignaturePosition.NOT_ADJACENT.
                    }
                }
                // Call it a successful merging if there are only less than 5 elements from the second list that
                // cannot be merged.
                if (secondList.size() < SIGNATURE_MERGE_THRESHOLD) {
                    // Prune the unsuccessfully merged signatures (i.e., these will have size() < maxSignatureEl).
                    final int maxNumOfEl = maxSignatureEl;
                    firstList.removeIf(el -> el.size() < maxNumOfEl);
                    // Remove the merged set of signatures when successful.
                    signatures.remove(secondList);
                } else if (secondList.size() < initialSecondListMembers) {
                    // If only some of the signatures from the second list are merged, this means UNSUCCESSFUL merging.
                    // Return the original copy of the signatures object.
                    return copySignatures;
                }
            }
        }
        return signatures;
    }

    /**
     * Sort the signatures in the {@code List} of {@code List} of {@code List} of {@code PcapPacket} objects.
     * The purpose of this is to sort the order of signatures in the signature list. For detection purposes, we need
     * to know if one signature occurs earlier/later in time with respect to the other signatures for more confidence
     * in detecting the occurrence of an event.
     * @param signatures A {@code List} of {@code List} of {@code List} of {@code PcapPacket} objects that needs sorting.
     *                   We assume that innermost {@code List} of {@code PcapPacket} objects have been sorted ascending
     *                   by timestamps. By the time we use this method, we should have sorted it when calling the
     *                   {@code clusterToListOfPcapPackets} method.
     * @return A sorted {@code List} of {@code List} of {@code List} of {@code PcapPacket} objects.
     */
    public static List<List<List<PcapPacket>>> sortSignatures(List<List<List<PcapPacket>>> signatures) {
        // TODO: This is the simplest solution!!! Might not cover all corner cases.
        // TODO: Sort the list of lists based on the first packet's timestamps!
        //Collections.sort(signatures, (p1, p2) -> {
        //    return p1.get(0).get(0).getTimestamp().compareTo(p2.get(0).get(0).getTimestamp());
        //});
        // TODO: The following is a more complete solution that covers corner cases.
        // Sort the list of lists based on one-to-one comparison between timestamps of signatures on both lists.
        // This also takes into account the fact that the number of signatures in the two lists could be different.
        // Additionally, this code forces the comparison between two signatures only if they occur in the
        // INCLUSION_WINDOW_MILLIS window; otherwise, it tries to find the right pair of signatures in the time window.
        Collections.sort(signatures, (p1, p2) -> {
            int compare = 0;
            int comparePrev = 0;
            int count1 = 0;
            int count2 = 0;
            // Need to make sure that both are not out of bound!
            while (count1 + 1 < p1.size() && count2 + 1 < p2.size()) {
                long timestamp1 = p1.get(count1).get(0).getTimestamp().toEpochMilli();
                long timestamp2 = p2.get(count2).get(0).getTimestamp().toEpochMilli();
                // The two timestamps have to be within a 15-second window!
                if (Math.abs(timestamp1 - timestamp2) < TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS) {
                    // If these two are within INCLUSION_WINDOW_MILLIS window then compare!
                    compare = p1.get(count1).get(0).getTimestamp().compareTo(p2.get(count2).get(0).getTimestamp());
                    if (comparePrev != 0) { // First time since it is 0
                        if (Integer.signum(compare) != Integer.signum(comparePrev)) {
                            // Throw an exception if the order of the two signatures is not consistent,
                            // E.g., 111, 222, 333 in one occassion and 222, 333, 111 in the other.
                            throw new Error("For some reason, the order of signatures are not always consistent!" +
                                    "Returning the original data structure of signatures...");
                        }
                    }
                    comparePrev = compare;
                    count1++;
                    count2++;
                } else {
                    // If not within INCLUSION_WINDOW_MILLIS window then find the correct pair
                    // by incrementing one of them.
                    if (timestamp1 < timestamp2)
                        count1++;
                    else
                        count2++;
                }
            }
            return compare;
        });
        return signatures;
    }

    /**
     * Gets the {@link IpV4Packet} contained in {@code packet}, or throws a {@link NullPointerException} if
     * {@code packet} does not contain an {@link IpV4Packet}.
     * @param packet A {@link PcapPacket} that is expected to contain a {@link IpV4Packet}.
     * @return The {@link IpV4Packet} contained in {@code packet}.
     * @throws NullPointerException if {@code packet} does not encapsulate an {@link IpV4Packet}.
     */
    private static IpV4Packet getIpV4PacketOrThrow(PcapPacket packet) {
        return Objects.requireNonNull(packet.get(IpV4Packet.class), "not an IPv4 packet");
    }
}
