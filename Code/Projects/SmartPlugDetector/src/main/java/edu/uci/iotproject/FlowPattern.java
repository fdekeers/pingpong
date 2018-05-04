package edu.uci.iotproject;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.DnsPacket;
import org.pcap4j.packet.namednumber.DnsResourceRecordType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class FlowPattern {

    static {
        // TP-Link Local ON packet lengths (TCP payload only), extracted from ON event at Feb 13, 2018 13:38:04
        // of the 5 switch data collection:
        // 517 1448 1448 1448 855 191 51 490 1027 31

        ArrayList<Integer> packetLengths = new ArrayList<>();
        packetLengths.addAll(Arrays.asList(new Integer[] {517, 1448, 1448, 1448, 855, 191, 51, 490, 1027, 31}));
        TP_LINK_LOCAL_ON = new FlowPattern("TP_LINK_LOCAL_ON", "events.tplinkra.com", packetLengths);
    }

    public static final FlowPattern TP_LINK_LOCAL_ON;

    /**
     * Class properties
     */
    private final String patternId;

    /**
     * The hostname that this {@code FlowPattern} is associated with.
     */
    private final String hostname;  // The hostname that this {@code FlowPattern} is associated with.

    /**
     * The order of packet lengths that defines this {@link FlowPattern}
     * TODO: this is a simplified representation, we should also include information about direction of each packet.
     */
    private final List<Integer> flowPacketOrder;
        
    private final Map<String, List<Integer>> hostnameToPacketOrderMap;
    private final PcapHandle pcap;
    
    /**
     * Class constants
     */
     

    /**
     * Constructor #1
     */
    public FlowPattern(String patternId, String hostname, PcapHandle pcap) {
        this.patternId = patternId;
        this.hostname = hostname;
        this.pcap = pcap;
        this.hostnameToPacketOrderMap = null;
        this.flowPacketOrder = new ArrayList<Integer>();
        processPcap();
    }

    /**
     * Process the PcapHandle to strip off unnecessary packets and just get the integer array of packet lengths
     */
    private void processPcap() {

        PcapPacket packet;
        try {
            while ((packet = pcap.getNextPacketEx()) != null) {
                // For now, we only work support pattern search in TCP over IPv4.
                IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                if (ipPacket == null || tcpPacket == null)
                    continue;
                if (tcpPacket.getPayload() == null) // We skip non-payload control packets as these are less predictable
                    continue; 
                int packetLength = tcpPacket.getPayload().length();
                flowPacketOrder.add(packetLength);
            }
        } catch (EOFException eofe) {
            System.out.println("[ FlowPattern ] Finished processing a training PCAP stream!");
            System.out.println("[ FlowPattern ] Pattern for " + patternId + ": " + Arrays.toString(flowPacketOrder.toArray()));
        } catch (PcapNativeException  |
                 TimeoutException     |
                 NotOpenException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructor #2
     *
     * @param   patternId       Label for this pattern
     * @param   hostname        Hostname associated with this pattern
     * @param   flowPacketOrder List of packets in order
     */
    public FlowPattern(String patternId, String hostname, List<Integer> flowPacketOrder) {
        this.patternId = patternId;
        this.hostname = hostname;
        this.hostnameToPacketOrderMap = null;
        this.pcap = null;
        this.flowPacketOrder = Collections.unmodifiableList(flowPacketOrder);
    }
    
    /**
     * Constructor #3
     */
    public FlowPattern(String patternId, String hostname, Map<String, List<Integer>> hostnameToPacketOrderMap) {
        this.patternId = patternId;
        this.hostname = hostname;
        this.pcap = null;
        this.flowPacketOrder = null;
        this.hostnameToPacketOrderMap = Collections.unmodifiableMap(hostnameToPacketOrderMap);
    }

    public String getPatternId() {
        return patternId;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * Get the the sequence of packet lengths that defines this {@code FlowPattern}.
     * @return the sequence of packet lengths that defines this {@code FlowPattern}.
     */
    public List<Integer> getPacketOrder() {
        return flowPacketOrder;
    }
    
    /**
     * Get the length of the List of {@code FlowPattern}.
     * @return the length of the List of {@code FlowPattern}.
     */
    public int getLength() {
        return flowPacketOrder.size();
    }

}
