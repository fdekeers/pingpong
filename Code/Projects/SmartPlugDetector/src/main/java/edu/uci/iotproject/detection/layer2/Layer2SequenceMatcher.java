package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import org.pcap4j.core.PcapPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class Layer2SequenceMatcher {

    /**
     * The sequence this {@link Layer2SequenceMatcher} is searching for.
     */
    private final List<PcapPacket> mSequence;

    /**
     * Buffer of actual packets seen so far that match the searched sequence (i.e., constitutes a subsequence of the
     * searched sequence).
     */
    private final List<PcapPacket> mMatchedPackets = new ArrayList<>();

    public Layer2SequenceMatcher(List<PcapPacket> sequence) {
        mSequence = sequence;
    }

    public boolean matchPacket(PcapPacket packet) {
        // The packet we want to match next.
        PcapPacket expected = mSequence.get(mMatchedPackets.size());
        // First verify if the received packet has the length we're looking for.
        if (packet.getOriginalLength() == expected.getOriginalLength()) {
            // Next apply timing constraints:
            // - to be a match, the packet must have a later timestamp than any other packet currently matched
            // - does adding the packet cause the max allowed time between first packet and last packet to be exceeded?
            if (mMatchedPackets.size() > 0 &&
                    !packet.getTimestamp().isAfter(mMatchedPackets.get(mMatchedPackets.size()-1).getTimestamp())) {
                return false;
            }
            if (mMatchedPackets.size() > 0 &&
                    packet.getTimestamp().
                            isAfter(mMatchedPackets.get(0).getTimestamp().
                                    plusMillis(TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS))) {
                // Packet too
                return false;
            }
            // TODO (how to) check directions?
            // This packet has a length matching next packet of searched sequence, so we store it and advance.
            mMatchedPackets.add(packet);
            if (mMatchedPackets.size() == mSequence.size()) {
                // TODO report (to observers?) that we are done.
            }
            return true;
        }
        return false;
    }

    public int getMatchedPacketsCount() {
        return mMatchedPackets.size();
    }

    public int getTargetSequencePacketCount() {
        return mSequence.size();
    }

    public List<PcapPacket> getTargetSequence() {
        return mSequence;
    }

    public List<PcapPacket> getMatchedPackets() {
        return mMatchedPackets;
    }
}
