package edu.uci.iotproject.detection;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.TcpReassembler;
import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;

import java.util.List;

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
     * For reassembling the observed traffic into TCP connections.
     */
    private final TcpReassembler mTcpReassembler = new TcpReassembler();

    public SignatureDetector(List<List<PcapPacket>> signature) {
        mSignature = signature;
    }


    @Override
    public void gotPacket(PcapPacket packet) {
        // Present packet to TCP reassembler so that it can be mapped to a connection (if it is a TCP packet).
        mTcpReassembler.gotPacket(packet);

    }

//    private void performDetection() {
//        // Let's start out simple by building a version that only works for signatures that do not span across multiple
//        // TCP conversations...
//        for (Conversation c : mTcpReassembler.getTcpConversations()) {
//            boolean matchFound = isSequenceInConversation(c);
//        }
//    }

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
        int seqIdx = 0;
        int convIdx = 0;
        while (convIdx < packets.size()) {
            PcapPacket seqPkt = sequence.get(seqIdx);
            PcapPacket convPkt = packets.get(convIdx);
            if (convPkt.getOriginalLength() == seqPkt.getOriginalLength()) {
                // TODO should also check direction of packets -- how to?
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
        int srcPort = PcapPacketUtils.getSourcePort(firstPkt);
        int dstPort = PcapPacketUtils.getDestinationPort(firstPkt);
        return TcpConversationUtils.isTlsPort(srcPort) || TcpConversationUtils.isTlsPort(dstPort);
    }

}
