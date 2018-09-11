package edu.uci.iotproject.analysis;

import org.apache.commons.math3.ml.clustering.Clusterable;
import org.pcap4j.core.PcapPacket;

import java.util.Optional;

/**
 * A simple wrapper for holding a pair of packets (e.g., a request and associated reply packet).
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapPacketPair implements Clusterable {

    private final PcapPacket mFirst;

    private final Optional<PcapPacket> mSecond;

    public PcapPacketPair(PcapPacket first, PcapPacket second) {
        mFirst = first;
        mSecond = Optional.ofNullable(second);
    }

    public PcapPacket getFirst() { return mFirst; }

    public Optional<PcapPacket> getSecond() { return mSecond; }

    @Override
    public String toString() {
        return String.format("%d, %s",
                getFirst().getOriginalLength(),
                getSecond().map(pkt -> Integer.toString(pkt.getOriginalLength())).orElse("null"));
    }

    @Override
    public double[] getPoint() {
        return new double[0];
    }
}
