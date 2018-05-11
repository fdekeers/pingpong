package edu.uci.iotproject.util;

import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.Objects;

/**
 * Utility methods for inspecting {@link PcapPacket} properties. Currently not used.
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

}
