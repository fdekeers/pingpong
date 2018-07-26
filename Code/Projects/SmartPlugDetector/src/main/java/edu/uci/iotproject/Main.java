package edu.uci.iotproject;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.io.TriggerTimesFileReader;
import org.pcap4j.core.*;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.time.Instant;
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
        // -------- 07-19-2018 --------
        TriggerTimesFileReader ttfr = new TriggerTimesFileReader();
//        List<Instant> triggerTimes = ttfr.readTriggerTimes("/Users/varmarken/Downloads/tplink-feb-13-2018.timestamps", false);
        List<Instant> triggerTimes = ttfr.readTriggerTimes("/Users/varmarken/temp/UCI IoT Project/June2018 experiments/tplink/tplink-june-14-2018-timestamps.txt", false);
//        String pcapFile = "/Users/varmarken/Development/Repositories/UCI/NetworkingGroup/smart_home_traffic/Code/Projects/SmartPlugDetector/pcap/wlan1.local.dns.pcap";
        String pcapFile = "/Users/varmarken/temp/UCI IoT Project/June2018 experiments/tplink/tplink.wlan1.local.pcap";
        String tpLinkPlugIp = "192.168.1.159";
        TriggerTrafficExtractor tte = new TriggerTrafficExtractor(pcapFile, triggerTimes, tpLinkPlugIp);
//        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen("/Users/varmarken/temp/traces/output/tplink-filtered.pcap");
        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen("/Users/varmarken/temp/UCI IoT Project/June2018 experiments/tplink/tplink-filtered.pcap");
        DnsMap dnsMap = new DnsMap();
        TcpReassembler tcpReassembler = new TcpReassembler();
        tte.performExtraction(pkt -> {
            try {
                outputter.dump(pkt);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }, dnsMap, tcpReassembler);
        outputter.flush();
        outputter.close();
        // ----------------------------
    }

}


// TP-Link MAC 50:c7:bf:33:1f:09 and usually IP 192.168.1.159 (remember to verify per file)