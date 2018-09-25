package edu.uci.iotproject.detection;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.TcpReassembler;
import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.io.PcapHandleReader;
import edu.uci.iotproject.util.PrintUtils;
import org.pcap4j.core.*;

import java.util.*;

import static edu.uci.iotproject.util.PcapPacketUtils.*;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class SignatureDetector implements PacketListener {

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        // Test client
//        String path = "/scratch/July-2018"; // Rahmadi
        String path = "/Users/varmarken/temp/UCI IoT Project/experiments"; // Janus
        final String inputPcapFile = path + "/2018-07/dlink/dlink.wlan1.local.pcap";
        final String signatureFile = path + "/2018-07/dlink/offSignature1.sig";
//        final String outputPcapFile = path + "/2018-07/dlink/dlink-processed.pcap";
//        final String triggerTimesFile = path + "/2018-07/dlink/dlink-july-26-2018.timestamps";
//        final String deviceIp = "192.168.1.199"; // .246 == phone; .199 == dlink plug?

        List<List<PcapPacket>> signature = PrintUtils.serializeClustersFromFile(signatureFile);
        SignatureDetector signatureDetector = new SignatureDetector(signature, null,
                (sig, match) -> System.out.println(
                        String.format("[ !!! SIGNATURE DETECTED AT %s !!! ]", match.get(0).getTimestamp().toString())
                )
        );


        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(inputPcapFile, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(inputPcapFile);
        }
        PcapHandleReader reader = new PcapHandleReader(handle, p -> true, signatureDetector);
        reader.readFromHandle();
        signatureDetector.performDetection();
    }

    /**
     * The signature that this {@link SignatureDetector} is trying to detect in the observed traffic.
     */
    private final List<List<PcapPacket>> mSignature;

    /**
     * The directions of packets in the sequences that make up {@link #mSignature}.
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

    private final Observer[] mObservers;

    public SignatureDetector(List<List<PcapPacket>> signature, String routerWanIp, Observer... detectionObservers) {
        mSignature = Collections.unmodifiableList(Objects.requireNonNull(signature, "signature cannot be null"));
        mObservers = Objects.requireNonNull(detectionObservers, "detectionObservers cannot be null");
        if (mSignature.isEmpty() || mSignature.stream().anyMatch(inner -> inner.isEmpty())) {
            throw new IllegalArgumentException("signature is empty (or contains empty inner List)");
        }
        if (mObservers.length == 0) {
            throw new IllegalArgumentException("no detectionObservers provided");
        }
        mRouterWanIp = routerWanIp;
        // Build the signature's direction sequence.
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


//    public void performDetection() {
//        // Let's start out simple by building a version that only works for signatures that do not span across multiple
//        // TCP conversations...
//        for (Conversation c : mTcpReassembler.getTcpConversations()) {
//            for (List<PcapPacket> sequence : mSignature) {
//                boolean matchFound = isSequenceInConversation(sequence, c);
//                if (matchFound) {
//                    for (Observer obs : mObservers) {
//                        obs.onSequenceDetected(sequence, c);
//                    }
//                    // Found signature in current conversation, so break inner loop and continue with next conversation.
//                    // TODO: signature can be present more than once in Conversation...
//                    break;
//                }
//            }
//            /*
//             * TODO:
//             * if no item in cluster matches, also perform a distance-based matching to cover those cases where we did
//             * not manage to capture every single mutation of the sequence during training.
//             *
//             * Need to compute average/centroid of cluster to do so...? Compute within-cluster variance, then check if
//             * distance between input conversation and cluster average/centroid is smaller than or equal to the computed
//             * variance?
//             */
//        }
//    }


    public void performDetection() {
        /*
         * Let's start out simple by building a version that only works for signatures that do not span across multiple
         * TCP conversations...
         */
        for (Conversation c : mTcpReassembler.getTcpConversations()) {
            if (c.isTls() && c.getTlsApplicationDataPackets().isEmpty() || !c.isTls() && c.getPackets().isEmpty()) {
                // Skip empty conversations.
                continue;
            }
            for (List<PcapPacket> signatureSequence : mSignature) {
                if (isTlsSequence(signatureSequence) != c.isTls()) {
                    // We consider it a mismatch if one is a TLS application data sequence and the other is not.
                    continue;
                }
                // Fetch set of packets to examine based on TLS or not.
                List<PcapPacket> cPkts = c.isTls() ? c.getTlsApplicationDataPackets() : c.getPackets();
                /*
                 * Note: since we expect all sequences that together make up the signature to exhibit the same direction
                 * pattern, we can simply pass the precomputed direction array for the signature sequence so that it
                 * won't have to be recomputed internally in each call to findSubsequenceInSequence().
                 */
                Optional<List<PcapPacket>> match =
                        findSubsequenceInSequence(signatureSequence, cPkts, mSignatureDirections, null);
                match.ifPresent(ps -> Arrays.stream(mObservers).forEach(o -> o.onSignatureDetected(mSignature, ps)));
                if (match.isPresent()) {
                    /*
                     * We found an element in the signature cluster that was present in conversation, so no need to scan
                     * conversation for remaining members of signature cluster (in fact, we'd be getting duplicate
                     * output in those cases where the cluster is made up of identical sequences if we did not stop the
                     * search here).
                     *
                     * TODO:
                     * How do we handle those cases where the conversation matches the signature more than once (for
                     * example, the long-lived connections used for sending the trigger from the cloud)?
                     */
                    break;
                }
            }
        }
    }

