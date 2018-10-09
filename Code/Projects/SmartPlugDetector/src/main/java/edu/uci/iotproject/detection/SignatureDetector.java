package edu.uci.iotproject.detection;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.analysis.UserAction;
import edu.uci.iotproject.io.PcapHandleReader;
import edu.uci.iotproject.util.PrintUtils;
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
public class SignatureDetector implements PacketListener, ClusterMatcher.ClusterMatchObserver {

    // Test client
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        //        String path = "/scratch/July-2018"; // Rahmadi
        String path = "/Users/varmarken/temp/UCI IoT Project/experiments"; // Janus

        // Kwikset Doorlock Sep 12 experiment
        final String inputPcapFile = path + "/2018-08/kwikset-doorlock/kwikset3.wlan1.local.pcap";
        // Kwikset Doorlock PHONE signatures
        final String onSignatureFile = path + "/2018-08/kwikset-doorlock/onSignature-Kwikset-Doorlock-phone.sig";
        final String offSignatureFile = path + "/2018-08/kwikset-doorlock/offSignature-Kwikset-Doorlock-phone.sig";

        /*
        // D-Link Plug experiment
        final String inputPcapFile = path + "/2018-07/dlink/dlink.wlan1.local.pcap";
        // D-Link Plug DEVICE signatures
        final String onSignatureFile = path + "/2018-07/dlink/onSignature-DLink-Plug-device.sig";
        final String offSignatureFile = path + "/2018-07/dlink/offSignature-DLink-Plug-device.sig";
        // D-Link Plug PHONE signatures
        final String onSignatureFile = path + "/2018-07/dlink/onSignature-DLink-Plug-phone.sig";
        final String offSignatureFile = path + "/2018-07/dlink/offSignature-DLink-Plug-phone.sig";
        */

        /*
        // D-Link Siren experiment
        final String inputPcapFile = path + "/2018-08/dlink-siren/dlink-siren.wlan1.local.pcap";
        // D-Link Siren DEVICE signatures
        final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-device.sig";
        final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-device.sig";
        // D-Link Siren PHONE signatures
        final String onSignatureFile = path + "/2018-08/dlink-siren/onSignature-DLink-Siren-phone.sig";
        final String offSignatureFile = path + "/2018-08/dlink-siren/offSignature-DLink-Siren-phone.sig";
        */

        List<List<List<PcapPacket>>> onSignature = PrintUtils.deserializeSignatureFromFile(onSignatureFile);
        List<List<List<PcapPacket>>> offSignature = PrintUtils.deserializeSignatureFromFile(offSignatureFile);

        SignatureDetector onDetector = new SignatureDetector(onSignature, null);
        SignatureDetector offDetector = new SignatureDetector(offSignature, null);

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
            String output = String.format("[ !!! %s SIGNATURE DETECTED at %s !!! ]",
                    eventDescription, dateTimeFormatter.format(ua.getTimestamp()));
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

        // TODO: need a better way of triggering detection than this...
        onDetector.mClusterMatchers.forEach(cm -> cm.performDetection());
        offDetector.mClusterMatchers.forEach(cm -> cm.performDetection());

        // Sort the list of detected events by timestamp to make it easier to compare it line-by-line with the trigger
        // times file.
        Collections.sort(detectedEvents, Comparator.comparing(UserAction::getTimestamp));
        // Output the detected events
        detectedEvents.forEach(outputter);
    }

    /**
     * The signature that this {@link SignatureDetector} is searching for.
     */
    private final List<List<List<PcapPacket>>> mSignature;

    /**
     * The {@link ClusterMatcher}s in charge of detecting each individual sequence of packets that together make up the
     * the signature.
     */
    private final List<ClusterMatcher> mClusterMatchers;

    /**
     * For each {@code i} ({@code i >= 0 && i < pendingMatches.length}), {@code pendingMatches[i]} holds the matches
     * found by the {@link ClusterMatcher} at {@code mClusterMatchers.get(i)} that have yet to be "consumed", i.e.,
     * have yet to be included in a signature detected by this {@link SignatureDetector} (a signature can be encompassed
     * of multiple packet sequences occurring shortly after one another on multiple connections).
     */
    private final List<List<PcapPacket>>[] pendingMatches;

    /**
     * Maps a {@link ClusterMatcher} to its corresponding index in {@link #pendingMatches}.
     */
    private final Map<ClusterMatcher, Integer> mClusterMatcherIds;

    private final List<SignatureDetectionObserver> mObservers = new ArrayList<>();

    public SignatureDetector(List<List<List<PcapPacket>>> searchedSignature, String routerWanIp) {
        // note: doesn't protect inner lists from changes :'(
        mSignature = Collections.unmodifiableList(searchedSignature);
        // Generate corresponding/appropriate ClusterMatchers based on the provided signature
        List<ClusterMatcher> clusterMatchers = new ArrayList<>();
        for (List<List<PcapPacket>> cluster : mSignature) {
            clusterMatchers.add(new ClusterMatcher(cluster, routerWanIp, this));
        }
        mClusterMatchers = Collections.unmodifiableList(clusterMatchers);

        // < exploratory >
        pendingMatches = new List[mClusterMatchers.size()];
        for (int i = 0; i < pendingMatches.length; i++) {
            pendingMatches[i] = new ArrayList<>();
        }
        Map<ClusterMatcher, Integer> clusterMatcherIds = new HashMap<>();
        for (int i = 0; i < mClusterMatchers.size(); i++) {
            clusterMatcherIds.put(mClusterMatchers.get(i), i);
        }
        mClusterMatcherIds = Collections.unmodifiableMap(clusterMatcherIds);
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
    public void onMatch(ClusterMatcher clusterMatcher, List<PcapPacket> match) {
        // Add the match at the corresponding index
        pendingMatches[mClusterMatcherIds.get(clusterMatcher)].add(match);
        checkSignatureMatch();
    }

    private void checkSignatureMatch() {
        // << Graph-based approach using Balint's idea. >>
        // This implementation assumes that the packets in the inner lists (the sequences) are ordered by asc timestamp.

        // There cannot be a signature match until each ClusterMatcher has found a match of its respective sequence.
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
            // The source is connected to all vertices that wrap the sequences detected by ClusterMatcher at index 0.
            // Note: zero cost edges as this is just a dummy link to facilitate search from a common start node.
            for (Vertex v : vertices[0]) {
                DefaultWeightedEdge edge = graph.addEdge(source, v);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Similarly, all vertices that wrap the sequences detected by the last ClusterMatcher of the signature
            // are connected to the sink node.
            for (Vertex v : vertices[vertices.length-1]) {
                DefaultWeightedEdge edge = graph.addEdge(v, sink);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Now link sequences detected by ClusterMatcher at index i to sequences detected by ClusterMatcher at index
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
                if (((long)shortestPath.getWeight()) < TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS) {
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