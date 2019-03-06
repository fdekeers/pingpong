package edu.uci.iotproject.detection.layer3;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.analysis.UserAction;
import edu.uci.iotproject.detection.AbstractClusterMatcher;
import edu.uci.iotproject.detection.ClusterMatcherObserver;
import edu.uci.iotproject.io.PcapHandleReader;
import edu.uci.iotproject.util.PrintUtils;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.pcap4j.core.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Consumer;

/**
 * Detects an event signature that spans one or multiple TCP connections.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class SignatureDetector implements PacketListener, ClusterMatcherObserver {

    // Test client
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
//        if (args.length < 3) {
//            String errMsg = String.format("Usage: %s inputPcapFile onSignatureFile offSignatureFile",
//                    SignatureDetector.class.getSimpleName());
//            System.out.println(errMsg);
//            return;
//        }
//        final String inputPcapFile = args[0];
//        final String onSignatureFile = args[1];
//        final String offSignatureFile = args[2];

        String path = "/scratch/July-2018"; // Rahmadi
//        String path = "/Users/varmarken/temp/UCI IoT Project/experiments"; // Janus
//        String path = "/home/jvarmark/iot_project/datasets"; // Hera (server)
//        String path = "/raid/varmarken/iot_project/datasets"; // Zeus (server)

        // No activity test
        //final String inputPcapFile = path + "/evaluation/no-activity/no-activity.wlan1.pcap";

        // D-Link Siren experiment
//        final String inputPcapFile = path + "/evaluation/dlink-siren/dlink-siren.data.wlan1.pcap";
//        final String inputPcapFile = path + "/evaluation/dlink-siren/dlink-siren.eth0.local.pcap";
        // D-Link Siren DEVICE signatures
//        final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-device.sig";
//        final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-device.sig";
        // D-Link Siren PHONE signatures
//        final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-phone.sig";
//        final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-phone.sig";
        // TODO: EXPERIMENT - November 19, 2018
        // Hue Bulb experiment
//        final String inputPcapFile = path + "/2018-08/hue-bulb/hue-bulb.wlan1.local.pcap";
        // Hue Bulb PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/hue-bulb/signatures/hue-bulb-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/hue-bulb/signatures/hue-bulb-offSignature-phone-side.sig";

        /*
        // Kwikset Doorlock Sep 12 experiment
//        final String inputPcapFile = path + "/evaluation/kwikset-doorlock/kwikset-doorlock.data.wlan1.pcap";
        final String inputPcapFile = path + "/evaluation/kwikset-doorlock/kwikset-doorlock.data.eth0.pcap";
//        // Kwikset Doorlock PHONE signatures
        final String onSignatureFile = path + "/2018-08/kwikset-doorlock/onSignature-Kwikset-Doorlock-phone-new.sig";
        final String offSignatureFile = path + "/2018-08/kwikset-doorlock/offSignature-Kwikset-Doorlock-phone-new.sig";
        */

        // D-Link Plug experiment
        //final String inputPcapFile = path + "/evaluation/dlink/dlink-plug.data.wlan1.pcap";
//        final String inputPcapFile = path + "/evaluation/dlink/dlink-plug.data.eth0.pcap";

        // D-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/2018-07/dlink/onSignature-DLink-Plug-device.sig";
//        final String offSignatureFile = path + "/2018-07/dlink/offSignature-DLink-Plug-device.sig";
        // D-Link Plug PHONE signatures
//        final String onSignatureFile = path + "/2018-07/dlink/onSignature-DLink-Plug-phone.sig";
//        final String offSignatureFile = path + "/2018-07/dlink/offSignature-DLink-Plug-phone.sig";

        // TODO: The following are negative tests against the PCAP file from UNSW
//        final String inputPcapFile = path + "/UNSW/16-10-04.pcap"; // TODO: Seems to be broken! Zero-payload!
//          final String inputPcapFile = path + "/UNSW/16-10-12.pcap";

//        final String inputPcapFile = path + "/UNSW/16-09-28.pcap"; // TODO: Seems to be broken! Zero-payload!
//        final String inputPcapFile = path + "/UNSW/16-10-02.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-03.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-04-a.pcap"; // TODO: Seems to be broken! Zero-payload!
//        final String inputPcapFile = path + "/UNSW/16-10-04-b.pcap"; // TODO: Seems to be broken! Zero-payload!
//        final String inputPcapFile = path + "/UNSW/16-10-07.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-08.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-09.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-10.pcap"; // TODO: Seems to be broken!
//        final String inputPcapFile = path + "/UNSW/16-10-11.pcap"; // TODO: Seems to be broken!
        // TODO: The following one is very long!!! - Split into smaller files!
//        final String inputPcapFile = path + "/UNSW/16-10-12-a.pcap";
//        final String inputPcapFile = path + "/UNSW/16-10-12-b.pcap";
//        final String inputPcapFile = path + "/UNSW/16-10-12-c.pcap";
//        final String inputPcapFile = path + "/UNSW/16-10-12-d.pcap";

