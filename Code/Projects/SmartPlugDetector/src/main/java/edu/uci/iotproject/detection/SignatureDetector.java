package edu.uci.iotproject.detection;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.TcpReassembler;
import edu.uci.iotproject.analysis.TcpConversationUtils;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static edu.uci.iotproject.util.PcapPacketUtils.*;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class SignatureDetector implements PacketListener {

    /**
     * The signature that this {@link SignatureDetector} is trying to detect in the observed traffic.
     */
    private final List<List<PcapPacket>> mSignature;

    /**
     * The directions of packets in the sequence that make up {@link #mSignature}.
     */
    private final Conversation.Direction[] mSignatureDirections;

    /**
     * For reassembling the observed traffic into TCP connections.
     */
    private final TcpReassembler mTcpReassembler = new TcpReassembler();

    /**
     * IP of the router's WAN port (if analyzed traffic is captured at the ISP's point of view).
     */
    private final String mRouterWanIp;

    public SignatureDetector(List<List<PcapPacket>> signature, String routerWanIp) {
        Objects.requireNonNull(signature, "signature cannot be null");
        if (signature.isEmpty() || signature.stream().anyMatch(inner -> inner.isEmpty())) {
            throw new IllegalArgumentException("signature is empty (or contains empty inner List)");
        }
        mSignature = signature;
        mRouterWanIp = routerWanIp;
        // Build the direction sequence.
        // Note: assumes that the provided signature was captured within the local network (routerWanIp is set to null).
        mSignatureDirections = getPacketDirections(mSignature.get(0), null);
        /*
         * Enforce restriction on cluster/signature members: all representatives must exhibit the same direction pattern
         * and contain the same number of packets. Note that this is a somewhat heavy operation, so it may be disabled
         * later on in favor of performance. However, it is only run once (at instantiation), so the overhead may be
         * warranted in order to ensure correctness, especially during the development/debugging phase.
         */
        if (mSignature.stream().
                anyMatch(inner -> !Arrays.equals(mSignatureDirections, getPacketDirections(inner, null)))) {
            throw new IllegalArgumentException(
                    "signature members must contain the same number of packets and exhibit the same packet direction " +
                            "pattern"
            );
        }
    }

    @Override
    public void gotPacket(PcapPacket packet) {
        // Present packet to TCP reassembler so that it can be mapped to a connection (if it is a TCP packet).
        mTcpReassembler.gotPacket(packet);

    }

    private void performDetection() {
        // Let's start out simple by building a version that only works for signatures that do not span across multiple
        // TCP conversations...
        for (Conversation c : mTcpReassembler.getTcpConversations()) {
            for (List<PcapPacket> sequence : mSignature) {
                boolean matchFound = isSequenceInConversation(sequence, c);
                if (matchFound) {
                    onSequenceDetected(sequence, c);
                    // Found signature in current conversation, so break inner loop and continue with next conversation.
                    break;
                }
            }
            /*
             * TODO:
             * if no item in cluster matches, also perform a distance-based matching to cover those cases where we did
             * not manage to capture every single mutation of the sequence during training.
             *
             * Need to compute average/centroid of cluster to do so...? Compute within-cluster variance, then check if
             * distance between input conversation and cluster average/centroid is smaller than or equal to the computed
             * variance?
             */
        }
    }

    private void onSequenceDetected(List<PcapPacket> sequence, Conversation c) {
        // TODO implement whatever output we want, e.g., print to std.out or notify observer objects


    }

    /**
     * Examine if a {@link Conversation} contains a given sequence of packets. Note: the current implementation actually
     * searches for a substring as it does not allow for interleaved packets in {@code c} that are not in
     * {@code sequence}; for example, if {@code sequence} consists of packet lengths [2, 3, 5] and {@code c} consists of
     * packet lengths [2, 3, 4, 5], the result will be {@code false}. If we are to allow interleaved packets, we need
     * a modified version of <a href="https://stackoverflow.com/a/20545604/1214974">this</a>.
     * @param sequence The sequence to look for.
     * @param c The {@link Conversation} to search for {@code sequence} in.
     * @return {@code true} if {@code c} contains {@code sequence}, {@code false} otherwise.
     */
    private boolean isSequenceInConversation(List<PcapPacket> sequence, Conversation c) {
        // The packets we match against differ depending on whether the signature is a TLS or non-TLS signature.
        boolean tlsSequence = isTlsSequence(sequence);
        if (tlsSequence && !c.isTls()) {
            // If we're looking for a TLS signature and this conversation does not appear to be a TLS conversation, we
            // are done. Note: this assumes that they do NOT start performing TLS on new ports that are not captured in
            // Conversation.isTls()
            return false;
        }
        // Based on TLS or non-TLS signature, fetch the corresponding list of packets to match against.
        List<PcapPacket> packets = tlsSequence ? c.getTlsApplicationDataPackets() : c.getPackets();
        // If sequence is longer than the conversation, it can obviously not be contained in the conversation.
        if (packets.size() < sequence.size()) {
            return false;
        }
        /*
         * Generate packet direction array for c. We have already generated the packet direction array for sequence as
         * part of the constructor (mSignatureDirections).
         */
        Conversation.Direction[] cDirections = getPacketDirections(packets, mRouterWanIp);
        int seqIdx = 0;
        int convIdx = 0;
        while (convIdx < packets.size()) {
            PcapPacket seqPkt = sequence.get(seqIdx);
            PcapPacket convPkt = packets.get(convIdx);
            // We only have a match if packet lengths and directions match.
            if (convPkt.getOriginalLength() == seqPkt.getOriginalLength() &&
                    mSignatureDirections[seqIdx] == cDirections[convIdx]) {
                // A match, advance both indices to consider next packet in sequence vs. next packet in conversation
                seqIdx++;
                convIdx++;
                if (seqIdx == sequence.size()) {
                    // we managed to match the full sequence in the conversation.
                    return true;
                }
            } else {
                // Mismatch.
                if (seqIdx > 0) {
                    /*
                     * If we managed to match parts of sequence, we restart the search for sequence in c at the index of
                     * c where the current mismatch occurred. I.e., we must reset seqIdx, but leave convIdx untouched.
                     */
                    seqIdx = 0;
                } else {
                    /*
                     * First packet of sequence didn't match packet at convIdx of conversation, so we move forward in
                     * conversation, i.e., we continue the search for sequence in c starting at index convIdx+1 of c.
                     */
                    convIdx++;
                }
            }
        }
        return false;
    }

    private boolean isTlsSequence(List<PcapPacket> sequence) {
        // NOTE: Assumes ALL packets in sequence pertain to the same TCP connection!
        PcapPacket firstPkt = sequence.get(0);
        int srcPort = getSourcePort(firstPkt);
        int dstPort = getDestinationPort(firstPkt);
        return TcpConversationUtils.isTlsPort(srcPort) || TcpConversationUtils.isTlsPort(dstPort);
    }

    /**
     * Given a {@code List<PcapPacket>}, generate a {@code Conversation.Direction[]} such that each entry in the
     * resulting {@code Conversation.Direction[]} specifies the direction of the {@link PcapPacket} at the corresponding
     * index in the input list.
     * @param packets The list of packets for which to construct a corresponding array of packet directions.
     * @param routerWanIp The IP of the router's WAN port. This is used for determining the direction of packets when
     *                    the traffic is captured just outside the local network (at the ISP side of the router). Set to
     *                    {@code null} if {@code packets} stem from traffic captured within the local network.
     * @return A {@code Conversation.Direction[]} specifying the direction of the {@link PcapPacket} at the
     *         corresponding index in {@code packets}.
     */
    private static Conversation.Direction[] getPacketDirections(List<PcapPacket> packets, String routerWanIp) {
        Conversation.Direction[] directions = new Conversation.Direction[packets.size()];
        for (int i = 0; i < packets.size(); i++) {
            PcapPacket pkt = packets.get(i);
            if (getSourceIp(pkt).equals(getDestinationIp(pkt))) {
                // Sanity check: we shouldn't be processing loopback traffic
                throw new AssertionError("loopback traffic detected");
            }
            if (isSrcIpLocal(pkt) || getSourceIp(pkt).equals(routerWanIp)) {
                directions[i] = Conversation.Direction.CLIENT_TO_SERVER;
            } else if (isDstIpLocal(pkt) || getDestinationIp(pkt).equals(routerWanIp)) {
                directions[i] = Conversation.Direction.SERVER_TO_CLIENT;
            } else {
                throw new IllegalArgumentException("no local IP or router WAN port IP found, can't detect direction");
            }
        }
        return directions;
    }

}
