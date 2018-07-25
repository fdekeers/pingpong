package edu.uci.iotproject.analysis;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.DnsMap;
import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.*;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class TcpConversationUtils {

    public static List<PcapPacketPair> extractPacketPairs(Conversation conv) {
        List<PcapPacket> packets = conv.getPackets();
        List<PcapPacketPair> pairs = new ArrayList<>();
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


    public static Map<String, List<Conversation>> groupConversationsByHostname(List<Conversation> tcpConversations, DnsMap ipHostnameMappings) {
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


}
