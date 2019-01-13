package edu.uci.iotproject.detection;

import edu.uci.iotproject.Layer2Flow;
import edu.uci.iotproject.L2FlowReassembler;
import edu.uci.iotproject.StateMachine;
import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.util.MacAddress;

import java.util.*;

/**
 * Layer 2 cluster matcher.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class L2ClusterMatcher extends AbstractClusterMatcher implements PacketListener {

    private final MacAddress mRouterMac = null;
    private final MacAddress mPhoneMac = null;
    private final MacAddress mDeviceMac = null;

    /**
     * Reassembles traffic flows.
     */
    private final L2FlowReassembler mFlowReassembler = new L2FlowReassembler();

    /**
     * Each inner set holds the possible packet lengths for the packet at the corresponding index in a sequemce, taken
     * across all sequences in {@link #mCluster}. For example, if the cluster is comprised of the sequences [112, 115]
     * and [112, 116], the set at index 0 will be {112}, and the set at index 1 will be {115, 116}.
     */
    private final List<Set<Integer>> mValidPktLengths;



    // Maintain one state machine for each layer...?
    private final StateMachine[] seqMatchers;

    public L2ClusterMatcher(List<List<PcapPacket>> cluster) {
        super(cluster);

        mValidPktLengths = new ArrayList<>();
        for (int i = 0; i < mCluster.get(0).size(); i++) {
            mValidPktLengths.add(new HashSet<>());
        }
        for (List<PcapPacket> seqVariation : mCluster) {
            for (int i = 0; i < seqVariation.size(); i++) {
                mValidPktLengths.get(i).add(seqVariation.get(i).getOriginalLength());
            }
        }

        seqMatchers = new StateMachine[mValidPktLengths.size()];
    }

    @Override
    protected List<List<PcapPacket>> pruneCluster(List<List<PcapPacket>> cluster) {
        return null;
    }


    @Override
    public void gotPacket(PcapPacket packet) {
        for (int i = 0; i < seqMatchers.length; i++) {
            StateMachine sm = seqMatchers[i];
            if (sm.attemptAdvance(packet)) {

            }

        }







        for (int i = 0; i < mValidPktLengths.size(); i++) {
            if (mValidPktLengths.get(i).contains(packet.getOriginalLength())) {
                // This packet length is potentially of interest to state machines that currently expect the i'th packet
                // of the searched sequence

            }
        }




        // Forward to flow reassembler
        mFlowReassembler.gotPacket(packet);




    }


    public void performDetection() {
        for (Layer2Flow flow : mFlowReassembler.getFlows()) {
            List<PcapPacket> flowPkts = flow.getPackets();

            for (List<PcapPacket> signatureSequence : mCluster) {

            }
        }
    }

/*
    private Optional<List<PcapPacket>> findSubsequenceInSequence(List<PcapPacket> subsequence,
                                                                 List<PcapPacket> sequence,
                                                                 boolean[] subsequenceDirections) {
        if (sequence.size() < subsequence.size()) {
            // If subsequence is longer, it cannot be contained in sequence.
            return Optional.empty();
        }
        // If packet directions have not been precomputed by calling code, we need to construct them.
        if (subsequenceDirections == null) {
            subsequenceDirections = getPacketDirections(subsequence);
        }






//        if (sequenceDirections == null) {
//            sequenceDirections = getPacketDirections(sequence);
//        }


        boolean[] sequenceDirections;

        int subseqIdx = 0;
        int seqIdx = 0;
        while (seqIdx < sequence.size()) {
            if (subseqIdx == 0) {
                // Every time we (re-)start matching (i.e., when we consider the first element of subsequence), we must
                // recompute the directions array for the subsequence.size() next elements of sequence so that we can
                // perform index-wise comparisons of the individual elements of the two direction arrays. If we compute
                // the directions array for the entire sequence in one go, we may end up with a reversed representation
                // of the packet directions (i.e. one in which all boolean values in the array are flipped to be the
                // opposite of what is the expected order) for a subsection of sequence that actually obeys the expected
                // directions (as defined by the directions array corresponding to subsequence), depending on the packets
                // that come earlier (as we always use 'true' for the first packet direction of a sequence).
                int toIndex = Integer.min(seqIdx + subsequence.size(), sequence.size());
                sequenceDirections = getPacketDirections(sequence.subList(seqIdx, toIndex));
            }


            PcapPacket subseqPkt = subsequence.get(subseqIdx);
            PcapPacket seqPkt = sequence.get(seqIdx);
            // We only have a match if packet lengths and directions match.
            if (subseqPkt.getOriginalLength() == seqPkt.getOriginalLength() &&
                    subsequenceDirections[subseqIdx] == sequenceDirections[subseqIdx]) {
                if (subseqIdx > 0) {

                }
            }
        }
    }
    */

    /**
     * Returns a boolean array {@code b} such that each entry in {@code b} indicates the direction of the packet at the
     * corresponding index in {@code pktSequence}. As there is no notion of client and server, we model the
     * packet directions as simple binary values. The direction of the first packet in {@code pktSequence} (and all
     * subsequent packets going in the same direction) is denoted using a value of {@code true}, and all packets going
     * in the opposite direction are denoted using a value of {@code false}.
     *
     * @param pktSequence A sequence of packets exchanged between two hosts for which packet directions are to be
     *                    extracted.
     * @return The packet directions for {@code pktSequence}.
     */
    private boolean[] getPacketDirections(List<PcapPacket> pktSequence) {
        boolean[] directions = new boolean[pktSequence.size()];
        for (int i = 0; i < pktSequence.size(); i++) {
            if (i == 0) {
                // Special case for first packet: no previous packet to compare against.
                directions[i] = true;
            } else {
                PcapPacket currPkt = pktSequence.get(i);
                PcapPacket prevPkt = pktSequence.get(i-1);
                if (PcapPacketUtils.getEthSrcAddr(currPkt).equals(PcapPacketUtils.getEthSrcAddr(prevPkt))) {
                    // Same direction as previous packet.
                    directions[i] = directions[i-1];
                } else {
                    // Opposite direction of previous packet.
                    directions[i] = !directions[i-1];
                }
            }
        }
        return directions;
    }
}
