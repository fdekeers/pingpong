package edu.uci.iotproject;

import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.*;

/**
 * Models a (TCP) conversation/connection/session/flow (packet's belonging to the same session between a client and a
 * server).
 * Holds a list of {@link PcapPacket}s identified as pertaining to the flow. Note that this list is <em>not</em>
 * considered when determining equality of two {@code Conversation} instances in order to allow for a
 * {@code Conversation} to function as a key in data structures such as {@link java.util.Map} and {@link java.util.Set}.
 * See {@link #equals(Object)} for the definition of equality.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class Conversation {

    /* Begin instance properties */
    /**
     * The IP of the host that is considered the client (i.e. the host that initiates the conversation)
     * in this conversation.
     */
    private final String mClientIp;

    /**
     * The port number used by the host that is considered the client in this conversation.
     */
    private final int mClientPort;

    /**
     * The IP of the host that is considered the server (i.e. is the responder) in this conversation.
     */
    private final String mServerIp;

    /**
     * The port number used by the server in this conversation.
     */
    private final int mServerPort;

    /**
     * The list of packets pertaining to this conversation.
     */
    private final List<PcapPacket> mPackets;

    /**
     * Contains the sequence numbers seen so far for this {@code Conversation}.
     * Used for filtering out retransmissions.
     */
    private final Set<Integer> mSeqNumbers;

    /**
     * List of pairs FINs and their corresponding ACKs associated with this conversation.
     */
    private List<FinAckPair> mFinPackets;
    /* End instance properties */

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
        this.mSeqNumbers = new HashSet<>();
        this.mFinPackets = new ArrayList<>();
    }

    /**
     * Add a packet to the list of packets associated with this conversation.
     * @param packet The packet that is to be added to (associated with) this conversation.
     * @param ignoreRetransmissions Boolean value indicating if retransmissions should be ignored.
     *                              If set to {@code true}, {@code packet} will <em>not</em> be added to the
     *                              internal list of packets pertaining to this {@code Conversation}
     *                              <em>iff</em> the sequence number of {@code packet} was already
     *                              seen in a previous packet.
     */
    public void addPacket(PcapPacket packet, boolean ignoreRetransmissions) {
        // Precondition: verify that packet does indeed pertain to conversation.
        onAddPrecondition(packet);
        // For now we only support TCP flows.
        TcpPacket tcpPacket = Objects.requireNonNull(packet.get(TcpPacket.class));
        int seqNo = tcpPacket.getHeader().getSequenceNumber();
        if (ignoreRetransmissions && mSeqNumbers.contains(seqNo)) {
            // Packet is a retransmission. Ignore it.
            return;
        }
        // Update set of sequence numbers seen so far with sequence number of new packet.
        mSeqNumbers.add(seqNo);
        // Finally add packet to list of packets pertaining to this conversation.
        mPackets.add(packet);
    }

    /**
     * Adds a TCP FIN packet to the list of TCP FIN packets associated with this conversation.
     * @param finPacket The TCP FIN packet that is to be added to (associated with) this conversation.
     */
    public void addFinPacket(PcapPacket finPacket) {
        // Precondition: verify that packet does indeed pertain to conversation.
        onAddPrecondition(finPacket);
        mFinPackets.add(new FinAckPair(finPacket));
    }

    /**
     * Attempt to ACK any FIN packets held by this conversation.
     * @param ackPacket The ACK for a FIN previously added to this conversation.
     */
    public void attemptAcknowledgementOfFin(PcapPacket ackPacket) {
        // Precondition: verify that the packet pertains to this conversation.
        onAddPrecondition(ackPacket);
        // Mark unack'ed FIN(s) that this ACK matches as ACK'ed (there might be more than one in case of retransmissions..?)
        mFinPackets.replaceAll(finAckPair -> (!finAckPair.isAcknowledged() && finAckPair.isCorrespondingAckPacket(ackPacket)) ? new FinAckPair(finAckPair.getFinPacket(), ackPacket) : finAckPair);
    }

    /**
     * Get a list of packets pertaining to this {@code Conversation}.
     * The returned list is a read-only list.
     * @return the list of packets pertaining to this {@code Conversation}.
     */
    public List<PcapPacket> getPackets() {
        // Return read-only view to prevent external code from manipulating internal state (preserve invariant).
        return Collections.unmodifiableList(mPackets);
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

    /**
     * Invoke to verify that the precondition holds when a caller attempts to add a packet to this {@code Conversation}.
     * An {@link IllegalArgumentException} is thrown if the precondition is violated.
     * @param packet the packet to be added to this {@code Conversation}
     */
    private void onAddPrecondition(PcapPacket packet) {
        // Apply precondition to preserve class invariant: all packets in mPackets must match the 4 tuple that
        // defines the conversation.
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
    }

}