//    /**
//     * Examine if a {@link Conversation} contains a given sequence of packets. Note: the current implementation actually
//     * searches for a substring as it does not allow for interleaved packets in {@code c} that are not in
//     * {@code sequence}; for example, if {@code sequence} consists of packet lengths [2, 3, 5] and {@code c} consists of
//     * packet lengths [2, 3, 4, 5], the result will be {@code false}. If we are to allow interleaved packets, we need
//     * a modified version of <a href="https://stackoverflow.com/a/20545604/1214974">this</a>.
//     * @param sequence The sequence to look for.
//     * @param c The {@link Conversation} to search for {@code sequence} in.
//     * @return {@code true} if {@code c} contains {@code sequence}, {@code false} otherwise.
//     */
//    private boolean isSequenceInConversation(List<PcapPacket> sequence, Conversation c) {
//        // TODO add offset argument to allow looking for sequence starting later in Conversation.
//        // The packets we match against differ depending on whether the signature is a TLS or non-TLS signature.
//        boolean tlsSequence = isTlsSequence(sequence);
//        if (tlsSequence && !c.isTls()) {
//            // If we're looking for a TLS signature and this conversation does not appear to be a TLS conversation, we
//            // are done. Note: this assumes that they do NOT start performing TLS on new ports that are not captured in
//            // Conversation.isTls()
//            return false;
//        }
//        // Based on TLS or non-TLS signature, fetch the corresponding list of packets to match against.
//        List<PcapPacket> packets = tlsSequence ? c.getTlsApplicationDataPackets() : c.getPackets();
//        // If sequence is longer than the conversation, it can obviously not be contained in the conversation.
//        if (packets.size() < sequence.size()) {
//            return false;
//        }
//        /*
//         * Generate packet direction array for c. We have already generated the packet direction array for sequence as
//         * part of the constructor (mSignatureDirections).
//         */
//        Conversation.Direction[] cDirections = getPacketDirections(packets, mRouterWanIp);
//        int seqIdx = 0;
//        int convIdx = 0;
//        while (convIdx < packets.size()) {
//            PcapPacket seqPkt = sequence.get(seqIdx);
//            PcapPacket convPkt = packets.get(convIdx);
//            // We only have a match if packet lengths and directions match.
//            if (convPkt.getOriginalLength() == seqPkt.getOriginalLength() &&
//                    mSignatureDirections[seqIdx] == cDirections[convIdx]) {
//                // A match, advance both indices to consider next packet in sequence vs. next packet in conversation
//                seqIdx++;
//                convIdx++;
//                if (seqIdx == sequence.size()) {
//                    // we managed to match the full sequence in the conversation.
//                    return true;
//                }
//            } else {
//                // Mismatch.
//                if (seqIdx > 0) {
//                    /*
//                     * If we managed to match parts of sequence, we restart the search for sequence in c at the index of
//                     * c where the current mismatch occurred. I.e., we must reset seqIdx, but leave convIdx untouched.
//                     */
//                    seqIdx = 0;
//                } else {
//                    /*
//                     * First packet of sequence didn't match packet at convIdx of conversation, so we move forward in
//                     * conversation, i.e., we continue the search for sequence in c starting at index convIdx+1 of c.
//                     */
//                    convIdx++;
//                }
//            }
//        }
//        return false;
//    }

    private boolean isTlsSequence(List<PcapPacket> sequence) {
        // NOTE: Assumes ALL packets in sequence pertain to the same TCP connection!
        PcapPacket firstPkt = sequence.get(0);
        int srcPort = getSourcePort(firstPkt);
        int dstPort = getDestinationPort(firstPkt);
        return TcpConversationUtils.isTlsPort(srcPort) || TcpConversationUtils.isTlsPort(dstPort);
    }

