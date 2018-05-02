package edu.uci.iotproject;

import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.DnsPacket;
import org.pcap4j.packet.namednumber.DnsResourceRecordType;

import java.io.EOFException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeoutException;

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
        //final String fileName = "/users/varmarken/Desktop/wlan1.local.dns.pcap";
        final String fileName = "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";

        // ====== Debug code ======
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(fileName, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(fileName);
        }
        FlowPatternFinder fpf = new FlowPatternFinder(handle, FlowPattern.TP_LINK_LOCAL_ON);
        fpf.start();

        // ========================
    }
}
