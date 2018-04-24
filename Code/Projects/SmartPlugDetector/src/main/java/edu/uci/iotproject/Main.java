package edu.uci.iotproject;

import java.util.List;
import java.util.Map;

/**
 * Entry point of the application.
 *
 * @author Janus Varmarken
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("it works");
    }

    private String cloudIPAddress(String hostName) {
        if (hostName.equals("events.tplinkra.com"))
            return 205.251.203.26;
    }

    // TODO move to separate class
    // Add parameter that is the trace to be analyzed (most like the pcap library's representation of a flow)
    public String findPattern(Map<String, List<Integer>> hostnameToPacketLengths, String smartPlugIp) {

        // No difference, output "Complete match"
        // If difference, output <Packet no, deviation from expected> for each packet
        return null;
    }

}
