package edu.uci.iotproject;

import java.util.Collections;
import java.util.List;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class FlowPattern {

    private final String patternId;

    /**
     * The hostname that this {@code FlowPattern} is associated with.
     */
    private final String hostname;

    /**
     * The order of packet lengths that defines this {@link FlowPattern}
     */
    private final List<Integer> flowPacketOrder;

    public FlowPattern(String patternId, String hostname, List<Integer> flowPacketOrder) {
        this.patternId = patternId;
        this.hostname = hostname;
        this.flowPacketOrder = Collections.unmodifiableList(flowPacketOrder);
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * Get the the sequence of packet lengths that defines this {@code FlowPattern}.
     * @return the the sequence of packet lengths that defines this {@code FlowPattern}.
     */
    public List<Integer> getPacketOrder() {
        return flowPacketOrder;
    }

}
