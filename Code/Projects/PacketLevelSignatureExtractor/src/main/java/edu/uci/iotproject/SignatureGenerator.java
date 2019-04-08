package edu.uci.iotproject;

import static edu.uci.iotproject.analysis.UserAction.Type;

import edu.uci.iotproject.analysis.*;
import edu.uci.iotproject.io.PrintWriterUtils;
import edu.uci.iotproject.io.TriggerTimesFileReader;
import edu.uci.iotproject.trafficreassembly.layer3.Conversation;
import edu.uci.iotproject.trafficreassembly.layer3.TcpReassembler;
import edu.uci.iotproject.util.PcapPacketUtils;
import edu.uci.iotproject.util.PrintUtils;
import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.DBSCANClusterer;
import org.pcap4j.core.*;
import org.pcap4j.packet.namednumber.DataLinkType;

import java.io.*;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class SignatureGenerator {

    /**
     * If set to {@code true}, output written to the results file is also dumped to standard out.
     */
    private static boolean DUPLICATE_OUTPUT_TO_STD_OUT = true;
    /**
     * File name for logging.
     */
    private static String LOG_EXTENSION = "_signature-generation.log";
    /**
     * Directory for logging.
     */
    private static String LOG_DIRECTORY = "./";

    public static void main(String[] args) throws PcapNativeException, NotOpenException, EOFException,
            TimeoutException, UnknownHostException, IOException {
        // -------------------------------------------------------------------------------------------------------------
        // ------------ # Code for extracting traffic generated by a device within x seconds of a trigger # ------------
        if (args.length < 11) {
            String errMsg = String.format("SPECTO version 1.0\n" +
                            "Copyright (C) 2018-2019 Janus Varmarken and Rahmadi Trimananda.\n" +
                            "University of California, Irvine.\n" +
                            "All rights reserved.\n\n" +
                            "Usage: %s inputPcapFile outputPcapFile triggerTimesFile deviceIp" +
                            " onSignatureFile offSignatureFile onClusterAnalysisFile offClusterAnalysisFile epsilon" +
                            " deletedSequencesOn deletedSequencesOff" +
                            "\n  inputPcapFile: the target of the detection" +
                            "\n  outputPcapFile: the processed PCAP file through 15-second window filtering" +
                            "\n  triggerTimesFile: the trigger timestamps" +
                            "\n  deviceIp: the IP address of the device we want to generate a signature for" +
                            "\n  onSignatureFile: name of the ON signature file" +
                            "\n  offSignatureFile: name of the OFF signature file" +
                            "\n  onClusterAnalysisFile: name of the ON signature cluster analysis file" +
                            "\n  offClusterAnalysisFile: name of the OFF signature cluster analysis file" +
                            "\n  epsilon: epsilon value of the DBSCAN algorithm" +
                            "\n  deletedSequencesOn: sequences to be deleted from the final ON signature" +
                            " (please separate with commas, e.g., 0,1,2, or put '-1' if not needed)" +
                            "\n  deletedSequencesOff: sequences to be deleted from the final OFF signature" +
                            " (please separate with commas, e.g., 0,1,2, or put '-1' if not needed)",
                    SignatureGenerator.class.getSimpleName());
            System.out.println(errMsg);
            return;
        }
        boolean verbose = true;
        final String inputPcapFile = args[0];
        final String outputPcapFile = args[1];
        final String triggerTimesFile = args[2];
        final String deviceIp = args[3];
        final String onSignatureFile = args[4];
        final String offSignatureFile = args[5];
        final String onClusterAnalysisFile = args[6];
        final String offClusterAnalysisFile = args[7];
        final double eps = Double.parseDouble(args[8]);
        final String deletedSequencesOn = args[9];
        final String deletedSequencesOff = args[10];
        final String logFile = inputPcapFile + LOG_EXTENSION;

        // Prepare file outputter.
        File outputFile = new File(logFile);
        outputFile.getParentFile().mkdirs();
        final PrintWriter resultsWriter = new PrintWriter(new FileWriter(outputFile));

        // =========================================== TRAFFIC FILTERING ============================================

        TriggerTimesFileReader ttfr = new TriggerTimesFileReader();
        List<Instant> triggerTimes = ttfr.readTriggerTimes(triggerTimesFile, false);
        // Tag each trigger with "ON" or "OFF", assuming that the first trigger is an "ON" and that they alternate.
        List<UserAction> userActions = new ArrayList<>();
        for (int i = 0; i < triggerTimes.size(); i++) {
            userActions.add(new UserAction(i % 2 == 0 ? Type.TOGGLE_ON : Type.TOGGLE_OFF, triggerTimes.get(i)));
        }
        TriggerTrafficExtractor tte = new TriggerTrafficExtractor(inputPcapFile, triggerTimes, deviceIp);
        final PcapDumper outputter = Pcaps.openDead(DataLinkType.EN10MB, 65536).dumpOpen(outputPcapFile);
        DnsMap dnsMap = new DnsMap();
        TcpReassembler tcpReassembler = new TcpReassembler();
        TrafficLabeler trafficLabeler = new TrafficLabeler(userActions);
        tte.performExtraction(pkt -> {
            try {
                outputter.dump(pkt);
            } catch (NotOpenException e) {
                e.printStackTrace();
            }
        }, dnsMap, tcpReassembler, trafficLabeler);
        outputter.flush();
        outputter.close();

        if (tte.getPacketsIncludedCount() != trafficLabeler.getTotalPacketCount()) {
            // Sanity/debug check
            throw new AssertionError(String.format("mismatch between packet count in %s and %s",
                    TriggerTrafficExtractor.class.getSimpleName(), TrafficLabeler.class.getSimpleName()));
        }

        // Extract all conversations present in the filtered trace.
        List<Conversation> allConversations = tcpReassembler.getTcpConversations();
        // Group conversations by hostname.
        Map<String, List<Conversation>> convsByHostname =
                TcpConversationUtils.groupConversationsByHostname(allConversations, dnsMap);
        PrintWriterUtils.println("Grouped conversations by hostname.", resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        // For each hostname, count the frequencies of packet lengths exchanged with that hostname.
        final Map<String, Map<Integer, Integer>> pktLenFreqsByHostname = new HashMap<>();
        convsByHostname.forEach((host, convs) -> pktLenFreqsByHostname.put(host,
                TcpConversationUtils.countPacketLengthFrequencies(convs)));
        PrintWriterUtils.println("Counted frequencies of packet lengths exchanged with each hostname.",
                resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        // For each hostname, count the frequencies of packet sequences (i.e., count how many
        // conversations exchange a sequence of packets of some specific lengths).
        final Map<String, Map<String, Integer>> pktSeqFreqsByHostname = new HashMap<>();
        convsByHostname.forEach((host, convs) -> pktSeqFreqsByHostname.put(host,
                TcpConversationUtils.countPacketSequenceFrequencies(convs)));
        PrintWriterUtils.println("Counted frequencies of packet sequences exchanged with each hostname.",
                resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        // For each hostname, count frequencies of packet pairs exchanged
        // with that hostname across all conversations
        final Map<String, Map<String, Integer>> pktPairFreqsByHostname =
                TcpConversationUtils.countPacketPairFrequenciesByHostname(allConversations, dnsMap);
        PrintWriterUtils.println("Counted frequencies of packet pairs per hostname.",
                resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        // For each user action, reassemble the set of TCP connections occurring shortly after
        final Map<UserAction, List<Conversation>> userActionToConversations =
                trafficLabeler.getLabeledReassembledTcpTraffic();
        final Map<UserAction, Map<String, List<Conversation>>> userActionsToConvsByHostname =
                trafficLabeler.getLabeledReassembledTcpTraffic(dnsMap);
        PrintWriterUtils.println("Reassembled TCP conversations occurring shortly after each user event.",
                resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);

        /*
         * NOTE: no need to generate these more complex on/off maps that also contain mappings from hostname and
         * sequence identifiers as we do not care about hostnames and sequences during clustering.
         * We can simply use the UserAction->List<Conversation> map to generate ON/OFF groupings of conversations.
         */
        // Contains all ON events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> ons = new HashMap<>();
        // Contains all OFF events: hostname -> sequence identifier -> list of conversations with that sequence
        Map<String, Map<String, List<Conversation>>> offs = new HashMap<>();
        userActionsToConvsByHostname.forEach((ua, hostnameToConvs) -> {
            Map<String, Map<String, List<Conversation>>> outer = ua.getType() == Type.TOGGLE_ON ? ons : offs;
            hostnameToConvs.forEach((host, convs) -> {
                Map<String, List<Conversation>> seqsToConvs = TcpConversationUtils.
                        groupConversationsByPacketSequence(convs, verbose);
                outer.merge(host, seqsToConvs, (oldMap, newMap) -> {
                    newMap.forEach((sequence, cs) -> oldMap.merge(sequence, cs, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    }));
                    return oldMap;
                });
            });
        });

        // ============================================== PAIR CLUSTERING ============================================
        // TODO: No need to use the more convoluted on/off maps; Can simply use the UserAction->List<Conversation> map
        // TODO: when don't care about hostnames and sequences (see comment earlier).
        // ===========================================================================================================
        List<Conversation> onConversations = userActionToConversations.entrySet().stream().
                filter(e -> e.getKey().getType() == Type.TOGGLE_ON). // drop all OFF events from stream
                map(e -> e.getValue()). // no longer interested in the UserActions
                flatMap(List::stream). // flatten List<List<T>> to a List<T>
                collect(Collectors.toList());
        List<Conversation> offConversations = userActionToConversations.entrySet().stream().
                filter(e -> e.getKey().getType() == Type.TOGGLE_OFF).
                map(e -> e.getValue()).
                flatMap(List::stream).
                collect(Collectors.toList());
        //Collections.sort(onConversations, (c1, c2) -> c1.getPackets().)

        List<PcapPacketPair> onPairs = onConversations.stream().
                map(c -> c.isTls() ? TcpConversationUtils.extractTlsAppDataPacketPairs(c) :
                        TcpConversationUtils.extractPacketPairs(c)).
                flatMap(List::stream). // flatten List<List<>> to List<>
                collect(Collectors.toList());
        List<PcapPacketPair> offPairs = offConversations.stream().
                map(c -> c.isTls() ? TcpConversationUtils.extractTlsAppDataPacketPairs(c) :
                        TcpConversationUtils.extractPacketPairs(c)).
                flatMap(List::stream). // flatten List<List<>> to List<>
                collect(Collectors.toList());
        // Note: need to update the DnsMap of all PcapPacketPairs if we want to use the IP/hostname-sensitive distance.
        Stream.concat(Stream.of(onPairs), Stream.of(offPairs)).flatMap(List::stream).forEach(p -> p.setDnsMap(dnsMap));
        // Perform clustering on conversation logged as part of all ON events.
        // Calculate number of events per type (only ON/only OFF), which means half of the number of all timestamps.
        int numberOfEventsPerType = triggerTimes.size() / 2;
        int lowerBound = numberOfEventsPerType - (int)(numberOfEventsPerType * 0.1);
        int upperBound = numberOfEventsPerType + (int)(numberOfEventsPerType * 0.1);
        int minPts = lowerBound;
        DBSCANClusterer<PcapPacketPair> onClusterer = new DBSCANClusterer<>(eps, minPts);
        List<Cluster<PcapPacketPair>> onClusters = onClusterer.cluster(onPairs);
        // Perform clustering on conversation logged as part of all OFF events.
        DBSCANClusterer<PcapPacketPair> offClusterer = new DBSCANClusterer<>(eps, minPts);
        List<Cluster<PcapPacketPair>> offClusters = offClusterer.cluster(offPairs);
        // Sort the conversations as reference
        List<Conversation> sortedAllConversation = TcpConversationUtils.sortConversationList(allConversations);
        // Output clusters
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("       Clustering results for ON        ", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("       Number of clusters: " + onClusters.size(), resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        int count = 0;
        List<List<List<PcapPacket>>> ppListOfListReadOn = new ArrayList<>();
        List<List<List<PcapPacket>>> ppListOfListListOn = new ArrayList<>();
        List<List<List<PcapPacket>>> corePointRangeSignatureOn = new ArrayList<>();
        for (Cluster<PcapPacketPair> c : onClusters) {
            PrintWriterUtils.println(String.format("<<< Cluster #%02d (%03d points) >>>", ++count, c.getPoints().size()),
                    resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
            PrintWriterUtils.println(PrintUtils.toSummaryString(c), resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
            if(c.getPoints().size() > lowerBound && c.getPoints().size() < upperBound) {
                // Print to file
                List<List<PcapPacket>> ppListOfList = PcapPacketUtils.clusterToListOfPcapPackets(c);
                // Check for overlaps and decide whether to do range-based or conservative checking
                corePointRangeSignatureOn.add(PcapPacketUtils.extractRangeCorePoints(ppListOfList, eps, minPts));
                ppListOfListListOn.add(ppListOfList);
            }
        }
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("       Clustering results for OFF        ", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("       Number of clusters: " + offClusters.size(), resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        count = 0;
        List<List<List<PcapPacket>>> ppListOfListReadOff = new ArrayList<>();
        List<List<List<PcapPacket>>> ppListOfListListOff = new ArrayList<>();
        List<List<List<PcapPacket>>> corePointRangeSignatureOff = new ArrayList<>();
        for (Cluster<PcapPacketPair> c : offClusters) {
            PrintWriterUtils.println(String.format("<<< Cluster #%03d (%06d points) >>>", ++count, c.getPoints().size()),
                    resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
            PrintWriterUtils.println(PrintUtils.toSummaryString(c), resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
            if(c.getPoints().size() > lowerBound && c.getPoints().size() < upperBound) {
                // Print to file
                List<List<PcapPacket>> ppListOfList = PcapPacketUtils.clusterToListOfPcapPackets(c);
                // Check for overlaps and decide whether to do range-based or conservative checking
                corePointRangeSignatureOff.add(PcapPacketUtils.extractRangeCorePoints(ppListOfList, eps, minPts));
                ppListOfListListOff.add(ppListOfList);
            }
        }

        // =========================================== SIGNATURE CREATION ===========================================
        // Concatenate
        ppListOfListListOn = PcapPacketUtils.concatSequences(ppListOfListListOn, sortedAllConversation);
        // Remove sequences in the list that have overlap
        StringTokenizer stringTokenizerOn = new StringTokenizer(deletedSequencesOn, ",");
        while(stringTokenizerOn.hasMoreTokens()) {
            int sequenceToDelete = Integer.parseInt(stringTokenizerOn.nextToken());
            if (sequenceToDelete == -1) { // '-1' means there is no removal
                break;
            }
            PcapPacketUtils.removeSequenceFromSignature(ppListOfListListOn, sequenceToDelete);
        }
        ppListOfListListOn = PcapPacketUtils.sortSequences(ppListOfListListOn);
        PrintWriterUtils.println("Concatenated and sorted ON signature sequences...", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);

        // Concatenate
        ppListOfListListOff = PcapPacketUtils.concatSequences(ppListOfListListOff, sortedAllConversation);
        // Remove sequences in the list that have overlap
        StringTokenizer stringTokenizerOff = new StringTokenizer(deletedSequencesOff, ",");
        while(stringTokenizerOff.hasMoreTokens()) {
            int sequenceToDelete = Integer.parseInt(stringTokenizerOff.nextToken());
            if (sequenceToDelete == -1) { // '-1' means there is no removal
                break;
            }
            PcapPacketUtils.removeSequenceFromSignature(ppListOfListListOff, sequenceToDelete);
        }
        ppListOfListListOff = PcapPacketUtils.sortSequences(ppListOfListListOff);
        PrintWriterUtils.println("Concatenated and sorted OFF signature sequences...", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);

        // Write the signatures into the screen
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("              ON Signature              ", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PcapPacketUtils.printSignatures(ppListOfListListOn, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("              OFF Signature             ", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PcapPacketUtils.printSignatures(ppListOfListListOff, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        // Printing signatures into files
        PrintUtils.serializeIntoFile(onSignatureFile, ppListOfListListOn);
        PrintUtils.serializeIntoFile(offSignatureFile, ppListOfListListOff);
        // Printing cluster analyses into files
        PrintUtils.serializeIntoFile(onClusterAnalysisFile, corePointRangeSignatureOn);
        PrintUtils.serializeIntoFile(offClusterAnalysisFile, corePointRangeSignatureOff);

        // =========================================== SIGNATURE DURATIONS =============================================
        List<Instant> firstSignatureTimestamps = new ArrayList<>();
        List<Instant> lastSignatureTimestamps = new ArrayList<>();
        if (!ppListOfListListOn.isEmpty()) {
            List<List<PcapPacket>> firstListOnSign = ppListOfListListOn.get(0);
            List<List<PcapPacket>> lastListOnSign = ppListOfListListOn.get(ppListOfListListOn.size() - 1);
            // Load ON signature first and last packet's timestamps
            for (List<PcapPacket> list : firstListOnSign) {
                // Get timestamp Instant from the last packet
                firstSignatureTimestamps.add(list.get(0).getTimestamp());
            }
            for (List<PcapPacket> list : lastListOnSign) {
                // Get timestamp Instant from the last packet
                int lastPacketIndex = list.size() - 1;
                lastSignatureTimestamps.add(list.get(lastPacketIndex).getTimestamp());
            }
        }

        if (!ppListOfListListOff.isEmpty()) {
            List<List<PcapPacket>> firstListOffSign = ppListOfListListOff.get(0);
            List<List<PcapPacket>> lastListOffSign = ppListOfListListOff.get(ppListOfListListOff.size() - 1);
            // Load OFF signature first and last packet's timestamps
            for (List<PcapPacket> list : firstListOffSign) {
                // Get timestamp Instant from the last packet
                firstSignatureTimestamps.add(list.get(0).getTimestamp());
            }
            for (List<PcapPacket> list : lastListOffSign) {
                // Get timestamp Instant from the last packet
                int lastPacketIndex = list.size() - 1;
                lastSignatureTimestamps.add(list.get(lastPacketIndex).getTimestamp());
            }
        }
        // Sort the timestamps
        firstSignatureTimestamps.sort(Comparator.comparing(Instant::toEpochMilli));
        lastSignatureTimestamps.sort(Comparator.comparing(Instant::toEpochMilli));

        Iterator<Instant> iterFirst = firstSignatureTimestamps.iterator();
        Iterator<Instant> iterLast = lastSignatureTimestamps.iterator();
        long duration;
        long maxDuration = Long.MIN_VALUE;
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("           Signature Durations          ", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        while (iterFirst.hasNext() && iterLast.hasNext()) {
            Instant firstInst = iterFirst.next();
            Instant lastInst = iterLast.next();
            Duration dur = Duration.between(firstInst, lastInst);
            duration = dur.toMillis();
            // Check duration --- should be below 15 seconds
            if (duration > TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS) {
                while (duration > TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS && iterFirst.hasNext()) {
                    // that means we have to move to the next trigger
                    firstInst = iterFirst.next();
                    dur = Duration.between(firstInst, lastInst);
                    duration = dur.toMillis();
                }
            } else { // Below 0/Negative --- that means we have to move to the next signature
                while (duration < 0 && iterLast.hasNext()) {
                    // that means we have to move to the next trigger
                    lastInst = iterLast.next();
                    dur = Duration.between(firstInst, lastInst);
                    duration = dur.toMillis();
                }
            }
            PrintWriterUtils.println(duration, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
            // Update duration if this bigger than the max value and still less than the window inclusion time
            maxDuration = maxDuration < duration && duration <= TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS ?
                    duration : maxDuration;
        }
        // Just assign the value 0 if there is no signature
        if (maxDuration == Long.MIN_VALUE) {
            maxDuration = 0;
        }
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("Max signature duration: " + maxDuration, resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("========================================", resultsWriter,
                DUPLICATE_OUTPUT_TO_STD_OUT);
        resultsWriter.flush();
        resultsWriter.close();
        // ==========================================================================================================
    }
}