//    private List<PcapPacket> findeSequenceInConversation(List<PcapPacket> sequence, Conversation conv, int offset) {
//        if (isTlsSequence(sequence) != conv.isTls()) {
//            // We consider it a mismatch if one is a TLS Application Data sequence and the other is not.
//            return null;
//        }
//        List<PcapPacket> convPackets = conv.isTls() ? conv.getTlsApplicationDataPackets() : conv.getPackets();
//
//    }

    private Optional<List<PcapPacket>> findSubsequenceInSequence(List<PcapPacket> subsequence,
                                                                 List<PcapPacket> sequence,
                                                                 Conversation.Direction[] subsequenceDirections,
                                                                 Conversation.Direction[] sequenceDirections) {
        if (isTlsSequence(subsequence) != isTlsSequence(sequence)) {
            // We consider it a mismatch if one is a TLS application data sequence and the other is not.
            return Optional.empty();
        }
        if (sequence.size() < subsequence.size()) {
            // If subsequence is longer, it cannot be contained in sequence.
            return Optional.empty();
        }
        // If packet directions have not been precomputed by calling code, we need to construct them.
        if (subsequenceDirections == null) {
            subsequenceDirections = getPacketDirections(subsequence, mRouterWanIp);
        }
        if (sequenceDirections == null) {
            sequenceDirections = getPacketDirections(sequence, mRouterWanIp);
        }
        int subseqIdx = 0;
        int seqIdx = 0;
        while (seqIdx < sequence.size()) {
            PcapPacket subseqPkt = subsequence.get(subseqIdx);
            PcapPacket seqPkt = sequence.get(seqIdx);
            // We only have a match if packet lengths and directions match.
            if (subseqPkt.getOriginalLength() == seqPkt.getOriginalLength() &&
                    subsequenceDirections[subseqIdx] == sequenceDirections[seqIdx]) {
                // A match; advance both indices to consider next packet in subsequence vs. next packet in sequence.
                subseqIdx++;
                seqIdx++;
                if (subseqIdx == subsequence.size()) {
                    // We managed to match the entire subsequence in sequence.
                    // Return the sublist of sequence that matches subsequence.
                    /*
                     * TODO:
                     * ASSUMES THE BACKING LIST (i.e., 'sequence') IS _NOT_ STRUCTURALLY MODIFIED, hence may not work
                     * for live traces!
                     */
                    return Optional.of(sequence.subList(seqIdx - subsequence.size(), seqIdx));
                }
            } else {
                // Mismatch.
                if (subseqIdx > 0) {
                    /*
                     * If we managed to match parts of subsequence, we restart the search for subsequence in sequence at
                     * the index of sequence where the current mismatch occurred. I.e., we must reset subseqIdx, but
                     * leave seqIdx untouched.
                     */
                    subseqIdx = 0;
                } else {
                    /*
                     * First packet of subsequence didn't match packet at seqIdx of sequence, so we move forward in
                     * sequence, i.e., we continue the search for subsequence in sequence starting at index seqIdx+1 of
                     * sequence.
                     */
                    seqIdx++;
                }
            }
        }
        return Optional.empty();
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

    interface Observer {
//        /**
//         * Callback that is invoked when a sequence associated with the signature/cluster (i.e., the sequence is a
//         * member of the cluster that makes up the signature) is detected in a {@link Conversation}.
//         * @param sequence The sequence that was detected in {@code conversation}.
//         * @param conversation The {@link Conversation} that {@code sequence} was detected in.
//         */
//        void onSequenceDetected(List<PcapPacket> sequence, Conversation conversation);

        void onSignatureDetected(List<List<PcapPacket>> signature, List<PcapPacket> match);
    }

}
