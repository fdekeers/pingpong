package edu.uci.iotproject;

import org.pcap4j.core.PcapPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapProcessingPipeline {

    private final PcapReader mPcapReader;
    private final List<PcapPacketConsumer> mPacketConsumers;

    public PcapProcessingPipeline(PcapReader pcapReader) {
        mPcapReader = pcapReader;
        mPacketConsumers = new ArrayList<>();
    }

    public void addPcapPacketConsumer(PcapPacketConsumer packetConsumer) {
        mPacketConsumers.add(packetConsumer);
    }

    public void executePipeline() {
        PcapPacket packet;
        while ((packet = mPcapReader.readNextPacket()) != null) {
            for (PcapPacketConsumer consumer : mPacketConsumers) {
                consumer.consumePacket(packet);
            }
        }
    }

}
