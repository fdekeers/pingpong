package edu.uci.iotproject.detection;

import edu.uci.iotproject.Layer2Flow;
import org.pcap4j.core.PcapPacket;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public interface Layer2FlowObserver {

    void onNewPacket(Layer2Flow flow, PcapPacket newPacket);

}
