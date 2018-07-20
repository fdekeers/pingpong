package edu.uci.iotproject;

import edu.uci.iotproject.analysis.PcapPacketPair;
import edu.uci.iotproject.analysis.TcpConversationUtils;
import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.io.TriggerTimesFileReader;
import org.pcap4j.core.*;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
//
//        //final String fileName = args.length > 0 ? args[0] : "/home/rtrimana/pcap_processing/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
//        final String fileName = args.length > 0 ? args[0] : "/scratch/June-2018/TPLink/wlan1/tplink.wlan1.local.pcap";
//        //final String fileName = args.length > 0 ? args[0] : "/scratch/June-2018/DLink/wlan1/dlink.wlan1.local.pcap";
//        final String trainingFileName = "./pcap/TP_LINK_LOCAL_ON_SUBSET.pcap";
////        final String trainingFileName = "./pcap/TP_LINK_LOCAL_ON.pcap";
////
////        // ====== Debug code ======
//        PcapHandle handle;
//        PcapHandle trainingPcap;
//        try {
//            handle = Pcaps.openOffline(fileName, PcapHandle.TimestampPrecision.NANO);
//            trainingPcap = Pcaps.openOffline(trainingFileName, PcapHandle.TimestampPrecision.NANO);
//        } catch (PcapNativeException pne) {
//            handle = Pcaps.openOffline(fileName);
//            trainingPcap = Pcaps.openOffline(trainingFileName);
//        }
////
////        // TODO: The followings are the way to extract multiple hostnames and their associated packet lengths lists
////        //List<String> list = new ArrayList<>();
////        //list.add("events.tplinkra.com");
////        //FlowPattern fp = new FlowPattern("TP_LINK_LOCAL_ON", list, trainingPcap);
////        //List<String> list2 = new ArrayList<>();
////        //list2.add("devs.tplinkcloud.com");
////        //list2.add("events.tplinkra.com");
////        //FlowPattern fp3 = new FlowPattern("TP_LINK_REMOTE_ON", list2, trainingPcap);
////
//        FlowPattern fp = new FlowPattern("TP_LINK_LOCAL_ON", "events.tplinkra.com", trainingPcap);
//        //FlowPattern fp = new FlowPattern("DLINK_LOCAL_ON", "rfe-us-west-1.dch.dlink.com", trainingPcap);
//        FlowPatternFinder fpf = new FlowPatternFinder(handle, fp);
//        fpf.start();
////
////        // ========================

        /*
        PcapReader pcapReader = new PcapReader(args[0]);
        PcapProcessingPipeline pipeline = new PcapProcessingPipeline(pcapReader);
        TcpReassembler tcpReassembler = new TcpReassembler();
        pipeline.addPcapPacketConsumer(tcpReassembler);
        pipeline.executePipeline();
        System.out.println("Pipeline terminated");

        List<List<PcapPacketPair>> pairs = new ArrayList<>();
        for (Conversation c : tcpReassembler.getTcpConversations()) {
            pairs.add(TcpConversationUtils.extractPacketPairs(c));
        }
        */

        /*
        // -------- 07-17-2018 --------
        // Only consider packets to/from the TP-Link plug.
        PcapReader pcapReader = new PcapReader(args[0], "ip host 192.168.1.159");
        TcpReassembler tcpReassembler = new TcpReassembler();
        PcapPacket packet;
        while((packet = pcapReader.readNextPacket()) != null) {
            tcpReassembler.consumePacket(packet);
        }
        // Now we have a set of reassembled TCP conversations.
        List<Conversation> conversations = tcpReassembler.getTcpConversations();
        for(Conversation c : conversations) {
            List<PcapPacketPair> pairs = TcpConversationUtils.extractPacketPairs(c);
            for (PcapPacketPair pair : pairs) {
                // TODO ...
                // 1. discard packets that are not within X seconds after trigger time
                // 2. conversations may be (are) with different servers - so need to plot in different plots, one per hostname?
            }
        }

        // ----------------------------
        */

        // -------- 07-19-2018 --------
        TriggerTimesFileReader ttfr = new TriggerTimesFileReader();
        List<Instant> triggerTimes = ttfr.readTriggerTimes("/Users/varmarken/Downloads/tplink-feb-13-2018.timestamps", false);
//        triggerTimes.stream().forEach(i -> System.out.println(i.atZone(TriggerTimesFileReader.ZONE_ID_LOS_ANGELES).toString()));
        String pcapFile = "/Users/varmarken/Development/Repositories/UCI/NetworkingGroup/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        String tpLinkPlugIp = "192.168.1.159";
        TriggerTrafficExtractor tte = new TriggerTrafficExtractor(pcapFile, triggerTimes, tpLinkPlugIp);
        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen("/Users/varmarken/temp/traces/output/tplink-filtered.pcap");
        tte.performExtraction(pkt -> {
            try {
                outputter.dump(pkt);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        });
        outputter.flush();
        outputter.close();
        // ----------------------------


    }

}


// TP-Link MAC 50:c7:bf:33:1f:09 and usually IP 192.168.1.159 (remember to verify per file)