//        final String inputPcapFile = path + "/UNSW/16-09-23.pcap";
//        final String inputPcapFile = path + "/UNSW/16-09-24.pcap";
//        final String inputPcapFile = path + "/UNSW/16-09-25.pcap";
//        final String inputPcapFile = path + "/UNSW/16-09-26.pcap";
//        final String inputPcapFile = path + "/UNSW/16-09-27.pcap";
//        final String inputPcapFile = path + "/UNSW/16-09-29.pcap";
//        final String inputPcapFile = path + "/UNSW/16-10-01.pcap";
//        final String inputPcapFile = path + "/UNSW/16-10-06.pcap";
        // Negative test: dataset from UNB
//        final String inputPcapFile = path + "/evaluation/negative-datasets/UNB/Monday-WorkingHours_one-local-endpoint-001.pcap";

        // TODO: The following are tests for signatures against training data

        // D-Link Plug experiment
//        final String inputPcapFile = path + "/training/dlink-plug/wlan1/dlink-plug.wlan1.local.pcap";
        // D-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/training/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/training/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig";
        // D-Link Plug PHONE signatures
//        final String onSignatureFile = path + "/training/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig";

        // TODO: EXPERIMENT - November 7, 2018
        // D-Link Plug experiment
//        //final String inputPcapFile = path + "/experimental_result/standalone/dlink-plug/wlan1/dlink-plug.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/dlink-plug/wlan1/dlink-plug.wlan1.detection.pcap";
//        //final String inputPcapFile = path + "/experimental_result/smarthome/dlink-plug/eth0/dlink-plug.eth0.detection.pcap";
//        // D-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig";
        // D-Link Plug PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig";

        // TODO: EXPERIMENT - November 9, 2018
        // D-Link Siren experiment
        //final String inputPcapFile = path + "/experimental_result/standalone/dlink-siren/wlan1/dlink-siren.wlan1.local.pcap";
        //final String inputPcapFile = path + "/experimental_result/smarthome/dlink-siren/wlan1/dlink-siren.wlan1.detection.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/dlink-siren/eth0/dlink-siren.eth0.detection.pcap";
//        // D-Link Siren DEVICE signatures
//        // TODO: The device signature does not have pairs---only one packet which is 216, so we don't consider this as a signature
//        final String onSignatureFile = path + "/experimental_result/standalone/dlink-siren/signatures/dlink-siren-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/dlink-siren/signatures/dlink-siren-offSignature-device-side.sig";
        // D-Link Siren PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig";
//        final String onSignatureFile = path + "/training/signatures/dlink-siren/dlink-siren-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/signatures/dlink-siren/dlink-siren-offSignature-phone-side.sig";

        // TP-Link Plug experiment
////        final String inputPcapFile = path + "/training/tplink-plug/wlan1/tplink-plug.wlan1.local.pcap";
////        final String inputPcapFile = path + "/experimental_result/wifi-Sniffer/tests2/airtool_2019-01-04_11.08.45.AM.pcap";
//        final String inputPcapFile = path + "/experimental_result/wifi-Sniffer/tests2/command-frames-only.pcap";
//        // TP-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/training/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/training/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig";
        // TODO: EXPERIMENT - November 8, 2018
        // TP-Link Plug experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/tplink-plug/wlan1/tplink-plug.wlan1.local.pcap";
////        final String inputPcapFile = path + "/experimental_result/standalone/tplink-plug/eth0/tplink-plug.eth0.local.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/tplink-plug/wlan1/tplink-plug.wlan1.detection.pcap";
//        //final String inputPcapFile = path + "/experimental_result/smarthome/tplink-plug/eth0/tplink-plug.eth0.detection.pcap";
//        // TP-Link Plug DEVICE signatures
////        final String onSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig";
////        final String offSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig";
//        final String onSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig";
        // TP-Link Plug PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/tplink-plug/signatures/tplink-plug-offSignature-phone-side.sig";

        // Arlo camera experiment
//        final String inputPcapFile = path + "/training/arlo-camera/wlan1/arlo-camera.wlan1.local.pcap";
////        // TP-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/training/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig";
        // TODO: EXPERIMENT - November 13, 2018
        // Arlo Camera experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/arlo-camera/wlan1/arlo-camera.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/standalone/arlo-camera/eth0/arlo-camera.eth0.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/arlo-camera/wlan1/arlo-camera.wlan1.detection.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/arlo-camera/eth0/arlo-camera.eth0.detection.pcap";
////        final String inputPcapFile = path + "/training/arlo-camera/eth0/arlo-camera.eth0.local.pcap";
//        // Arlo Camera PHONE signatures
////        final String onSignatureFile = path + "/experimental_result/standalone/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig";
////        final String offSignatureFile = path + "/experimental_result/standalone/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig";
//        final String onSignatureFile = path + "/experimental_result/standalone/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig.complete";
//        final String offSignatureFile = path + "/experimental_result/standalone/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig.complete";

        // Amazon Alexa experiment
//        final String inputPcapFile = path + "/training/amazon-alexa/wlan1/alexa2.wlan1.local.pcap";
//        // TP-Link Plug DEVICE signatures
//        final String onSignatureFile = path + "/training/amazon-alexa/signatures/amazon-alexa-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/training/amazon-alexa/signatures/amazon-alexa-offSignature-device-side.sig";

        // SmartThings Plug experiment
