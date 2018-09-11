package edu.uci.iotproject.analysis;

import org.pcap4j.core.PcapPacket;

/**
 * A simple wrapper for holding a pair of packets (e.g., a request and associated reply packet).
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapPacketPair {

    private final PcapPacket mFirst;

    private final PcapPacket mSecond;

    public PcapPacketPair(PcapPacket first, PcapPacket second) {
        mFirst = first;
        mSecond = second;
    }

    public PcapPacket getFirst() { return mFirst; }

    public PcapPacket getSecond() { return mSecond; }

    @Override
    public String toString() {
        return getFirst().getOriginalLength() + ", "  + (getSecond() == null ? "null" : getSecond().getOriginalLength());
    }

}
