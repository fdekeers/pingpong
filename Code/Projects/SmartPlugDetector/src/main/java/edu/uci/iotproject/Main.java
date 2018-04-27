package edu.uci.iotproject;


import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.DnsPacket;
import org.pcap4j.packet.namednumber.DnsResourceRecordType;

import java.io.EOFException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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


    public static void main(String[] args) throws PcapNativeException, NotOpenException, EOFException, TimeoutException, UnknownHostException {
        final String fileName = "/users/varmarken/Desktop/wlan1.local.dns.pcap";
        List<DnsPacket> dnsPackets = extractDnsAnswerPackets(fileName);
        Map<String, List<String>> ipToHostnameMap = constructIpToHostnameMap(dnsPackets);
        ipToHostnameMap.forEach((k,v) -> System.out.println(String.format("%s => %s", k, v.toString())));
    }

    private static List<DnsPacket> extractDnsAnswerPackets(String pcapFileName) throws PcapNativeException, NotOpenException, TimeoutException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(pcapFileName, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(pcapFileName);
        }
        // Apparently BPFs don't support "dns" protocol filter, so have to filter by port.
        handle.setFilter("port 53", BpfProgram.BpfCompileMode.OPTIMIZE);
        ArrayList<DnsPacket> result = new ArrayList<>();
        try {
            Packet packet;
            while ((packet = handle.getNextPacketEx()) != null) {
                DnsPacket dnsPacket = packet.get(DnsPacket.class);
                // We only care about DNS answers.
                if (dnsPacket != null && dnsPacket.getHeader().getAnswers().size() != 0) {
                    result.add(dnsPacket);
                }
            }
        } catch (EOFException eof) {
            // (Note have to resort to EOFException as handle.getStats().getNumPacketsCaptured() only works on Windows)
            // Clean up.
            handle.close();
        }
        System.out.println(String.format("Found %d DNS answers", result.size()));
        return result;
    }

    private static Map<String, List<String>> constructIpToHostnameMap(List<DnsPacket> dnsPackets) throws UnknownHostException {
        HashMap<String, List<String>> result = new HashMap<>();
        for(DnsPacket dnsPacket : dnsPackets) {
            // The hostname that this DNS reply provides answers for.
            // TODO: safe to assume only one question?
            String hostname = dnsPacket.getHeader().getQuestions().get(0).getQName().getName();
            for(DnsResourceRecord answer : dnsPacket.getHeader().getAnswers()) {
                // We only care about type A records
                if (!answer.getDataType().equals(DnsResourceRecordType.A)) {
                    continue;
                }
                // Sanity check. For some reason the hostname appears to be the empty string in the answer .
                // We hence have to assume that all answers correspond to a single question that holds the hostname as part of its object tree.
                // Therefore, if there are more questions in one query-reply exchange, we are in trouble.
                if (!answer.getName().getName().equals("") && !answer.getName().getName().equals(hostname)) {
                    throw new RuntimeException("[DNS parser] mismatch between hostname in question and hostname in answer");
                }
                // The IP in byte representation.
                byte[] ipBytes = answer.getRData().getRawData();
                // Convert to string representation.
                String ip = Inet4Address.getByAddress(ipBytes).getHostAddress();
                List<String> hostnameList = new ArrayList<>();
                hostnameList.add(hostname);
                // Update or insert depending on presence of key:
                // Concat the existing list and the new list if ip already present as key,
                // otherwise add an entry for ip pointing to new list.
                result.merge(ip, hostnameList, (v1, v2) -> { v1.addAll(v2); return v1; });
            }
        }
        return result;
    }

