package edu.uci.iotproject.util;

import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.Objects;

/**
 * Utility methods for inspecting {@link PcapPacket} properties.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public final class PcapPacketUtils {

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

}
