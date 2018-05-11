package edu.uci.iotproject.comparison;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.FlowPattern;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.TcpPacket;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Contains concrete implementations of comparison functions that compare a {@link Conversation} and a {@link FlowPattern}.
 * These functions are supplied to {@link PatternComparisonTask} which in turn facilitates comparison on a background thread.
 * This design provides plugability: currently, we only support complete match comparison, but further down the road we
 * can simply introduce more sophisticated comparison functions here (e.g. Least Common Substring) simply replace the
 * argument passed to the {@link PatternComparisonTask} constructor to switch between the different implementations.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class ComparisonFunctions {

    /**
     * Comparison function that checks for a <em>complete</em> match, i.e. a match in which every packet in the
     * {@link Conversation} has the same length as the corresponding packet in the {@link FlowPattern}.
     */
    public static final BiFunction<Conversation, FlowPattern, CompleteMatchPatternComparisonResult> COMPLETE_MATCH = (conversation, flowPattern) -> {
        List<PcapPacket> convPackets = conversation.getPackets();
        if (convPackets.size() != flowPattern.getLength()) {
            return new CompleteMatchPatternComparisonResult(conversation, flowPattern, false);
        }
        for (int i = 0; i < convPackets.size(); i++) {
            TcpPacket tcpPacket = convPackets.get(i).get(TcpPacket.class);
            if (tcpPacket.getPayload().length() != flowPattern.getPacketOrder().get(i)) {
                return new CompleteMatchPatternComparisonResult(conversation, flowPattern, false);
            }
        }
        return new CompleteMatchPatternComparisonResult(conversation, flowPattern, true);
    };

    /**
     * Comparison function that searches a {@link Conversation} looking for the presence of a complete match of a {@link FlowPattern}.
     * Unlike {@link #COMPLETE_MATCH}, which searches for a 1:1 match between the {@code Conversation} and the {@code FlowPattern},
     * this function targets cases where the {@code Conversation} is longer than the {@code FlowPattern}.
     * In other words, this function searches for a complete match of a sub sequence of packets in the {@code Conversation}.
     * Note: this is a slow, brute force search.
     */
    public static final BiFunction<Conversation, FlowPattern, CompleteMatchPatternComparisonResult> SUB_SEQUENCE_COMPLETE_MATCH = new BiFunction<Conversation, FlowPattern, CompleteMatchPatternComparisonResult>() {
        // TODO needs review; I was tired when I wrote this :).
        private boolean find(Conversation conversation, FlowPattern flowPattern, int nextIndex, int matchedIndices) {
            if (matchedIndices == flowPattern.getLength()) {
                // Found a full sub sequence.
                return true;
            }
            List<PcapPacket> convPackets = conversation.getPackets();
            if (nextIndex >= convPackets.size()) {
                // Reached end of list without finding a match.
                return false;
            }
            if (convPackets.get(nextIndex).get(TcpPacket.class).getPayload().length() == flowPattern.getPacketOrder().get(matchedIndices)) {
                // So far, so good. Still need to check if the remainder of the sub sequence is present.
                return find(conversation, flowPattern, ++nextIndex, ++matchedIndices);
            } else {
                // Miss; trace back and retry the search starting at the index immediately after the index from the
                // recursive calls potentially started matching some of the sub sequence.
                return find(conversation, flowPattern, nextIndex-matchedIndices+1, 0);
            }
        }

        @Override
        public CompleteMatchPatternComparisonResult apply(Conversation conversation, FlowPattern flowPattern) {
            return new CompleteMatchPatternComparisonResult(conversation, flowPattern, find(conversation, flowPattern, 0, 0));
        }

    };

}