package edu.uci.iotproject;

import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.io.EOFException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Provides functionality for searching for the presence of a {@link FlowPattern} in a PCAP trace.
 *
 * @author Janus Varmarken
 */
public class FlowPatternFinder {

    private final Map<String, Set<String>> dnsMap;
    private final Map<Conversation, List<Packet>> connections = new HashMap<>();

    public FlowPatternFinder(Map<String, Set<String>> dnsMap) {
        this.dnsMap = Objects.requireNonNull(dnsMap);
    }

    private static final Set<String> EMPTY_SET = Collections.unmodifiableSet(new HashSet<>());

    // TODO clean up exceptions etc.
    public void findFlowPattern(PcapHandle pcap, FlowPattern pattern)
            throws PcapNativeException, NotOpenException, TimeoutException {
        try {
            Packet packet;
            while ((packet = pcap.getNextPacketEx()) != null) {

                // For now, we only work support pattern search in TCP over IPv4.
                IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                if (ipPacket == null || tcpPacket == null) {
                    continue;
                }
                String srcAddress = ipPacket.getHeader().getSrcAddr().getHostAddress();
                String dstAddress = ipPacket.getHeader().getDstAddr().getHostAddress();
                int srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
                int dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
                // Is this packet related to the pattern and coming from the cloud server?
                boolean fromServer = dnsMap.getOrDefault(srcAddress, EMPTY_SET).contains(pattern.getHostname());
                // Is this packet related to the pattern and going to the cloud server?
                boolean fromClient = dnsMap.getOrDefault(dstAddress, EMPTY_SET).contains(pattern.getHostname());
                if (!fromServer && !fromClient) {
                    // Packet not related to pattern, skip it.
                    continue;
                }
                if (tcpPacket.getPayload() == null) {
                    // We skip non-payload control packets as these are less predictable and should therefore not be
                    // part of a signature (e.g. receiver can choose not to ACK immediately)
                    continue;
                }

                // Identify conversations (connections/sessions) by the four-tuple (clientIp, clientPort, serverIp, serverPort).
                // TODO: this is strictly not sufficient to differentiate one TCP session from another, but should suffice for now.
                Conversation conversation = fromClient ? new Conversation(srcAddress, srcPort, dstAddress, dstPort) :
                        new Conversation(dstAddress, dstPort, srcAddress, srcPort);
                List<Packet> listWrappedPacket = new ArrayList<>();
                listWrappedPacket.add(packet);
                // Create new conversation entry, or append packet to existing.
                connections.merge(conversation, listWrappedPacket, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                });
            }
        } catch (EOFException eofe) {
            System.out.println("findFlowPattern: finished processing entire file");
            find(pattern);
        }
    }

    private void find(FlowPattern pattern) {
        for (Conversation con : connections.keySet()) {
            List<Packet> packets = connections.get(con);
            if (packets.size() != pattern.getPacketOrder().size()) {
                // Not a complete match if different number of packets.
                continue;
            }
            boolean completeMatch = true;
            for (int i = 0; i < packets.size(); i++) {
                TcpPacket tcpPacket = packets.get(i).get(TcpPacket.class);
                if (tcpPacket.getPayload().length() != pattern.getPacketOrder().get(i)) {
                    completeMatch = false;
                    break;
                }
            }
            if (completeMatch) {
                System.out.println(String.format("found a complete match for %s", pattern.getPatternId()));
            }
        }
    }

    /**
     * Immutable class used for identifying a conversation/connection/session/flow (packet's belonging to the same
     * session between a client and a server).
     */
    private static class Conversation {

        private final String clientIp;
        private final int clientPort;
        private final String serverIp;
        private final int serverPort;

        public Conversation(String clientIp, int clientPort, String serverIp, int serverPort) {
            this.clientIp = clientIp;
            this.clientPort = clientPort;
            this.serverIp = serverIp;
            this.serverPort = serverPort;
        }


        // =========================================================================================================
        // We simply reuse equals and hashCode methods of String.class to be able to use this immutable class as a key
        // in a Map.
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Conversation && this.toString().equals(obj.toString());
        }
        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        // =========================================================================================================

        @Override
        public String toString() {
            return String.format("%s:%d %s:%d", clientIp, clientPort, serverIp, serverPort);
        }
    }

}
