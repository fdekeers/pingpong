package edu.uci.iotproject.comparison;

import edu.uci.iotproject.Conversation;
import edu.uci.iotproject.FlowPattern;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;

/**
 * A task that compares a given {@link Conversation} and {@link FlowPattern} using a provided comparison function.
 * The task implements {@link Callable} and can hence be executed on a background thread.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PatternComparisonTask<R extends AbstractPatternComparisonResult<?>> implements Callable<R> {

    private final Conversation mConversation;
    private final FlowPattern mFlowPattern;
    private final BiFunction<Conversation, FlowPattern, R> mComparitor;

    /**
     * Create a new {@code PatternComparisonTask}.
     *
     * @param conversation The conversation to compare against {@code pattern}.
     * @param pattern The pattern to compare against {@code conversation}.
     * @param comparisonFunction The function that compares {@code pattern} and {@code conversation}.
     */
    public PatternComparisonTask(Conversation conversation, FlowPattern pattern, BiFunction<Conversation, FlowPattern, R> comparisonFunction) {
        this.mConversation = conversation;
        this.mFlowPattern = pattern;
        this.mComparitor = comparisonFunction;
    }

    @Override
    public R call() throws Exception {
        return mComparitor.apply(mConversation, mFlowPattern);
    }

}