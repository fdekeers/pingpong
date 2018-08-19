package edu.uci.iotproject;

import edu.uci.iotproject.comparison.seqalignment.AlignmentPricer;
import edu.uci.iotproject.comparison.seqalignment.SequenceAlignment;
import org.pcap4j.core.PcapPacket;

import java.util.List;
import java.util.Map;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class SequenceExtraction {


    private final SequenceAlignment<Integer> mAlignmentAlg;


    public SequenceExtraction() {
        mAlignmentAlg = new SequenceAlignment<>(new AlignmentPricer<>((i1,i2) -> Math.abs(i1-i2), i -> 10));
    }


    public SequenceExtraction(SequenceAlignment<Integer> alignmentAlgorithm) {
        mAlignmentAlg = alignmentAlgorithm;
    }

    // Initial
//    /**
//     *
//     * @param convsForAction A set of {@link Conversation}s known to be associated with a single type of user action.
//     */
//    public void extract(List<Conversation> convsForAction) {
//        int maxDifference = 0;
//
//        for (int i = 0; i < convsForAction.size(); i++) {
//            for (int j = i+1; j < convsForAction.size(); i++) {
//                Integer[] sequence1 = getPacketLengthSequence(convsForAction.get(i));
//                Integer[] sequence2 = getPacketLengthSequence(convsForAction.get(j));
//                int alignmentCost = mAlignmentAlg.calculateAlignment(sequence1, sequence2);
//                if (alignmentCost > maxDifference) {
//                    maxDifference = alignmentCost;
//                }
//            }
//        }
//
//    }


//    public void extract(Map<String, List<Conversation>> hostnameToConvs) {
//        int maxDifference = 0;
//
//        for (int i = 0; i < convsForAction.size(); i++) {
//            for (int j = i+1; j < convsForAction.size(); i++) {
//                Integer[] sequence1 = getPacketLengthSequence(convsForAction.get(i));
//                Integer[] sequence2 = getPacketLengthSequence(convsForAction.get(j));
//                int alignmentCost = mAlignmentAlg.calculateAlignment(sequence1, sequence2);
//                if (alignmentCost > maxDifference) {
//                    maxDifference = alignmentCost;
//                }
//            }
//        }
//
//    }

    private Integer[] getPacketLengthSequence(Conversation c) {
        List<PcapPacket> packets = c.getPackets();
        Integer[] packetLengthSequence = new Integer[packets.size()];
        for (int i = 0; i < packetLengthSequence.length; i++) {
            packetLengthSequence[i] = packets.get(i).length();
        }
        return packetLengthSequence;
    }
}