//        final String inputPcapFile = path + "/training/st-plug/wlan1/st-plug.wlan1.local.pcap";
//        // SmartThings Plug DEVICE signatures
//        //final String onSignatureFile = path + "/training/st-plug/signatures/st-plug-onSignature-device-side.sig";
//        //final String offSignatureFile = path + "/training/st-plug/signatures/st-plug-offSignature-device-side.sig";
//        // SmartThings Plug PHONE signatures
//        final String onSignatureFile = path + "/training/st-plug/signatures/st-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/st-plug/signatures/st-plug-offSignature-phone-side.sig";
        // TODO: EXPERIMENT - November 12, 2018
        // SmartThings Plug experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/st-plug/wlan1/st-plug.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/standalone/st-plug/eth0/st-plug.eth0.local.pcap";
//        //final String inputPcapFile = path + "/experimental_result/smarthome/st-plug/wlan1/st-plug.wlan1.detection.pcap";
        final String inputPcapFile = path + "/experimental_result/smarthome/st-plug/eth0/st-plug.eth0.detection.pcap";
//        // SmartThings Plug PHONE signatures
        final String onSignatureFile = path + "/experimental_result/standalone/st-plug/signatures/st-plug-onSignature-phone-side.sig";
        final String offSignatureFile = path + "/experimental_result/standalone/st-plug/signatures/st-plug-offSignature-phone-side.sig";
//        final String onSignatureFile = path + "/training/signatures/st-plug/st-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/signatures/st-plug/st-plug-offSignature-phone-side.sig";

        // TODO: EXPERIMENT - January 9, 2018
        // Blossom Sprinkler experiment
////        final String inputPcapFile = path + "/experimental_result/standalone/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/blossom-sprinkler/eth0/blossom-sprinkler.eth0.detection.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.detection.pcap";
//        // Blossom Sprinkler DEVICE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig";
//        // Blossom Sprinkler PHONE signatures
////        final String onSignatureFile = path + "/experimental_result/standalone/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-phone-side.sig";
////        final String offSignatureFile = path + "/experimental_result/standalone/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-phone-side.sig";

        // LiFX Bulb experiment
//        final String inputPcapFile = path + "/training/lifx-bulb/wlan1/lifx-bulb.wlan1.local.pcap";
//        // LiFX Bulb DEVICE signatures
//        final String onSignatureFile = path + "/training/lifx-bulb/signatures/lifx-bulb-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/training/lifx-bulb/signatures/lifx-bulb-offSignature-device-side.sig";
        // LiFX Bulb PHONE signatures
//        final String onSignatureFile = path + "/training/lifx-bulb/signatures/lifx-bulb-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/lifx-bulb/signatures/lifx-bulb-offSignature-phone-side.sig";

        // Blossom Sprinkler experiment
//        //final String inputPcapFile = path + "/training/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.local.pcap";
//        final String inputPcapFile = path + "/2018-08/blossom/blossom.wlan1.local.pcap";
//        //final String inputPcapFile = path + "/training/blossom-sprinkler/eth0/blossom-sprinkler.eth0.local.pcap";
//        // Blossom Sprinkler DEVICE signatures
//        final String onSignatureFile = path + "/training/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig";
//        final String offSignatureFile = path + "/training/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig";

        // Nest Thermostat experiment
//        final String inputPcapFile = path + "/training/nest-thermostat/wlan1/nest-thermostat.wlan1.local.pcap";
//        // Nest Thermostat DEVICE signatures
////        final String onSignatureFile = path + "/training/nest-thermostat/signatures/nest-thermostat-onSignature-device-side.sig";
////        final String offSignatureFile = path + "/training/nest-thermostat/signatures/nest-thermostat-offSignature-device-side.sig";
//        // Nest Thermostat PHONE signatures
//        final String onSignatureFile = path + "/training/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig";
//        // TODO: EXPERIMENT - November 15, 2018
        // Nest Thermostat experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/nest-thermostat/wlan1/nest-thermostat.wlan1.local.pcap";
////        final String inputPcapFile = path + "/experimental_result/standalone/nest-thermostat/eth0/nest-thermostat.eth0.local.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/nest-thermostat/wlan1/nest-thermostat.wlan1.detection.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/nest-thermostat/eth0/nest-thermostat.eth0.detection.pcap";
////        // Nest Thermostat PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig";

        /*
        // Hue Bulb experiment
        final String inputPcapFile = path + "/training/hue-bulb/wlan1/hue-bulb.wlan1.local.pcap";
        // Hue Bulb PHONE signatures
        final String onSignatureFile = path + "/training/hue-bulb/signatures/hue-bulb-onSignature-phone-side.sig";
        final String offSignatureFile = path + "/training/hue-bulb/signatures/hue-bulb-offSignature-phone-side.sig";
        */



        // TP-Link Bulb experiment
