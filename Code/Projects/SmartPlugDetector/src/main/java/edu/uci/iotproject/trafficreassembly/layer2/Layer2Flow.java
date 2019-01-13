package edu.uci.iotproject.trafficreassembly.layer2;

import edu.uci.iotproject.detection.Layer2FlowObserver;
import org.pcap4j.core.PcapPacket;
import org.pcap4j.packet.EthernetPacket;
import org.pcap4j.util.MacAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The packets exchanged between two endpoints (MAC addresses).
 *
 * @author Janus Varmarken
 */
public class Layer2Flow {

    private final MacAddress mEndpoint1;
    private final MacAddress mEndpoint2;

    private final List<Layer2FlowObserver> mFlowObservers = new ArrayList<>();

    public Layer2Flow(MacAddress endpoint1, MacAddress endpoint2) {
        mEndpoint1 = endpoint1;
        mEndpoint2 = endpoint2;
    }

    public void addFlowObserver(Layer2FlowObserver observer) {
        mFlowObservers.add(observer);
    }

    public void removeFlowObserver(Layer2FlowObserver observer) {
        mFlowObservers.remove(observer);
    }

    /**
     * The packets in the flow.
     */
    private final List<PcapPacket> mPackets = new ArrayList<>();

    /**
     * Add a packet to this flow.
     * @param packet The packet that is to be added to the flow.
     */
    public void addPacket(PcapPacket packet) {
        verifyAddresses(packet);
        mPackets.add(packet);
        // Notify flow observers of the new packet
        mFlowObservers.forEach(o -> o.onNewPacket(this, packet));
    }

    public List<PcapPacket> getPackets() {
        return Collections.unmodifiableList(mPackets);
    }

    private void verifyAddresses(PcapPacket packet) {
        EthernetPacket ethPkt = packet.get(EthernetPacket.class);
        MacAddress srcAddr = ethPkt.getHeader().getSrcAddr();
        MacAddress dstAddr = ethPkt.getHeader().getDstAddr();
        if ((mEndpoint1.equals(srcAddr) && mEndpoint2.equals(dstAddr)) ||
                (mEndpoint1.equals(dstAddr) && mEndpoint2.equals(srcAddr))) {
            // All is good.
            return;
        }
        throw new IllegalArgumentException("Mismatch in MACs: packet does not pertain to this flow");
    }

}



/*


 Packet stream -> flow reassembler -> flow1, flow2, flow3... -> for each flow, keep a sequence matcher for each sequence of cluster


 */