package edu.uci.iotproject;

/**
 * For observing a {@link L2FlowReassembler}.
 *
 * @author Janus Varmarken
 */
public interface Layer2FlowReassemblerObserver {

    /**
     * Invoked when when a {@link L2FlowReassembler} detects a new flow (i.e., when it encounters traffic between two
     * MAC addresses that has not previously communicated in the traffic trace).
     *
     * @param reassembler The {@link L2FlowReassembler} that detected the new flow.
     * @param newFlow The new flow.
     */
    void onNewFlow(L2FlowReassembler reassembler, Layer2Flow newFlow);

}
