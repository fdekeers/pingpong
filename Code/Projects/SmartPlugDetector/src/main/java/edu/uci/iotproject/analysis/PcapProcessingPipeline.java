package edu.uci.iotproject.analysis;

import edu.uci.iotproject.PcapPacketConsumer;
import edu.uci.iotproject.PcapReader;
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
    private final PcapPacketFilter mPacketFilter;

    public PcapProcessingPipeline(PcapReader pcapReader, PcapPacketFilter packetFilter) {
        mPcapReader = pcapReader;
        mPacketConsumers = new ArrayList<>();
        mPacketFilter = packetFilter;
    }

    public void addPcapPacketConsumer(PcapPacketConsumer packetConsumer) {
        mPacketConsumers.add(packetConsumer);
    }

    public void executePipeline() {
        PcapPacket packet;
        long count = 0;
        while ((packet = mPcapReader.readNextPacket()) != null) {
            if (mPacketFilter != null && !mPacketFilter.shouldIncludePacket(packet)) {
                continue;
            }
            for (PcapPacketConsumer consumer : mPacketConsumers) {
                consumer.consumePacket(packet);
            }
            count++;
            if (count % 1000 == 0) {
                System.out.println("Processed " + count + " packets");
            }
        }
    }

}
