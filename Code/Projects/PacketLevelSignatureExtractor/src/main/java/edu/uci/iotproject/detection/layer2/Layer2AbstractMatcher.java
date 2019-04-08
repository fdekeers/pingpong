package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PcapPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for layer 2 matchers ({@code Layer2SequenceMatcher} and {@code Layer2RangeMatcher}).
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
abstract public class Layer2AbstractMatcher {

    /**
     * Buffer of actual packets seen so far that match the searched range (i.e., constitutes a subsequence).
     */
    protected final List<PcapPacket> mMatchedPackets = new ArrayList<>();

    /**
     * Models the directions of packets. As the sequence matcher assumes that it is only presented
     * with packet from a single flow (packets exchanged between two devices), we can model the packet directions with a
     * single bit. We don't have any notion "phone to device" or "device to phone" as we don't know the MAC addresses
     * of devices in advance during matching.
     */
    protected final boolean[] mPacketDirections;

    /**
     * Keep track of the numbers of skipped packets
     */
    protected int mSkippedPackets;
    protected int mMaxSkippedPackets;

    /**
     * Create a {@code Layer2AbstractMatcher}.
     * @param sequence The sequence of the signature.
     */
    public Layer2AbstractMatcher(List<PcapPacket> sequence) {
        mPacketDirections = new boolean[sequence.size()];
        // Compute packet directions for sequence.
        for (int i = 0; i < sequence.size(); i++) {
            if (i == 0) {
                // No previous packet; boolean parameter is ignored in this special case.
                mPacketDirections[i] = getPacketDirection(null, true, sequence.get(i));
            } else {
                // Base direction marker on direction of previous packet.
                PcapPacket prevPkt = sequence.get(i-1);
                boolean prevPktDirection = mPacketDirections[i-1];
                mPacketDirections[i] = getPacketDirection(prevPkt, prevPktDirection, sequence.get(i));
            }
        }
        mSkippedPackets = 0;
        mMaxSkippedPackets = 0;
    }

    /**
     * Compute the direction of a packet based on the previous packet. If no previous packet is provided, the direction
     * of {@code currPkt} is {@code true} by definition.
     * @param prevPkt The previous packet, if any.
     * @param prevPktDirection The computed direction of the previous packet
     * @param currPkt The current packet for which the direction is to be determined.
     * @return The direction of {@code currPkt}.
     */
    protected boolean getPacketDirection(PcapPacket prevPkt, boolean prevPktDirection, PcapPacket currPkt) {
        if (prevPkt == null) {
            // By definition, use true as direction marker for first packet
            return true;
        }
        if (PcapPacketUtils.getEthSrcAddr(prevPkt).equals(PcapPacketUtils.getEthSrcAddr(currPkt))) {
            // Current packet goes in same direction as previous packet.
            return prevPktDirection;
        } else {
            // Current packet goes in opposite direction of previous packet.
            return !prevPktDirection;
        }
    }

    /**
     * See the implementer class for the following method.
     *
     * @param packet
     * @return {@code true} if this {@code Layer2SequenceMatcher} could advance by adding {@code packet} to its set of
     *         matched packets, {@code false} otherwise.
     */
    public abstract boolean matchPacket(PcapPacket packet);

    /**
     * See the implementer class for the following method.
     */
    public abstract int getTargetSequencePacketCount();

    public int getMatchedPacketsCount() {
        return mMatchedPackets.size();
    }

    public List<PcapPacket> getMatchedPackets() {
        return mMatchedPackets;
    }

    public int getMaxSkippedPackets() {
        return mMaxSkippedPackets;
    }

    /**
     * Utility for {@code getMatchedPackets().get(getMatchedPackets().size()-1)}.
     * @return The last matched packet, or {@code null} if no packets have been matched yet.
     */
    public PcapPacket getLastPacket() {
        //return mSequence.size() > 0 ? mSequence.get(mSequence.size()-1) : null;
        return mMatchedPackets.size() > 0 ? mMatchedPackets.get(mMatchedPackets.size()-1) : null;
    }
}
