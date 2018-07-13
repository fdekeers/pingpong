package edu.uci.iotproject;

import org.pcap4j.core.PcapPacket;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public interface PcapPacketConsumer {

    void consumePacket(PcapPacket pcapPacket);

}
