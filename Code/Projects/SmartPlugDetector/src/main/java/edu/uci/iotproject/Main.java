package edu.uci.iotproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entry point of the application.
 *
 * @author Janus Varmarken
 */
public class Main {
    private static int counter = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("it works");
        //String file = "/scratch/traffic_measurements/Switches-Feb2018/wemo/wlan1/wlan1.setup.pcap";
        String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        
        try {
            Pcap data = Pcap.fromFile(file);
            //data.hdr();
            List<Pcap.Packet> listPacket = data.packets();
            System.out.println("Number of packets: " + listPacket.size());
            System.out.println("===================");
            for(Pcap.Packet packet : listPacket) {
                System.out.print("#" + counter++ + "\n");
                if (packet._root().hdr().network() == Pcap.Linktype.ETHERNET) {
                    EthernetFrame eFrame = (EthernetFrame) packet.body();
                    if (eFrame.etherType() == EthernetFrame.EtherTypeEnum.IPV4) {
                        Ipv4Packet ip4Packet = (Ipv4Packet) eFrame.body();
                        byte[] srcIp = ip4Packet.srcIpAddr();
                        byte[] dstIp = ip4Packet.dstIpAddr();
                        System.out.println("Byte length source: " + srcIp.length + " Byte length dest: " + dstIp.length);
                        System.out.print("Source: ");
                        for(int i = 0; i < srcIp.length; i++) {
                            System.out.print(Byte.toUnsignedInt(srcIp[i]));
                            if(i < srcIp.length-1)
                                System.out.print(".");
                        }
                        System.out.print(" - Dest: ");
                        for(int i = 0; i < dstIp.length; i++) {
                            System.out.print(Byte.toUnsignedInt(dstIp[i]));
                            if(i < dstIp.length-1)
                                System.out.print(".");
                            else
                                System.out.println("\n");
                        }
                    }
                }
            }
            
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
