package edu.uci.iotproject;

import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Models a (TCP) conversation/connection/session/flow (packet's belonging to the same session between a client and a
 * server).
 * Holds a list of {@link PcapPacket}s identified as pertaining to the flow. Note that this list is <em>not</em>
 * considered when determining equality of two {@code Conversation} instances in order to allow for a
 * {@code Conversation} to function as a key in data structures such as {@link java.util.Map} and {@link java.util.Set}.
 * See {@link #equals(Object)} for the definition of equality.
 */
public class Conversation {

    private final String mClientIp;
    private final int mClientPort;
    private final String mServerIp;
    private final int mServerPort;
    private final List<PcapPacket> mPackets;

    /**
     * Constructs a new {@code Conversation}.
     * @param clientIp The IP of the host that is considered the client (i.e. the host that initiates the conversation)
     *                 in the conversation.
     * @param clientPort The port number used by the client for the conversation.
     * @param serverIp The IP of the host that is considered the server (i.e. is the responder) in the conversation.
     * @param serverPort The port number used by the server for the conversation.
     */
    public Conversation(String clientIp, int clientPort, String serverIp, int serverPort) {
        this.mClientIp = clientIp;
        this.mClientPort = clientPort;
        this.mServerIp = serverIp;
        this.mServerPort = serverPort;
        this.mPackets = new ArrayList<>();
    }

    /**
     * Add a packet to the list of packets associated with this conversation.
     * @param packet The packet that is to be added to (associated with) this conversation.
     */
    public void addPacket(PcapPacket packet) {
        // Apply precondition to preserve class invariant: all packets in mPackets must match the 4 tuple that
        // defines the conversation.
        // ==== Precondition: verify that packet does indeed pertain to conversation. ====
        IpV4Packet ipPacket = Objects.requireNonNull(packet.get(IpV4Packet.class));
        // For now we only support TCP flows.
        TcpPacket tcpPacket = Objects.requireNonNull(packet.get(TcpPacket.class));
        String ipSrc = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String ipDst = ipPacket.getHeader().getDstAddr().getHostAddress();
        int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
        int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
        String clientIp, serverIp;
        int clientPort, serverPort;
        if (ipSrc.equals(mClientIp)) {
            clientIp = ipSrc;
            clientPort = srcPort;
            serverIp = ipDst;
            serverPort = dstPort;
        } else {
            clientIp = ipDst;
            clientPort = dstPort;
            serverIp = ipSrc;
            serverPort = srcPort;
        }
        if (!(clientIp.equals(mClientIp) && clientPort == mClientPort &&
                serverIp.equals(mServerIp) && serverPort == mServerPort)) {
            throw new IllegalArgumentException(
                    String.format("Attempt to add packet that does not pertain to %s",
                            Conversation.class.getSimpleName()));
        }
        // ================================================================================
        mPackets.add(packet);
    }

    // =========================================================================================================
    // We simply reuse equals and hashCode methods of String.class to be able to use this class as a key
    // in a Map.

    /**
     * <em>Note:</em> currently, equality is determined based on pairwise equality of the elements of the four tuple
     * ({@link #mClientIp}, {@link #mClientPort}, {@link #mServerIp}, {@link #mServerPort}) for {@code this} and
     * {@code obj}.
     * @param obj The object to test for equality with {@code this}.
     * @return {@code true} if {@code obj} is considered equal to {@code this} based on the definition of equality given above.
     */
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
        return String.format("%s:%d %s:%d", mClientIp, mClientPort, mServerIp, mServerPort);
    }
}