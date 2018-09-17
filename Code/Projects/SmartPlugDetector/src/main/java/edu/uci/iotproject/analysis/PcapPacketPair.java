package edu.uci.iotproject.analysis;

import edu.uci.iotproject.util.PcapPacketUtils;
import org.pcap4j.core.PcapPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * <p>
 *     A simple wrapper for holding a pair of packets (e.g., a request and associated reply packet).
 * </p>
 *
 * <b>Note:</b> we use the deprecated version
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapPacketPair {

    private final PcapPacket mFirst;

    private final Optional<PcapPacket> mSecond;

    public PcapPacketPair(PcapPacket first, PcapPacket second) {
        mFirst = first;
        mSecond = Optional.ofNullable(second);
    }

    public PcapPacket getFirst() { return mFirst; }

    public boolean isFirstClient() {
        String firstIp = PcapPacketUtils.getSourceIp(mFirst);
        InetAddress ia = null;
        try {
            ia = InetAddress.getByName(firstIp);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return ia.isSiteLocalAddress();
    }

    public Optional<PcapPacket> getSecond() { return mSecond; }

    public boolean isSecondClient() {
        // Return the value of the second source if it is not null
        if (mSecond.isPresent()) {
            String secondIp = PcapPacketUtils.getSourceIp(mSecond.get());
            InetAddress ia = null;
            try {
                ia = InetAddress.getByName(secondIp);
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            return ia.isSiteLocalAddress();
        } else {
            // When it is null, we always return the opposite of the first source's status
            return !isFirstClient();
        }
    }

    @Override
    public String toString() {
        return String.format("%d, %s",
                getFirst().getOriginalLength(),
                getSecond().map(pkt -> Integer.toString(pkt.getOriginalLength())).orElse("null"));
    }

}