//        final String inputPcapFile = path + "/training/tplink-bulb/wlan1/tplink-bulb.wlan1.local.pcap";
//        // TP-Link Bulb PHONE signatures
//        final String onSignatureFile = path + "/training/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/training/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig";
        // TODO: EXPERIMENT - November 16, 2018
        // TP-Link Bulb experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/tplink-bulb/wlan1/tplink-bulb.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/standalone/tplink-bulb/eth0/tplink-bulb.eth0.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/tplink-bulb/wlan1/tplink-bulb.wlan1.detection.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/tplink-bulb/eth0/tplink-bulb.eth0.detection.pcap";
//        // TP-Link Bulb PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig";

        /*
        // WeMo Plug experiment
        final String inputPcapFile = path + "/training/wemo-plug/wlan1/wemo-plug.wlan1.local.pcap";
        // WeMo Plug PHONE signatures
        final String onSignatureFile = path + "/training/wemo-plug/signatures/wemo-plug-onSignature-device-side.sig";
        final String offSignatureFile = path + "/training/wemo-plug/signatures/wemo-plug-offSignature-device-side.sig";
        */
        // TODO: EXPERIMENT - November 20, 2018
        // WeMo Plug experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/wemo-plug/wlan1/wemo-plug.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/standalone/wemo-plug/eth0/wemo-plug.eth0.local.pcap";
        // TODO: WE HAVE 4 ADDITIONAL EVENTS (TRIGGERED MANUALLY), SO WE JUST IGNORE THEM BECAUSE THEY HAPPENED BEFORE
        // TODO: THE ACTUAL TRIGGERS
//        final String inputPcapFile = path + "/experimental_result/smarthome/wemo-plug/wlan1/wemo-plug.wlan1.detection.pcap";
////        final String inputPcapFile = path + "/experimental_result/smarthome/wemo-plug/eth0/wemo-plug.eth0.detection.pcap";
//        // WeMo Plug PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig";

        /*
        // WeMo Insight Plug experiment
        final String inputPcapFile = path + "/training/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.local.pcap";
        // WeMo Insight Plug PHONE signatures
        final String onSignatureFile = path + "/training/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-device-side.sig";
        final String offSignatureFile = path + "/training/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-device-side.sig";
        */
        // TODO: EXPERIMENT - November 21, 2018
        // WeMo Insight Plug experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.local.pcap";
//        final String inputPcapFile = path + "/experimental_result/standalone/wemo-insight-plug/eth0/wemo-insight-plug.eth0.local.pcap";
        // TODO: WE HAVE 1 ADDITIONAL EVENT (FROM WEMO PLUG)
//        final String inputPcapFile = path + "/experimental_result/smarthome/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.detection.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/wemo-insight-plug/eth0/wemo-insight-plug.eth0.detection.pcap";
        // WeMo Insight Plug PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig";


        // Kwikset Doorlock Sep 12 experiment
//        final String inputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset3.wlan1.local.pcap";
//        // Kwikset Doorlock PHONE signatures
//        final String onSignatureFile = path + "/2018-08/kwikset-doorlock/onSignature-Kwikset-Doorlock-phone.sig";
//        final String offSignatureFile = path + "/2018-08/kwikset-doorlock/offSignature-Kwikset-Doorlock-phone.sig";
        // TODO: EXPERIMENT - November 10, 2018
        // Kwikset Door lock experiment
//        final String inputPcapFile = path + "/experimental_result/standalone/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.local.pcap";
        //final String inputPcapFile = path + "/experimental_result/smarthome/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.detection.pcap";
//        final String inputPcapFile = path + "/experimental_result/smarthome/kwikset-doorlock/eth0/kwikset-doorlock.eth0.detection.pcap";
//        // Kwikset Door lock PHONE signatures
//        final String onSignatureFile = path + "/experimental_result/standalone/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig";
//        final String offSignatureFile = path + "/experimental_result/standalone/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig";
////        final String onSignatureFile = path + "/training/signatures/kwikset-doorlock/kwikset-doorlock-onSignature-phone-side.sig";
////        final String offSignatureFile = path + "/training/signatures/kwikset-doorlock/kwikset-doorlock-offSignature-phone-side.sig";



        // D-Link Siren experiment
//        final String inputPcapFile = path + "/2018-08/dlink-siren/dlink-siren.wlan1.local.pcap";
        // D-Link Siren DEVICE signatures
        //final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-device.sig";
        //final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-device.sig";
        // D-Link Siren PHONE signatures
//        final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-phone.sig";
//        final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-phone.sig";


        // Output file names used (to make it easy to catch if one forgets to change them)
        System.out.println("ON signature file in use is " + onSignatureFile);
        System.out.println("OFF signature file in use is " + offSignatureFile);
        System.out.println("PCAP file that is the target of detection is " + inputPcapFile);

        List<List<List<PcapPacket>>> onSignature = PrintUtils.deserializeSignatureFromFile(onSignatureFile);
        List<List<List<PcapPacket>>> offSignature = PrintUtils.deserializeSignatureFromFile(offSignatureFile);

        // LAN
