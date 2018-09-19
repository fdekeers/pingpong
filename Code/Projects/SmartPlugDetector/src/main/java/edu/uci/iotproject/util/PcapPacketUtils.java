package edu.uci.iotproject.util;

import edu.uci.iotproject.analysis.PcapPacketPair;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility methods for inspecting {@link PcapPacket} properties.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public final class PcapPacketUtils {

    /**
     * Gets the source IP (in decimal format) of an IPv4 packet.
     * @param packet The packet for which the IPv4 source address is to be extracted.
     * @return The decimal representation of the source IP of {@code packet} <em>iff</em> {@code packet} wraps an
     *         {@link IpV4Packet}, otherwise {@code null}.
     */
    public static String getSourceIp(PcapPacket packet) {
        IpV4Packet ipPacket = packet.get(IpV4Packet.class);
        return ipPacket == null ? null : ipPacket.getHeader().getSrcAddr().getHostAddress();
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
            // Create a list of PcapPacket objects (list of two members)
            List<PcapPacket> ppList = new ArrayList<>();
            ppList.add(ppp.getFirst());
            ppList.add(ppp.getSecond().get());
            // Create a list of list of PcapPacket objects
            ppListOfList.add(ppList);
        }

        return ppListOfList;
    }

}
