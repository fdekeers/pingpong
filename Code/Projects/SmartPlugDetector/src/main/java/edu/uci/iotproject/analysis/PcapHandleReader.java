package edu.uci.iotproject.analysis;

import org.pcap4j.core.*;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

/**
 * Reads packets from a {@link PcapHandle} (online or offline) and delivers those packets that pass the test exercised
 * by the provided {@link PcapPacketFilter} onto the provided {@link PacketListener}s.
 *
 * @author Janus Varmarken
 */
public class PcapHandleReader {

    private final PcapPacketFilter mPacketFilter;
    private final PcapHandle mHandle;
    private final PacketListener[] mPacketListeners;

    public PcapHandleReader(PcapHandle handle, PcapPacketFilter packetFilter, PacketListener... packetListeners) {
        mHandle = handle;
        mPacketFilter = packetFilter;
        mPacketListeners = packetListeners;
    }


    /**
     * Start reading (and filtering) packets from the provided {@link PcapHandle}.
     * @throws PcapNativeException if an error occurs in the pcap native library.
     * @throws NotOpenException if the provided {@code PcapHandle} is not open.
     * @throws TimeoutException if packets are being read from a live capture and the timeout expired.
     */
    public void readFromHandle() throws PcapNativeException, NotOpenException, TimeoutException {
        try {
            PcapPacket prevPacket = null;
            PcapPacket packet;
            while ((packet = mHandle.getNextPacketEx()) != null) {
                if (prevPacket != null && packet.getTimestamp().isBefore(prevPacket.getTimestamp())) {
                    // Fail early if assumption doesn't hold.
                    mHandle.close();
                    throw new AssertionError("Packets not in ascending temporal order");
                }
                if (mPacketFilter.shouldIncludePacket(packet)) {
                    // Packet accepted for inclusion; deliver it to observing client code.
                    for (PacketListener consumer : mPacketListeners) {
                        consumer.gotPacket(packet);
                    }
                }
                prevPacket = packet;
            }
        } catch (EOFException eof) {
            // Reached end of file. All good.
            System.out.println(String.format("%s: finished reading pcap file", getClass().getSimpleName()));
        }
        mHandle.close();
    }

}
