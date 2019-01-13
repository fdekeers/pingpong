package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowReassembler;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2Flow;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowReassemblerObserver;
import edu.uci.iotproject.detection.AbstractClusterMatcher;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowObserver;
import edu.uci.iotproject.io.PcapHandleReader;
import edu.uci.iotproject.util.PrintUtils;
import org.pcap4j.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class Layer2ClusterMatcher extends AbstractClusterMatcher implements Layer2FlowReassemblerObserver, Layer2FlowObserver {

    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        final String onSignatureFile = "/Users/varmarken/temp/UCI IoT Project/experiments/training/signatures/tplink-plug/tplink-plug-onSignature-device-side.sig";
        List<List<List<PcapPacket>>> onSignature = PrintUtils.deserializeSignatureFromFile(onSignatureFile);


        Layer2FlowReassembler flowReassembler = new Layer2FlowReassembler();

        Layer2ClusterMatcher l2ClusterMatcher = new Layer2ClusterMatcher(onSignature.get(0));
        flowReassembler.addObserver(l2ClusterMatcher);

        final String inputPcapFile = "/Users/varmarken/temp/UCI IoT Project/experiments/2018-07/tplink/tplink.wlan1.local.pcap";

        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(inputPcapFile, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(inputPcapFile);
        }
        PcapHandleReader reader = new PcapHandleReader(handle, p -> true, flowReassembler);
        reader.readFromHandle();


    }


    private final List<Layer2SequenceMatcher> mSeqMatchers;

    public Layer2ClusterMatcher(List<List<PcapPacket>> cluster) {
        super(cluster);
        // Setup a sequence matcher for each sequence of the pruned cluster
        mSeqMatchers = new ArrayList<>();
        mCluster.forEach(seq -> mSeqMatchers.add(new Layer2SequenceMatcher(seq)));

//        for (int i = 0; i < mCluster.size(); i++) {
//
//
//            mSeqMatchers[i] = new Layer2SequenceMatcher(mCluster.get(i));
//
//
//        }
    }

//    @Override
//    public void gotPacket(PcapPacket packet) {
//        // Forward the packet to all sequence matchers.
//        for (Layer2SequenceMatcher matcher : mSeqMatchers) {
//            matcher.gotPacket(packet);
//        }
//
//
//    }


    private final Map<Layer2Flow, List<Layer2SequenceMatcher>> mPerFlowSeqMatchers = new HashMap<>();

    @Override
    public void onNewPacket(Layer2Flow flow, PcapPacket newPacket) {
        if (mPerFlowSeqMatchers.get(flow) == null) {
            // If this is the first time we encounter this flow, we need to set up sequence matchers for it.
            List<Layer2SequenceMatcher> matchers = new ArrayList<>();
            mCluster.forEach(seq -> matchers.add(new Layer2SequenceMatcher(seq)));
            mPerFlowSeqMatchers.put(flow, matchers);
        }
        // Buffer for new sequence matchers that will take over the job of observing for the first packet when a
        // sequence matcher advances beyond the first packet.
        List<Layer2SequenceMatcher> newSeqMatchers = new ArrayList<>();
        // Buffer for sequence matchers that have terminated and are to be removed from mPerFlowSeqMatchers.
        List<Layer2SequenceMatcher> terminatedSeqMatchers = new ArrayList<>();
        // Present the new packet to all sequence matchers
        for (Layer2SequenceMatcher sm : mPerFlowSeqMatchers.get(flow)) {
            boolean matched = sm.matchPacket(newPacket);
            if (matched && sm.getMatchedPacketsCount() == 1) {
                // Setup a new sequence matcher that matches from the beginning of the sequence so as to keep
                // progressing in the sequence matcher that just matched the current packet, while still allowing
                // for matches of the full sequence in later traffic. This is to accommodate the case where the
                // first packet of a sequence is detected in an early packet, but where the remaining packets of
                // that sequence do not appear until way later in time (e.g., if the first packet of the sequence
                // by chance is generated from traffic unrelated to the trigger traffic).
                // Note that we must store the new sequence matcher in a buffer and add it outside the loop in order to
                // prevent concurrent modification exceptions.
                newSeqMatchers.add(new Layer2SequenceMatcher(sm.getTargetSequence()));
            }
            if (matched && sm.getMatchedPacketsCount() == sm.getTargetSequencePacketCount()) {
                // This sequence matcher has a match of the sequence it was searching for
                // TODO report it.... for now just do a dummy printout.
                System.out.println("SEQUENCE MATCHER HAS A MATCH AT " + sm.getMatchedPackets().get(0).getTimestamp());
                // Mark the sequence matcher for removal. No need to create a replacement one as we do that whenever the
                // first packet of the sequence is matched (see above).
                terminatedSeqMatchers.add(sm);
            }
        }
        // Add the new sequence matchers, if any.
        mPerFlowSeqMatchers.get(flow).addAll(newSeqMatchers);
        // Remove the terminated sequence matchers, if any.
        mPerFlowSeqMatchers.get(flow).removeAll(terminatedSeqMatchers);
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


    @Override
    public void onNewFlow(Layer2FlowReassembler reassembler, Layer2Flow newFlow) {
        // Subscribe to the new flow to get updates whenever a new packet pertaining to the flow is processed.
        newFlow.addFlowObserver(this);
    }
}
