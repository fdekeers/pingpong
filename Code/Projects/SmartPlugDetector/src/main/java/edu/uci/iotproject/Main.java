package edu.uci.iotproject;

import java.util.List;
import java.util.Map;

/**
 * Entry point of the application.
 *
 * @author Janus Varmarken
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("it works");
        String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcapdump";
        
        try {
            Pcap data = Pcap.fromFile(file);
            //data.hdr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String cloudIPAddress(String hostName) {
        if (hostName.equals("events.tplinkra.com"))
            return "205.251.203.26";
        else
            return null;
    }

    // TODO move to separate class
    // Add parameter that is the trace to be analyzed (most like the pcap library's representation of a flow)
    public String findPattern(Map<String, List<Integer>> hostnameToPacketLengths, String smartPlugIp) {

        // No difference, output "Complete match"
        // If difference, output <Packet no, deviation from expected> for each packet
        return null;
    }
}