//        SignatureDetector onDetector = new SignatureDetector(onSignature, null);
//        SignatureDetector offDetector = new SignatureDetector(offSignature, null);

        // TODO: We need the array that contains other signatures so that we can check for overlap and decide
        // TODO: whether we use conservative or range-based matching
        // Right now we have ON signature as other signature for OFF and OFF signature as other signature for ON
        // In the future, we might have more other signatures
        List<List<List<List<PcapPacket>>>> otherSignaturesOutsideOn = new ArrayList<>();
        otherSignaturesOutsideOn.add(offSignature);
        List<List<List<List<PcapPacket>>>> otherSignaturesOutsideOff = new ArrayList<>();
        otherSignaturesOutsideOff.add(onSignature);

        // WAN
        SignatureDetector onDetector = new SignatureDetector(onSignature, "128.195.205.105", 0,
                otherSignaturesOutsideOn);
        SignatureDetector offDetector = new SignatureDetector(offSignature, "128.195.205.105", 0,
                otherSignaturesOutsideOff);

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).
                withLocale(Locale.US).withZone(ZoneId.of("America/Los_Angeles"));

        // Outputs information about a detected event to std.out
        final Consumer<UserAction> outputter = ua -> {
            String eventDescription;
            switch (ua.getType()) {
                case TOGGLE_ON:
                    eventDescription = "ON";
                    break;
                case TOGGLE_OFF:
                    eventDescription = "OFF";
                    break;
                default:
                    throw new AssertionError("unhandled event type");
            }
            //String output = String.format("[ !!! %s SIGNATURE DETECTED at %s !!! ]",
            //      eventDescription, dateTimeFormatter.format(ua.getTimestamp()));
            String output = String.format("%s",
                    dateTimeFormatter.format(ua.getTimestamp()));
            System.out.println(output);
        };

        // Let's create observers that construct a UserAction representing the detected event.
        final List<UserAction> detectedEvents = new ArrayList<>();
        onDetector.addObserver((searched, match) -> {
            PcapPacket firstPkt = match.get(0).get(0);
            detectedEvents.add(new UserAction(UserAction.Type.TOGGLE_ON, firstPkt.getTimestamp()));
        });
        offDetector.addObserver((searched, match) -> {
            PcapPacket firstPkt = match.get(0).get(0);
            detectedEvents.add(new UserAction(UserAction.Type.TOGGLE_OFF, firstPkt.getTimestamp()));
        });

        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(inputPcapFile, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(inputPcapFile);
        }
        PcapHandleReader reader = new PcapHandleReader(handle, p -> true, onDetector, offDetector);
        reader.readFromHandle();

        //if (onDetector.isConservativeChecking(offSignature)) {
        // System.out.println("Do conservative checking!");
        //} else {
        // TODO: WORK ON THIS RANGE-BASED CHECKING
        // System.out.println("Do range-based checking!");
        //}

        // TODO: need a better way of triggering detection than this...
        onDetector.mClusterMatchers.forEach(cm -> cm.performDetection());
        offDetector.mClusterMatchers.forEach(cm -> cm.performDetection());

        // Sort the list of detected events by timestamp to make it easier to compare it line-by-line with the trigger
        // times file.
        Collections.sort(detectedEvents, Comparator.comparing(UserAction::getTimestamp));

        // Output the detected events
        detectedEvents.forEach(outputter);

        System.out.println("Number of detected events of type " + UserAction.Type.TOGGLE_ON + ": " +
                detectedEvents.stream().filter(ua -> ua.getType() == UserAction.Type.TOGGLE_ON).count());
        System.out.println("Number of detected events of type " + UserAction.Type.TOGGLE_OFF + ": " +
                detectedEvents.stream().filter(ua -> ua.getType() == UserAction.Type.TOGGLE_OFF).count());


        // TODO: Temporary clean up until we clean the pipeline
//      List<UserAction> cleanedDetectedEvents = SignatureDetector.removeDuplicates(detectedEvents);
//      cleanedDetectedEvents.forEach(outputter);

    }

    /**
     * The signature that this {@link SignatureDetector} is searching for.
     */
    private final List<List<List<PcapPacket>>> mSignature;

    /**
     * The {@link Layer3ClusterMatcher}s in charge of detecting each individual sequence of packets that together make up the
     * the signature.
     */
    private final List<Layer3ClusterMatcher> mClusterMatchers;

    /**
     * For each {@code i} ({@code i >= 0 && i < pendingMatches.length}), {@code pendingMatches[i]} holds the matches
     * found by the {@link Layer3ClusterMatcher} at {@code mClusterMatchers.get(i)} that have yet to be "consumed", i.e.,
     * have yet to be included in a signature detected by this {@link SignatureDetector} (a signature can be encompassed
     * of multiple packet sequences occurring shortly after one another on multiple connections).
     */
    private final List<List<PcapPacket>>[] pendingMatches;

    /**
     * Maps a {@link Layer3ClusterMatcher} to its corresponding index in {@link #pendingMatches}.
     */
    private final Map<Layer3ClusterMatcher, Integer> mClusterMatcherIds;

    private final List<SignatureDetectionObserver> mObservers = new ArrayList<>();

    private int mInclusionTimeMillis;

    /**
     * Remove duplicates in {@code List} of {@code UserAction} objects. We need to clean this up for user actions
     * that appear multiple times.
     * TODO: This static method is probably just for temporary and we could get rid of this after we clean up
     * TODO:    the pipeline
     *
     * @param listUserAction A {@link List} of {@code UserAction}.
     *
     */
    public static List<UserAction> removeDuplicates(List<UserAction> listUserAction) {

        // Iterate and check for duplicates (check timestamps)
        Set<Long> epochSecondSet = new HashSet<>();
        // Create a target list for cleaned up list
        List<UserAction> listUserActionClean = new ArrayList<>();
        for(UserAction userAction : listUserAction) {
            // Don't insert if any duplicate is found
            if(!epochSecondSet.contains(userAction.getTimestamp().getEpochSecond())) {
                listUserActionClean.add(userAction);
                epochSecondSet.add(userAction.getTimestamp().getEpochSecond());
            }
        }
        return listUserActionClean;
    }

