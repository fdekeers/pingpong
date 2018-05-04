package edu.uci.iotproject.comparison;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.FlowPattern;

/**
 * The result of a search for a complete match. Serves as an example implementation of
 * {@link AbstractPatternComparisonResult}.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class CompleteMatchPatternComparisonResult extends AbstractPatternComparisonResult<Boolean> {

    public CompleteMatchPatternComparisonResult(Conversation conversation, FlowPattern flowPattern, Boolean result) {
        super(conversation, flowPattern, result);
    }

    @Override
    public String getTextualDescription() {
        if (getResult()) {
            return String.format("[ find ] Detected a COMPLETE MATCH of pattern '%s' at %s!",
                    mFlowPattern.getPatternId(), mConversation.getPackets().get(0).getTimestamp().toString());
        } else {
            return String.format("[ miss ] flow starting at %s was **not** a complete match of pattern '%s'",
                    mConversation.getPackets().get(0).getTimestamp().toString(), mFlowPattern.getPatternId());
        }
    }

}