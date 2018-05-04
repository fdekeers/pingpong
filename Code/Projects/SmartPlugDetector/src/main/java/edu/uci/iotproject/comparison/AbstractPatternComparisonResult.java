package edu.uci.iotproject.comparison;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.FlowPattern;

/**
 * Models the result of comparing a {@link Conversation} and a {@link FlowPattern}.
 *
 * @param <T> The type of the result; can be something as simple as a {@code Boolean} for a complete match comparison or
 *           or a complex data type for more sophisticated comparisons.
 */
public abstract class AbstractPatternComparisonResult<T> {

    /**
     * The result of the comparison.
     */
    private final T mResult;

    /**
     * The {@code Conversation} that was compared against {@link #mFlowPattern}.
     */
    protected final Conversation mConversation;

    /**
     * The {@code FlowPattern} that {@link #mConversation} was compared against.
     */
    protected final FlowPattern mFlowPattern;

    public AbstractPatternComparisonResult(Conversation conversation, FlowPattern flowPattern, T result) {
        this.mResult = result;
        this.mConversation = conversation;
        this.mFlowPattern = flowPattern;
    }

    /**
     * Gets the result of the comparison.
     * @return the result of the comparison
     */
    public T getResult() {
        return mResult;
    }

    /**
     * Get a textual description of the comparison result suitable for output on std.out.
     * @returna a textual description of the comparison result suitable for output on std.out.
     */
    abstract public String getTextualDescription();

}