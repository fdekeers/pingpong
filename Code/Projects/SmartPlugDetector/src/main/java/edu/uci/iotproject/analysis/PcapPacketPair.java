package edu.uci.iotproject.analysis;

import org.pcap4j.core.PcapPacket;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
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
        return getFirst().length() + ", "  + (getSecond() == null ? "null" : getSecond().length());
    }
}
