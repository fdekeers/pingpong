package edu.uci.iotproject;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapPacket;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Models a (TCP) conversation/connection/session/flow (packet's belonging to the same session between a client and a
 * server).
 * Holds a pair of packet lengths from {@link PcapPacket}s identified as pertaining to the flow.
 * Here we consider pairs of packet lengths, e.g., from device to cloud and cloud to device.
 * We collect these pairs of data points as signatures that we can plot on a graph.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class ConversationPair {

    /* Begin instance properties */
    /**
     * The PrintWriter object that writes data points into file
     */
    private PrintWriter pw;

    /**
     * The direction of conversation
     * true = device to server to device
     */
    private Direction direction;

    /**
     * If this is the first packet processed then the value is true (it is false otherwise).
     */
    private boolean firstPacket;

    /**
     * Count the frequencies of points
     */
    private Map<String, Integer> pointFreq;
    private String dataPoint;

    /**
     * Four possible directions of conversations.
     * E.g., DEVICE_TO_SERVER means the conversation is started from
     * a device-server packet and then a server-device as a response.
     * SERVER_TO_DEVICE means the conversation is started from a
     * server-device packet and then a device-server packet as a response.
     * The same pattern applies to PHONE_TO_SERVER and SERVER_TO_PHONE
     * directions.
     */
    public enum Direction {
        DEVICE_TO_SERVER,
        SERVER_TO_DEVICE,
        PHONE_TO_SERVER,
        SERVER_TO_PHONE
    }

    /**
     * Constructs a ConversationPair object.
     * @param fileName The file name to write data points into.
     * @param direction The direction of the first packet of the pair.
     */
    public ConversationPair(String fileName, Direction direction) {
        try {
            this.pw = new PrintWriter(fileName, "UTF-8");
        } catch(UnsupportedEncodingException |
                FileNotFoundException e) {
            e.printStackTrace();
        }
        this.direction = direction;
        this.firstPacket = true;
        this.pointFreq = new HashMap<>();
        this.dataPoint = null;
    }

    /**
     * Writes conversation pair's packet lengths.
     * @param packet The {@link PcapPacket} object that has packet information.
     * @param fromClient If true then this packet comes from client, e.g., device.
     * @param fromServer If true then this packet comes from server.
     */
    public void writeConversationPair(PcapPacket packet, boolean fromClient, boolean fromServer) {

        // Write device data point first and then server
        if (direction == Direction.DEVICE_TO_SERVER || direction == Direction.PHONE_TO_SERVER) {
            if (fromClient && firstPacket) { // first packet
                pw.print(packet.getTimestamp() + ", " + packet.getPayload().length() + ", ");
                System.out.print(packet.getTimestamp() + ", " + packet.getPayload().length() + ", ");
                dataPoint = Integer.toString(packet.getPayload().length()) + ", ";
                firstPacket = false;
            } else if (fromServer && !firstPacket) { // second packet
                pw.println(packet.getPayload().length());
                System.out.println(packet.getPayload().length());
                dataPoint = dataPoint + Integer.toString(packet.getPayload().length());
                countFrequency(dataPoint);
                firstPacket = true;
            }
        // Write server data point first and then device
        } else if (direction == Direction.SERVER_TO_DEVICE || direction == Direction.SERVER_TO_PHONE) {
            if (fromServer && firstPacket) { // first packet
                pw.print(packet.getTimestamp() + ", " + packet.getPayload().length() + ", ");
                dataPoint = Integer.toString(packet.getPayload().length()) + ", ";
                firstPacket = false;
            } else if (fromClient && !firstPacket) { // second packet
                pw.println(packet.getPayload().length());
                dataPoint = dataPoint + Integer.toString(packet.getPayload().length());
                countFrequency(dataPoint);
                firstPacket = true;
            }
        }
    }

    /**
     * Counts the frequencies of data points.
     * @param dataPoint One data point for a conversation pair, e.g., 556, 1232.
     */
    private void countFrequency(String dataPoint) {

        Integer freq = null;
        if (pointFreq.containsKey(dataPoint)) {
            freq = pointFreq.get(dataPoint);
        } else {
            freq = new Integer(0);
        }
        freq = freq + 1;
        pointFreq.put(dataPoint, freq);
    }

    /**
     * Prints the frequencies of data points from the Map.
     */
    public void printListFrequency() {
        for(Map.Entry<String, Integer> entry : pointFreq.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    /**
     * Close the PrintWriter object.
     */
    public void close() {
        pw.close();
    }
}