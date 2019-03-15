package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowReassembler;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2Flow;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowReassemblerObserver;
import edu.uci.iotproject.detection.AbstractClusterMatcher;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowObserver;
import org.pcap4j.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Attempts to detect members of a cluster (packet sequence mutations) in layer 2 flows.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class Layer2ClusterMatcher extends AbstractClusterMatcher implements Layer2FlowReassemblerObserver, Layer2FlowObserver {

    /**
     * Maps from a flow to a table of {@link Layer2SequenceMatcher}s for that particular flow. The table {@code t} is
     * structured such that {@code t[i][j]} is a {@link Layer2SequenceMatcher} that attempts to match member {@code i}
     * of {@link #mCluster} and has so far matched {@code j} packets of that particular sequence.
     */
    private final Map<Layer2Flow, Layer2SequenceMatcher[][]> mPerFlowSeqMatchers = new HashMap<>();
    private final Map<Layer2Flow, Layer2RangeMatcher[]> mPerFlowRangeMatcher = new HashMap<>();

    private final Function<Layer2Flow, Boolean> mFlowFilter;

    /**
     * Specifying range-based instead of conservative exact matching.
     */
    private final boolean mRangeBased;

    /**
     * Epsilon value used by the DBSCAN algorithm; it is used again for range-based matching here.
     */
    private final double mEps;

    private int mInclusionTimeMillis;

    /**
     * Create a new {@link Layer2ClusterMatcher} that attempts to find occurrences of {@code cluster}'s members.
     * @param cluster The sequence mutations that the new {@link Layer2ClusterMatcher} should search for.
     */
    public Layer2ClusterMatcher(List<List<PcapPacket>> cluster, int inclusionTimeMillis,
                                boolean isRangeBased, double eps) {
        // Consider all flows if no flow filter specified.
        this(cluster, flow -> true, inclusionTimeMillis, isRangeBased, eps);
    }

    /**
     * Create a new {@link Layer2ClusterMatcher} that attempts to find occurrences of {@code cluster}'s members.
     * @param cluster The sequence mutations that the new {@link Layer2ClusterMatcher} should search for.
     * @param flowFilter A filter that defines what {@link Layer2Flow}s the new {@link Layer2ClusterMatcher} should
     *                   search for {@code cluster}'s members in. If {@code flowFilter} returns {@code true}, the flow
     *                   will be included (searched). Note that {@code flowFilter} is only queried once for each flow,
     *                   namely when the {@link Layer2FlowReassembler} notifies the {@link Layer2ClusterMatcher} about
     *                   the new flow. This functionality may for example come in handy when one only wants to search
     *                   for matches in the subset of flows that involves a specific (range of) MAC(s).
     * @param inclusionTimeMillis Packet inclusion time limit for matching.
     * @param isRangeBased The boolean that decides if it is range-based vs. strict matching.
     * @param eps The epsilon value used in the DBSCAN algorithm.
     */
    public Layer2ClusterMatcher(List<List<PcapPacket>> cluster, Function<Layer2Flow, Boolean> flowFilter,
                                int inclusionTimeMillis, boolean isRangeBased, double eps) {
        super(cluster, isRangeBased);
        mFlowFilter = flowFilter;
        mRangeBased = isRangeBased;
        mEps = eps;
        mInclusionTimeMillis =
                inclusionTimeMillis == 0 ? TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS : inclusionTimeMillis;
    }

    @Override
    public void onNewPacket(Layer2Flow flow, PcapPacket newPacket) {
        if (mRangeBased) {
            rangeBasedMatching(flow, newPacket);
        } else {
            conservativeMatching(flow, newPacket);
        }
    }

    private void conservativeMatching(Layer2Flow flow, PcapPacket newPacket) {
        if (mPerFlowSeqMatchers.get(flow) == null) {
            // If this is the first time we encounter this flow, we need to set up sequence matchers for it.
            // All sequences of the cluster have the same length, so we only need to compute the length of the nested
            // arrays once. We want to make room for a cluster matcher in each state, including the initial empty state
            // but excluding the final "full match" state (as there is no point in keeping a terminated sequence matcher
            // around), so the length of the inner array is simply the sequence length.
            Layer2SequenceMatcher[][] matchers = new Layer2SequenceMatcher[mCluster.size()][mCluster.get(0).size()];
            // Prepare a "state 0" sequence matcher for each sequence variation in the cluster.
            for (int i = 0; i < matchers.length; i++) {
                matchers[i][0] = new Layer2SequenceMatcher(mCluster.get(i), mInclusionTimeMillis);
            }
            // Associate the new sequence matcher table with the new flow
            mPerFlowSeqMatchers.put(flow, matchers);
        }
        // Fetch table that contains sequence matchers for this flow.
        Layer2SequenceMatcher[][] matchers = mPerFlowSeqMatchers.get(flow);
        // Present the packet to all sequence matchers.
        for (int i = 0; i < matchers.length; i++) {
            // Present packet to the sequence matchers that has advanced the most first. This is to prevent discarding
            // the sequence matchers that have advanced the most in the special case where the searched sequence
            // contains two packets of the same length going in the same direction.
            for (int j = matchers[i].length - 1; j >= 0 ; j--) {
                Layer2SequenceMatcher sm = matchers[i][j];
                if (sm == null) {
                    // There is currently no sequence matcher that has managed to match j packets.
                    continue;
                }
                boolean matched = sm.matchPacket(newPacket);
                if (matched) {
                    if (sm.getMatchedPacketsCount() == sm.getTargetSequencePacketCount()) {
                        // Sequence matcher has a match. Report it to observers.
                        mObservers.forEach(o -> o.onMatch(this, sm.getMatchedPackets()));
                        // Remove the now terminated sequence matcher.
                        matchers[i][j] = null;
                    } else {
                        // Sequence matcher advanced one step, so move it to its corresponding new position iff the
                        // packet that advanced it has a later timestamp than that of the last matched packet of the
                        // sequence matcher at the new index, if any. In most traces, a small amount of the packets
                        // appear out of order (with regards to their timestamp), which is why this check is required.
                        // Obviously it would not be needed if packets where guaranteed to be processed in timestamp
                        // order here.
                        if (matchers[i][j+1] == null ||
                                newPacket.getTimestamp().isAfter(matchers[i][j+1].getLastPacket().getTimestamp())) {
                            matchers[i][j+1] = sm;
                        }
                    }
                    // We always want to have a sequence matcher in state 0, regardless of if the one that advanced
                    // from state zero completed its matching or if it replaced a different one in state 1 or not.
                    if (sm.getMatchedPacketsCount() == 1) {
                        matchers[i][j] = new Layer2SequenceMatcher(sm.getTargetSequence(), mInclusionTimeMillis);
                    }
                }
            }
        }
    }

    private void rangeBasedMatching(Layer2Flow flow, PcapPacket newPacket) {
        // TODO: For range-based matching, we only care about matching a range; therefore it is a matcher array.
        if (mPerFlowRangeMatcher.get(flow) == null) {
            // If this is the first time we encounter this flow, we need to set up a sequence matcher.
            // All sequences of the cluster have the same length, so we only need to compute the length of the
            // arrays once. We want to make room for a cluster matcher in each state, including the initial empty state
            // but excluding the final "full match" state (as there is no point in keeping a terminated sequence matcher
            // around), so the length of the array is simply the sequence length.
            Layer2RangeMatcher[] matcher = new Layer2RangeMatcher[mCluster.get(0).size()];
            // Prepare a "state 0" sequence matcher.
            matcher[0] = new Layer2RangeMatcher(mCluster.get(0), mCluster.get(1), mInclusionTimeMillis, mEps);
            // Associate the new sequence matcher table with the new flow.
            mPerFlowRangeMatcher.put(flow, matcher);
        }
        // Fetch table that contains sequence matchers for this flow.
        Layer2RangeMatcher[] matcher = mPerFlowRangeMatcher.get(flow);
        // Present packet to the sequence matcher.
        for (int j = matcher.length - 1; j >= 0; j--) {
            Layer2RangeMatcher sm = matcher[j];
            if (sm == null) {
                // There is currently no sequence matcher that has managed to match j packets.
                continue;
            }
            boolean matched = sm.matchPacket(newPacket);
            if (matched) {
                if (sm.getMatchedPacketsCount() == sm.getTargetSequencePacketCount()) {
                    // Sequence matcher has a match. Report it to observers.
                    mObservers.forEach(o -> o.onMatch(this, sm.getMatchedPackets()));
                    // Remove the now terminated sequence matcher.
                    matcher[j] = null;
                } else {
                    // Sequence matcher advanced one step, so move it to its corresponding new position iff the
                    // packet that advanced it has a later timestamp than that of the last matched packet of the
                    // sequence matcher at the new index, if any. In most traces, a small amount of the packets
                    // appear out of order (with regards to their timestamp), which is why this check is required.
                    // Obviously it would not be needed if packets where guaranteed to be processed in timestamp
                    // order here.
                    if (matcher[j+1] == null ||
                            newPacket.getTimestamp().isAfter(matcher[j+1].getLastPacket().getTimestamp())) {
                        matcher[j+1] = sm;
                    }
                }
                // We always want to have a sequence matcher in state 0, regardless of if the one that advanced
                // from state zero completed its matching or if it replaced a different one in state 1 or not.
                if (sm.getMatchedPacketsCount() == 1) {
                    matcher[j] = new Layer2RangeMatcher(sm.getTargetLowerBound(), sm.getTargetUpperBound(),
                            mInclusionTimeMillis, mEps);
                }
            }
        }
    }

    @Override
    protected List<List<PcapPacket>> pruneCluster(List<List<PcapPacket>> cluster) {
        // Note: we assume that all sequences in the input cluster are of the same length and that their packet
        // directions are identical.
        List<List<PcapPacket>> prunedCluster = new ArrayList<>();
        for (List<PcapPacket> originalClusterSeq : cluster) {
            boolean alreadyPresent = prunedCluster.stream().anyMatch(pcPkts -> {
                for (int i = 0; i < pcPkts.size(); i++) {
                    if (pcPkts.get(i).getOriginalLength() != originalClusterSeq.get(i).getOriginalLength()) {
                        return false;
                    }
                }
                return true;
            });
            if (!alreadyPresent) {
                // Add the sequence if not already present in the pruned cluster.
                prunedCluster.add(originalClusterSeq);
            }
        }
        return prunedCluster;
    }

    private static final boolean DEBUG = false;

    @Override
    public void onNewFlow(Layer2FlowReassembler reassembler, Layer2Flow newFlow) {
        // New flow detected. Check if we should consider it when searching for cluster member matches.
        if (mFlowFilter.apply(newFlow)) {
            if (DEBUG) {
                System.out.println(">>> ACCEPTING FLOW: " + newFlow + " <<<");
            }
            // Subscribe to the new flow to get updates whenever a new packet pertaining to the flow is processed.
            newFlow.addFlowObserver(this);
        } else if (DEBUG) {
            System.out.println(">>> IGNORING FLOW: " + newFlow + " <<<");
        }
    }
}
