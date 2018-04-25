package edu.uci.iotproject;

/**
 * Entry point of the application.
 *
 * @author Janus Varmarken
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("it works");
        String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        
        try {
            Pcap data = Pcap.fromFile(file);
            //data.hdr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
