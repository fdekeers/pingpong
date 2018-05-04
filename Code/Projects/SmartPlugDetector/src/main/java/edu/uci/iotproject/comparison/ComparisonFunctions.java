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

}