//    /**
//     * Check if there is any overlap between the signature stored in this class and another signature.
//     * Conditions:
//     * 1) If both signatures do not have any range, then we need to do conservative checking (return true).
//     * 2) If both signatures have the same number of packets/packet lengths, then we check the range; if the
//     *    numbers of packets/packet lengths are different then we assume that there is no overlap.
//     * 3) If there is any range in the signatures, then we need to check for overlap.
//     * 4) If there is overlap for every packet/packet length, then we return false (range-based checking); otherwise,
//     *    true (conservative checking).
//     *
//     * @param otherSignature A {@code List} of {@code List} of {@code List} of {@code PcapPacket} objects to be checked
//     *                       for overlaps with the signature stored in this class.
//     * @return A boolean that is true if there is an overlap; false otherwise.
//     */
//    public boolean isConservativeChecking(List<List<List<PcapPacket>>> otherSignature) {
//
//        // Get the ranges of the two signatures
//        List<List<List<PcapPacket>>> signatureRanges = getSequenceRanges(mSignature);
//        List<List<List<PcapPacket>>> otherSignatureRanges = getSequenceRanges(otherSignature);
//        if (!isRangeBased(signatureRanges) && !isRangeBased(otherSignatureRanges)) {
//            // Conservative checking when there is no range
//            return true;
//        } else if(signatureRanges.size() != otherSignatureRanges.size()) {
//            // The two signatures have different numbers of packets/packet lengths
//            return false;
//        } else {
//            // There is range; check if there is overlap
//            return checkOverlap(signatureRanges, otherSignatureRanges);
//        }
//    }
//
//    /*
//     * Check for overlap since we have range in at least one of the signatures.
//     * Overlap is only true when all ranges overlap. We need to check in order.
//     */
//    private boolean checkOverlap(List<List<List<PcapPacket>>> signatureRanges,
//                                 List<List<List<PcapPacket>>> otherSignatureRanges) {
//
//        for(List<List<PcapPacket>> listListPcapPacket : signatureRanges) {
//            // Lower bound of the range is in index 0
//            // Upper bound of the range is in index 1
//            int sequenceSetIndex = signatureRanges.indexOf(listListPcapPacket);
//            List<PcapPacket> minSequenceSignature = listListPcapPacket.get(0);
//            List<PcapPacket> maxSequenceSignature = listListPcapPacket.get(1);
//            for(PcapPacket pcapPacket : minSequenceSignature) {
//                // Get the lower and upper bounds of the current signature
//                int packetIndex = minSequenceSignature.indexOf(pcapPacket);
//                int lowerBound = pcapPacket.length();
//                int upperBound = maxSequenceSignature.get(packetIndex).length();
//                // Check for range overlap in the other signature!
//                // Check the packet/packet length at the same position
//                List<PcapPacket> minSequenceSignatureOther = otherSignatureRanges.get(sequenceSetIndex).get(0);
//                List<PcapPacket> maxSequenceSignatureOther = otherSignatureRanges.get(sequenceSetIndex).get(1);
//                int lowerBoundOther = minSequenceSignatureOther.get(packetIndex).length();
//                int upperBoundOther = maxSequenceSignatureOther.get(packetIndex).length();
//                if (!(lowerBoundOther <= lowerBound && lowerBound <= upperBoundOther) &&
//                    !(lowerBoundOther <= upperBound && upperBound <= upperBoundOther)) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }
//
//    /*
//     * Check and see if there is any range in the signatures
//     */
//    private boolean isRangeBased(List<List<List<PcapPacket>>> signatureRanges) {
//
//        for(List<List<PcapPacket>> listListPcapPacket : signatureRanges) {
//            // Lower bound of the range is in index 0
//            // Upper bound of the range is in index 1
//            List<PcapPacket> minSequence = listListPcapPacket.get(0);
//            List<PcapPacket> maxSequence = listListPcapPacket.get(1);
//            for(PcapPacket pcapPacket : minSequence) {
//                int index = minSequence.indexOf(pcapPacket);
//                if (pcapPacket.length() != maxSequence.get(index).length()) {
//                    // If there is any packet length that differs in the minSequence
//                    // and maxSequence, then it is range-based
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

    /* Find the sequence with the minimum packet lengths.
     * The second-layer list should contain the minimum sequence for element 0 and maximum sequence for element 1.
     */
    private List<List<List<PcapPacket>>> getSequenceRanges(List<List<List<PcapPacket>>> signature) {

        // Start from the first index
        List<List<List<PcapPacket>>> rangeBasedSequence = new ArrayList<>();
        for(List<List<PcapPacket>> listListPcapPacket : signature) {
            List<List<PcapPacket>> minMaxSequence = new ArrayList<>();
            // Both searches start from index 0
            List<PcapPacket> minSequence = new ArrayList<>(listListPcapPacket.get(0));
            List<PcapPacket> maxSequence = new ArrayList<>(listListPcapPacket.get(0));
            for(List<PcapPacket> listPcapPacket : listListPcapPacket) {
                for(PcapPacket pcapPacket : listPcapPacket) {
                    int index = listPcapPacket.indexOf(pcapPacket);
                    // Set the new minimum if length at the index is minimum
                    if (pcapPacket.length() < minSequence.get(index).length()) {
                        minSequence.set(index, pcapPacket);
                    }
                    // Set the new maximum if length at the index is maximum
                    if (pcapPacket.length() > maxSequence.get(index).length()) {
                        maxSequence.set(index, pcapPacket);
                    }
                }
            }
            // minSequence as element 0 and maxSequence as element 1
            minMaxSequence.add(minSequence);
            minMaxSequence.add(maxSequence);
            rangeBasedSequence.add(minMaxSequence);
        }

        return rangeBasedSequence;
    }

    public SignatureDetector(List<List<List<PcapPacket>>> searchedSignature, String routerWanIp, int inclusionTimeMillis,
                             List<List<List<List<PcapPacket>>>> otherSignatures) {
        // note: doesn't protect inner lists from changes :'(
        mSignature = Collections.unmodifiableList(searchedSignature);
        // Generate corresponding/appropriate ClusterMatchers based on the provided signature
        List<Layer3ClusterMatcher> clusterMatchers = new ArrayList<>();
        for (List<List<PcapPacket>> cluster : mSignature) {
            clusterMatchers.add(new Layer3ClusterMatcher(cluster, routerWanIp, otherSignatures, this));
        }
        mClusterMatchers = Collections.unmodifiableList(clusterMatchers);

        // < exploratory >
        pendingMatches = new List[mClusterMatchers.size()];
        for (int i = 0; i < pendingMatches.length; i++) {
            pendingMatches[i] = new ArrayList<>();
        }
        Map<Layer3ClusterMatcher, Integer> clusterMatcherIds = new HashMap<>();
        for (int i = 0; i < mClusterMatchers.size(); i++) {
            clusterMatcherIds.put(mClusterMatchers.get(i), i);
        }
        mClusterMatcherIds = Collections.unmodifiableMap(clusterMatcherIds);
        mInclusionTimeMillis =
                inclusionTimeMillis == 0 ? TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS : inclusionTimeMillis;
    }

    public void addObserver(SignatureDetectionObserver observer) {
        mObservers.add(observer);
    }

    public boolean removeObserver(SignatureDetectionObserver observer) {
        return mObservers.remove(observer);
    }

    @Override
    public void gotPacket(PcapPacket packet) {
        // simply delegate packet reception to all ClusterMatchers.
        mClusterMatchers.forEach(cm -> cm.gotPacket(packet));
    }

    @Override
    public void onMatch(AbstractClusterMatcher clusterMatcher, List<PcapPacket> match) {
        // Add the match at the corresponding index
        pendingMatches[mClusterMatcherIds.get(clusterMatcher)].add(match);
        checkSignatureMatch();
    }

    private void checkSignatureMatch() {
        // << Graph-based approach using Balint's idea. >>
        // This implementation assumes that the packets in the inner lists (the sequences) are ordered by asc timestamp.

        // There cannot be a signature match until each Layer3ClusterMatcher has found a match of its respective sequence.
        if (Arrays.stream(pendingMatches).noneMatch(l -> l.isEmpty())) {
            // Construct the DAG
            final SimpleDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph =
                    new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            // Add a vertex for each match found by all ClusterMatchers
            // And maintain an array to keep track of what cluster matcher each vertex corresponds to
            final List<Vertex>[] vertices = new List[pendingMatches.length];
            for (int i = 0; i < pendingMatches.length; i++) {
                vertices[i] = new ArrayList<>();
                for (List<PcapPacket> sequence : pendingMatches[i]) {
                    Vertex v = new Vertex(sequence);
                    vertices[i].add(v); // retain reference for later when we are to add edges
                    graph.addVertex(v); // add to vertex to graph
                }
            }
            // Add dummy source and sink vertices to facilitate search.
            final Vertex source = new Vertex(null);
            final Vertex sink = new Vertex(null);
            graph.addVertex(source);
            graph.addVertex(sink);
            // The source is connected to all vertices that wrap the sequences detected by Layer3ClusterMatcher at index 0.
            // Note: zero cost edges as this is just a dummy link to facilitate search from a common start node.
            for (Vertex v : vertices[0]) {
                DefaultWeightedEdge edge = graph.addEdge(source, v);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Similarly, all vertices that wrap the sequences detected by the last Layer3ClusterMatcher of the signature
            // are connected to the sink node.
            for (Vertex v : vertices[vertices.length-1]) {
                DefaultWeightedEdge edge = graph.addEdge(v, sink);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Now link sequences detected by Layer3ClusterMatcher at index i to sequences detected by Layer3ClusterMatcher at index
            // i+1 if they obey the timestamp constraint (i.e., that the latter is later in time than the former).
            for (int i = 0; i < vertices.length; i++) {
                int j = i + 1;
                if (j < vertices.length) {
                    for (Vertex iv : vertices[i]) {
                        PcapPacket ivLast = iv.sequence.get(iv.sequence.size()-1);
                        for (Vertex jv : vertices[j]) {
                            PcapPacket jvFirst = jv.sequence.get(jv.sequence.size()-1);
                            if (ivLast.getTimestamp().isBefore(jvFirst.getTimestamp())) {
                                DefaultWeightedEdge edge = graph.addEdge(iv, jv);
                                // The weight is the duration of the i'th sequence plus the duration between the i'th
                                // and i+1'th sequence.
                                Duration d = Duration.
                                        between(iv.sequence.get(0).getTimestamp(), jvFirst.getTimestamp());
                                // Unfortunately weights are double values, so must convert from long to double.
                                // TODO: need nano second precision? If so, use d.toNanos().
                                // TODO: risk of overflow when converting from long to double..?
                                graph.setEdgeWeight(edge, Long.valueOf(d.toMillis()).doubleValue());
                            }
                            // Alternative version if we cannot assume that sequences are ordered by timestamp:
//                            if (iv.sequence.stream().max(Comparator.comparing(PcapPacket::getTimestamp)).get()
//                                    .getTimestamp().isBefore(jv.sequence.stream().min(
//                                            Comparator.comparing(PcapPacket::getTimestamp)).get().getTimestamp())) {
//
//                            }
                        }
                    }
                }
            }
            // Graph construction complete, run shortest-path to find a (potential) signature match.
            DijkstraShortestPath<Vertex, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(graph);
            GraphPath<Vertex, DefaultWeightedEdge> shortestPath = dijkstra.getPath(source, sink);
            if (shortestPath != null) {
                // The total weight is the duration between the first packet of the first sequence and the last packet
                // of the last sequence, so we simply have to compare the weight against the timeframe that we allow
                // the signature to span. For now we just use the inclusion window we defined for training purposes.
                // Note however, that we must convert back from double to long as the weight is stored as a double in
                // JGraphT's API.
                if (((long)shortestPath.getWeight()) < mInclusionTimeMillis) {
                    // There's a signature match!
                    // Extract the match from the vertices
                    List<List<PcapPacket>> signatureMatch = new ArrayList<>();
                    for(Vertex v : shortestPath.getVertexList()) {
                        if (v == source || v == sink) {
                            // Skip the dummy source and sink nodes.
                            continue;
                        }
                        signatureMatch.add(v.sequence);
                        // As there is a one-to-one correspondence between vertices[] and pendingMatches[], we know that
                        // the sequence we've "consumed" for index i of the matched signature is also at index i in
                        // pendingMatches. We must remove it from pendingMatches so that we don't use it to construct
                        // another signature match in a later call.
                        pendingMatches[signatureMatch.size()-1].remove(v.sequence);
                    }
                    // Declare success: notify observers
                    mObservers.forEach(obs -> obs.onSignatureDetected(mSignature,
                            Collections.unmodifiableList(signatureMatch)));
                }
            }
        }
    }

    /**
     * Used for registering for notifications of signatures detected by a {@link SignatureDetector}.
     */
    interface SignatureDetectionObserver {

        /**
         * Invoked when the {@link SignatureDetector} detects the presence of a signature in the traffic that it's
         * examining.
         * @param searchedSignature The signature that the {@link SignatureDetector} reporting the match is searching
         *                          for.
         * @param matchingTraffic The actual traffic trace that matches the searched signature.
         */
        void onSignatureDetected(List<List<List<PcapPacket>>> searchedSignature,
                                 List<List<PcapPacket>> matchingTraffic);
    }

    /**
     * Encapsulates a {@code List<PcapPacket>} so as to allow the list to be used as a vertex in a graph while avoiding
     * the expensive {@link AbstractList#equals(Object)} calls when adding vertices to the graph.
     * Using this wrapper makes the incurred {@code equals(Object)} calls delegate to {@link Object#equals(Object)}
     * instead of {@link AbstractList#equals(Object)}. The net effect is a faster implementation, but the graph will not
     * recognize two lists that contain the same items--from a value and not reference point of view--as the same
     * vertex. However, this is fine for our purposes -- in fact restricting it to reference equality seems more
     * appropriate.
     */
    private static class Vertex {
        private final List<PcapPacket> sequence;
        private Vertex(List<PcapPacket> wrappedSequence) {
            sequence = wrappedSequence;
        }
    }
}