//    /**
//     * Private class properties
//     */
//    private Pcap pcap;
//    private List<Pcap.Packet> listPacket;
//    private Map<String, String> mapIPAddressToHostname;
//
//    /**
//     * Private class constants
//     */
//    private static final int DNS_PORT = 53;
//
//    /**
//     * Constructor
//     *
//     * @param   file    name of the analyzed PCAP file
//     */
//    public Main(String file) throws IOException {
//
//        pcap = Pcap.fromFile(file);
//        listPacket = pcap.packets();
//        mapIPAddressToHostname = new HashMap<String, String>();
//    }
//
//
//
//
//
//    /**
//     * Private method that maps DNS hostnames to their
//     * respected IP addresses. This method iterates
//     * through the List<Pcap.Packet>, gets DNS packets,
//     * and gets the IP addresses associated with them.
//     */
//    private void mapHostnamesToIPAddresses() {
//
//        int counter = 1;
//        for(Pcap.Packet packet : listPacket) {
//            System.out.print("# " + counter++);
//            // Check the packet type
//            if (packet._root().hdr().network() == Pcap.Linktype.ETHERNET) {
//                EthernetFrame ethFrame = (EthernetFrame) packet.body();
//                if (ethFrame.etherType() == EthernetFrame.EtherTypeEnum.IPV4) {
//                    Ipv4Packet ip4Packet = (Ipv4Packet) ethFrame.body();
//
//                    System.out.print(" - Protocol: " + ip4Packet.protocol());
//                    if (ip4Packet.protocol() == Ipv4Packet.ProtocolEnum.UDP) {
//                        // DNS is UDP port 53
//                        UdpDatagram udpData = (UdpDatagram) ip4Packet.body();
//                        System.out.print(" - Source Port: " + udpData.srcPort());
//                        System.out.print(" - Dest Port: " + udpData.dstPort());
//
//                        // Source port 53 means this is DNS response
//                        if (udpData.srcPort() == DNS_PORT) {
//                            KaitaiStream dnsStream = new ByteBufferKaitaiStream(udpData.body());
//                            DnsPacket dnsPacket = new DnsPacket(dnsStream);
//                            ArrayList<DnsPacket.Query> queries = dnsPacket.queries();
//                            ArrayList<DnsPacket.Answer> answers = dnsPacket.answers();
//                            String strDomainName = new String();
//                            for(DnsPacket.Query query : queries) {
//                                System.out.print(" - Queries: ");
//                                DnsPacket.DomainName domainName = query.name();
//                                ArrayList<DnsPacket.Label> labels = domainName.name();
//                                for(int i = 0; i < labels.size(); i++) {
//                                    System.out.print(labels.get(i).name());
//                                    strDomainName = strDomainName + labels.get(i).name();
//                                    if(i < labels.size()-2) {
//                                        System.out.print(".");
//                                        strDomainName = strDomainName + ".";
//                                    }
//                                }
//                                break;  // We are assuming that there is only one query
//                            }
//                            System.out.print(" - Answers " + answers.size());
//                            for(DnsPacket.Answer answer : answers) {
//                                System.out.print(" - TypeType: " + answer.type());
//                                System.out.print(" - ClassType: " + answer.answerClass());
//                                System.out.print("\n - Answers: ");
//                                DnsPacket.Address address = answer.address();
//                                if (answer.type() == DnsPacket.TypeType.A) {
//                                    String strAnswer = new String();
//                                    ArrayList<Integer> ipList = address.ip();
//                                    for(int i = 0; i < ipList.size(); i++) {
//                                        System.out.print(ipList.get(i));
//                                        strAnswer = strAnswer + Integer.toString(ipList.get(i));
//                                        if(i < ipList.size()-1) {
//                                            System.out.print(".");
//                                            strAnswer = strAnswer + ".";
//                                        }
//                                    }
//                                    mapIPAddressToHostname.put(strAnswer, strDomainName);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            System.out.println();
//        }
////        for(Map.Entry<String, String> entry : mapIPAddressToHostname.entrySet()) {
////            if (entry.getValue().equals("devs.tplinkcloud.com")) {
////                System.out.println(entry.getKey() + " - " + entry.getValue());
////            }
////        }
//        System.out.println("Total map size: " + mapIPAddressToHostname.size());
//        System.out.println("Answer for 13.33.41.8: " + mapIPAddressToHostname.get("13.33.41.8"));
//        System.out.println("Answer for 34.226.240.125: " + mapIPAddressToHostname.get("34.226.240.125"));
//    }
//
//    /*private String cloudIPAddress(String hostName) {
//        if (hostName.equals("events.tplinkra.com"))
//            return "205.251.203.26";
//        else
//            return null;
//    }*/
//
//    // TODO move to separate class
//    // Add parameter that is the trace to be analyzed (most like the pcap library's representation of a flow)
//    public String findPattern(Map<String, List<Integer>> hostnameToPacketLengths, String smartPlugIp) {
//
//        // No difference, output "Complete match"
//        // If difference, output <Packet no, deviation from expected> for each packet
//        return null;
//    }
//
//    public static void main(String[] args) {
//        System.out.println("it works");
//
//        //String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
//        String file = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.remote.dns.pcap";
//
//        try {
//            Main main = new Main(file);
//            main.mapHostnamesToIPAddresses();
//
//            /*Pcap data = Pcap.fromFile(file);
//            List<Pcap.Packet> listPacket = data.packets();
//            System.out.println("Number of packets: " + listPacket.size());
//            System.out.println("===================");
//            for(Pcap.Packet packet : listPacket) {
//                if (packet._root().hdr().network() == Pcap.Linktype.ETHERNET) {
//                    EthernetFrame eFrame = (EthernetFrame) packet.body();
//                    if (eFrame.etherType() == EthernetFrame.EtherTypeEnum.IPV4) {
//                        Ipv4Packet ip4Packet = (Ipv4Packet) eFrame.body();
//                        byte[] srcIp = ip4Packet.srcIpAddr();
//                        byte[] dstIp = ip4Packet.dstIpAddr();
//                        System.out.println("Byte length source: " + srcIp.length + " Byte length dest: " + dstIp.length);
//                        System.out.print("Source: ");
//                        for(int i = 0; i < srcIp.length; i++) {
//                            System.out.print(Byte.toUnsignedInt(srcIp[i]));
//                            if(i < srcIp.length-1)
//                                System.out.print(".");
//                        }
//                        System.out.print(" - Dest: ");
//                        for(int i = 0; i < dstIp.length; i++) {
//                            System.out.print(Byte.toUnsignedInt(dstIp[i]));
//                            if(i < dstIp.length-1)
//                                System.out.print(".");
//                            else
//                                System.out.println("\n");
//                        }
//                    }
//                }
//            }*/
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
