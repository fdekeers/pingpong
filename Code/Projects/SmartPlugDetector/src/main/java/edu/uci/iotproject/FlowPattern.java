package edu.uci.iotproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class FlowPattern {

    static {
        // TP-Link Local ON packet lengths (TCP payload only), extracted from ON event at Feb 13, 2018 13:38:04
        // of the 5 switch data collection:
        // 517 1448 1448 1448 855 191 51 490 1027 31

        ArrayList<Integer> packetLengths = new ArrayList<>();
        packetLengths.addAll(Arrays.asList(new Integer[] {517, 1448, 1448, 1448, 855, 191, 51, 490, 1027, 31}));
        TP_LINK_LOCAL_ON = new FlowPattern("TP_LINK_LOCAL_ON", "events.tplinkra.com", packetLengths);
    }

    public static final FlowPattern TP_LINK_LOCAL_ON;

    private final String patternId;

    /**
     * The hostname that this {@code FlowPattern} is associated with.
     */
    private final String hostname;

    /**
     * The order of packet lengths that defines this {@link FlowPattern}
     * TODO: this is a simplified representation, we should also include information about direction of each packet.
     */
    private final List<Integer> flowPacketOrder;

    public FlowPattern(String patternId, String hostname, List<Integer> flowPacketOrder) {
        this.patternId = patternId;
        this.hostname = hostname;
        this.flowPacketOrder = Collections.unmodifiableList(flowPacketOrder);
    }

    public String getPatternId() {
        return patternId;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * Get the the sequence of packet lengths that defines this {@code FlowPattern}.
     * @return the sequence of packet lengths that defines this {@code FlowPattern}.
     */
    public List<Integer> getPacketOrder() {
        return flowPacketOrder;
    }
    
    /**
     * Get the length of the List of {@code FlowPattern}.
     * @return the length of the List of {@code FlowPattern}.
     */
    public int getLength() {
        return flowPacketOrder.size();
    }

}
