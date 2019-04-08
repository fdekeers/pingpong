package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.util.MacAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Attempts to detect the presence of a specific packet sequence in the set of packets provided through multiple calls
 * to {@link #matchPacket(PcapPacket)}, considering only layer 2 information.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class Layer2SequenceMatcher extends Layer2AbstractMatcher {

    /**
     * The sequence this {@link Layer2SequenceMatcher} is searching for.
     */
    private final List<PcapPacket> mSequence;

    private int mInclusionTimeMillis;


    /**
     * Create a {@code Layer2SequenceMatcher}.
     * @param sequence The sequence to match against (search for).
     */
    public Layer2SequenceMatcher(List<PcapPacket> sequence, int inclusionTimeMillis) {
        super(sequence);
        mSequence = sequence;
        // Compute packet directions for sequence.
        for (int i = 0; i < sequence.size(); i++) {
            if (i == 0) {
                // No previous packet; boolean parameter is ignored in this special case.
                mPacketDirections[i] = getPacketDirection(null, true, sequence.get(i));
            } else {
                // Base direction marker on direction of previous packet.
                PcapPacket prevPkt = mSequence.get(i-1);
                boolean prevPktDirection = mPacketDirections[i-1];
                mPacketDirections[i] = getPacketDirection(prevPkt, prevPktDirection, sequence.get(i));
            }
        }
        mInclusionTimeMillis =
                inclusionTimeMillis == 0 ? TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS : inclusionTimeMillis;
    }

    /**
     * Attempt to advance this {@code Layer2SequenceMatcher} by matching {@code packet} against the packet that this
     * {@code Layer2SequenceMatcher} expects as the next packet of the sequence it is searching for.
     * @param packet
     * @return {@code true} if this {@code Layer2SequenceMatcher} could advance by adding {@code packet} to its set of
     *         matched packets, {@code false} otherwise.
     */
    public boolean matchPacket(PcapPacket packet) {
        if (getMatchedPacketsCount() == getTargetSequencePacketCount()) {
            // We already matched the entire sequence, so we can't match any more packets.
            return false;
        }

        // Verify that new packet pertains to same flow as previously matched packets, if any.
        if (getMatchedPacketsCount() > 0) {
            MacAddress pktSrc = PcapPacketUtils.getEthSrcAddr(packet);
            MacAddress pktDst = PcapPacketUtils.getEthDstAddr(packet);
            MacAddress earlierPktSrc = PcapPacketUtils.getEthSrcAddr(mMatchedPackets.get(0));
            MacAddress earlierPktDst = PcapPacketUtils.getEthDstAddr(mMatchedPackets.get(0));
            if (!(pktSrc.equals(earlierPktSrc) && pktDst.equals(earlierPktDst) ||
                    pktSrc.equals(earlierPktDst) && pktDst.equals(earlierPktSrc))) {
                return false;
            }
        }

        // Get representative of the packet we expect to match next.
        PcapPacket expected = mSequence.get(mMatchedPackets.size());
        // First verify if the received packet has the length we're looking for.
        if (packet.getOriginalLength() == expected.getOriginalLength()) {
            // If this is the first packet, we only need to verify that its length is correct. Time constraints are
            // obviously satisfied as there are no previous packets. Furthermore, direction matches by definition as we
            // don't know the MAC of the device (or phone) in advance, so we can't enforce a rule saying "first packet
            // must originate from this particular MAC".
            if (getMatchedPacketsCount() == 0) {
                // Store packet as matched and advance.
                mMatchedPackets.add(packet);
                return true;
            }
            // Check if direction of packet matches expected direction.
            boolean actualDirection = getPacketDirection(mMatchedPackets.get(getMatchedPacketsCount()-1),
                    mPacketDirections[getMatchedPacketsCount()-1], packet);
            boolean expectedDirection = mPacketDirections[getMatchedPacketsCount()];
            if (actualDirection != expectedDirection) {
                mSkippedPackets++;
                return false;
            }
            // Next apply timing constraints:
            // 1: to be a match, the packet must have a later timestamp than any other packet currently matched
            // 2: does adding the packet cause the max allowed time between first packet and last packet to be exceeded?
            if (!packet.getTimestamp().isAfter(mMatchedPackets.get(getMatchedPacketsCount()-1).getTimestamp())) {
                mSkippedPackets++;
                return false;
            }
//            if (packet.getTimestamp().isAfter(mMatchedPackets.get(0).getTimestamp().
//                            plusMillis(TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS))) {
            if (packet.getTimestamp().isAfter(mMatchedPackets.get(0).getTimestamp().
                plusMillis(mInclusionTimeMillis))) {
                mSkippedPackets++;
                return false;
            }
            // If we made it here, it means that this packet has the expected length, direction, and obeys the timing
            // constraints, so we store it and advance.
            if (mMaxSkippedPackets < mSkippedPackets) {
                mMaxSkippedPackets = mSkippedPackets;
                mSkippedPackets = 0;
            }
            mMatchedPackets.add(packet);
            if (mMatchedPackets.size() == mSequence.size()) {
                // TODO report (to observers?) that we are done?
            }
            return true;
        }
        return false;
    }

    public int getTargetSequencePacketCount() {
        return mSequence.size();
    }

    public List<PcapPacket> getTargetSequence() {
        return mSequence;
    }

}
