package edu.uci.iotproject;

import edu.uci.iotproject.maclayer.MacLayerFlowPattern;
import edu.uci.iotproject.maclayer.MacLayerFlowPatternFinder;
import org.pcap4j.core.*;

import java.io.EOFException;
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
        // -------------------------------------------------------------------------------------------------------------
        // Example/debug code for searching for a pattern at the MAC layer.
//        String fileName = "./pcap/mac-tplink.local.pcapng";
//        PcapHandle handle;
//        try {
//            handle = Pcaps.openOffline(fileName, PcapHandle.TimestampPrecision.NANO);
//        } catch (PcapNativeException pne) {
//            handle = Pcaps.openOffline(fileName);
//        }
//        Arrays.asList(1590, 1590, 1590, 1001, 337, 197, 636, 1311, 177) // Full pattern (all non-zero payload packets).
//        MacLayerFlowPattern pattern = new MacLayerFlowPattern("TP_LINK_LOCAL_OFF_MAC", "50:c7:bf:33:1f:09", Arrays.asList(637, 1312));
//        MacLayerFlowPatternFinder finder = new MacLayerFlowPatternFinder(handle, pattern);
//        finder.findFlowPattern();
        // -------------------------------------------------------------------------------------------------------------

        final String fileName = args.length > 0 ? args[0] : "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        //final String fileName = args.length > 0 ? args[0] : "/scratch/June-2018/TPLink/wlan1/tplink.wlan1.local.pcap";
        final String trainingFileName = "./pcap/TP_LINK_LOCAL_ON_SUBSET.pcap";
//        final String trainingFileName = "./pcap/TP_LINK_LOCAL_ON.pcap";
//
//        // ====== Debug code ======
        PcapHandle handle;
        PcapHandle trainingPcap;
        try {
            handle = Pcaps.openOffline(fileName, PcapHandle.TimestampPrecision.NANO);
            trainingPcap = Pcaps.openOffline(trainingFileName, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(fileName);
            trainingPcap = Pcaps.openOffline(trainingFileName);
        }
//
//        // TODO: The followings are the way to extract multiple hostnames and their associated packet lengths lists
//        //List<String> list = new ArrayList<>();
//        //list.add("events.tplinkra.com");
//        //FlowPattern fp = new FlowPattern("TP_LINK_LOCAL_ON", list, trainingPcap);
//        //List<String> list2 = new ArrayList<>();
//        //list2.add("devs.tplinkcloud.com");
//        //list2.add("events.tplinkra.com");
//        //FlowPattern fp3 = new FlowPattern("TP_LINK_REMOTE_ON", list2, trainingPcap);
//
        FlowPattern fp = new FlowPattern("TP_LINK_LOCAL_ON", "events.tplinkra.com", trainingPcap);
        FlowPatternFinder fpf = new FlowPatternFinder(handle, fp);
        fpf.start();
//
//        // ========================
    }
}
