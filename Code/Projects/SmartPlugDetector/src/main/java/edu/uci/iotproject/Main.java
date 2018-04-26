package edu.uci.iotproject;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.kaitai.struct.KaitaiStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a system that reads PCAP files to compare
 * patterns of DNS hostnames, packet sequences, and packet
 * lengths with training data to determine certain events
 * or actions for smart home devices.
 *
 * @author Janus Varmarken
 * @author Rahmadi Trimananda (rtrimana@uci.edu)
 * @version 0.1
 */
public class Main {


    /** 
     * Private class properties
     */
    private Pcap pcap;
    private List<Pcap.Packet> listPacket;
    private Map<String, List<byte[]>> mapHostnamesToIPAddresses;

    /** 
     * Private class constants
     */
    private static final int DNS_PORT = 53;

    /** 
     * Constructor
     *
     * @param   file    name of the analyzed PCAP file
     */
    public Main(String file) throws IOException {

        pcap = Pcap.fromFile(file);
        listPacket = pcap.packets();
        mapHostnamesToIPAddresses = new HashMap<String, List<byte[]>>();
    }


    /** 
     * Private method that maps DNS hostnames to their
     * respected IP addresses. This method iterates
     * through the List<Pcap.Packet>, gets DNS packets,
     * and gets the IP addresses associated with them.
     */
    private void mapHostnamesToIPAddresses() {

        int counter = 1;
        for(Pcap.Packet packet : listPacket) {
            System.out.print("# " + counter++);
            // Check the packet type
            if (packet._root().hdr().network() == Pcap.Linktype.ETHERNET) {
                EthernetFrame ethFrame = (EthernetFrame) packet.body();
                if (ethFrame.etherType() == EthernetFrame.EtherTypeEnum.IPV4) {
                    Ipv4Packet ip4Packet = (Ipv4Packet) ethFrame.body();

                    System.out.print(" - Protocol: " + ip4Packet.protocol());
                    
                    if (ip4Packet.protocol() == Ipv4Packet.ProtocolEnum.UDP) {
                        // DNS is UDP port 53
                        UdpDatagram udpData = (UdpDatagram) ip4Packet.body();
                        System.out.print(" - Source Port: " + udpData.srcPort());
                        System.out.print(" - Dest Port: " + udpData.dstPort());
                        
                        // Source port 53 means this is DNS response
                        if (udpData.srcPort() == DNS_PORT) {
                            KaitaiStream dnsStream = new ByteBufferKaitaiStream(udpData.body());
                            DnsPacket dnsPacket = new DnsPacket(dnsStream);
                            ArrayList<DnsPacket.Answer> answers = dnsPacket.answers();
                            System.out.print(" - this DNS packet has " + answers.size() + " answers.");
                        }
                    }
                }
            }
            System.out.println();
        }
    }

    /*private String cloudIPAddress(String hostName) {
        if (hostName.equals("events.tplinkra.com"))
            return "205.251.203.26";
        else
            return null;
    }*/

    // TODO move to separate class
    // Add parameter that is the trace to be analyzed (most like the pcap library's representation of a flow)
    public String findPattern(Map<String, List<Integer>> hostnameToPacketLengths, String smartPlugIp) {

        // No difference, output "Complete match"
        // If difference, output <Packet no, deviation from expected> for each packet
        return null;
    }
    
    public static void main(String[] args) {
        System.out.println("it works");
        //String file = "/scratch/traffic_measurements/Switches-Feb2018/wemo/wlan1/wlan1.setup.pcap";
        String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        
        try {
            Main main = new Main(file);
            main.mapHostnamesToIPAddresses();

            /*Pcap data = Pcap.fromFile(file);
            List<Pcap.Packet> listPacket = data.packets();
            System.out.println("Number of packets: " + listPacket.size());
            System.out.println("===================");
            for(Pcap.Packet packet : listPacket) {
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
            }*/
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
