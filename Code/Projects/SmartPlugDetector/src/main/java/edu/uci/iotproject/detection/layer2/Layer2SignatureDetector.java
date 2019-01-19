package edu.uci.iotproject.detection.layer2;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.analysis.UserAction;
import edu.uci.iotproject.detection.AbstractClusterMatcher;
import edu.uci.iotproject.detection.ClusterMatcherObserver;
import edu.uci.iotproject.detection.SignatureDetectorObserver;
import edu.uci.iotproject.io.PcapHandleReader;
import edu.uci.iotproject.io.PrintWriterUtils;
import edu.uci.iotproject.trafficreassembly.layer2.Layer2FlowReassembler;
import edu.uci.iotproject.util.PrintUtils;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.pcap4j.core.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;

/**
 * Performs layer 2 signature detection.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class Layer2SignatureDetector implements PacketListener, ClusterMatcherObserver {

    /**
     * If set to {@code true}, output written to the results file is also dumped to standard out.
     */
    private static boolean DUPLICATE_OUTPUT_TO_STD_OUT = true;

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException {
        if (args.length < 4) {
            String errMsg = String.format("Usage: %s inputPcapFile onSignatureFile offSignatureFile resultsFile [stdOut]" +
                            "\n - inputPcapFile: the target of the detection" +
                            "\n - onSignatureFile: the file that contains the ON signature to search for" +
                            "\n - offSignatureFile: the file that contains the OFF signature to search for" +
                            "\n - resultsFile: where to write the results of the detection" +
                            "\n - stdOut: optional true/false literal indicating if output should also be printed to std out; default is true",
                    Layer2SignatureDetector.class.getSimpleName());
            System.out.println(errMsg);
            return;
        }
        final String pcapFile = args[0];
        final String onSignatureFile = args[1];
        final String offSignatureFile = args[2];
        final String resultsFile = args[3];
        if (args.length == 5) {
            DUPLICATE_OUTPUT_TO_STD_OUT = Boolean.parseBoolean(args[4]);
        }

        // Prepare file outputter.
        File outputFile = new File(resultsFile);
        outputFile.getParentFile().mkdirs();
        final PrintWriter resultsWriter = new PrintWriter(new FileWriter(outputFile));
        // Include metadata as comments at the top
        PrintWriterUtils.println("# Detection results for:", resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("# - inputPcapFile: " + pcapFile, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("# - onSignatureFile: " + onSignatureFile, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        PrintWriterUtils.println("# - onSignatureFile: " + offSignatureFile, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        resultsWriter.flush();

        // Create signature detectors and add observers that output their detected events.
        Layer2SignatureDetector onDetector = new Layer2SignatureDetector(PrintUtils.deserializeSignatureFromFile(onSignatureFile));
        Layer2SignatureDetector offDetector = new Layer2SignatureDetector(PrintUtils.deserializeSignatureFromFile(offSignatureFile));
        onDetector.addObserver((signature, match) -> {
            UserAction event = new UserAction(UserAction.Type.TOGGLE_ON, match.get(0).get(0).getTimestamp());
            PrintWriterUtils.println(event, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        });
        offDetector.addObserver((signature, match) -> {
            UserAction event = new UserAction(UserAction.Type.TOGGLE_OFF, match.get(0).get(0).getTimestamp());
            PrintWriterUtils.println(event, resultsWriter, DUPLICATE_OUTPUT_TO_STD_OUT);
        });

        // Load the PCAP file
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(pcapFile, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(pcapFile);
        }
        PcapHandleReader reader = new PcapHandleReader(handle, p -> true, onDetector, offDetector);
        // Parse the file
        reader.readFromHandle();

        // Flush output to results file and close it.
        resultsWriter.flush();
        resultsWriter.close();
    }

    /**
     * The signature that this {@link Layer2SignatureDetector} is searching for.
     */
    private final List<List<List<PcapPacket>>> mSignature;

    /**
     * The {@link Layer2ClusterMatcher}s in charge of detecting each individual sequence of packets that together make
     * up the the signature.
     */
    private final List<Layer2ClusterMatcher> mClusterMatchers;

    /**
     * For each {@code i} ({@code i >= 0 && i < mPendingMatches.length}), {@code mPendingMatches[i]} holds the matches
     * found by the {@link Layer2ClusterMatcher} at {@code mClusterMatchers.get(i)} that have yet to be "consumed",
     * i.e., have yet to be included in a signature detected by this {@link Layer2SignatureDetector} (a signature can
     * be encompassed of multiple packet sequences occurring shortly after one another on multiple connections).
     */
    private final List<List<PcapPacket>>[] mPendingMatches;

    /**
     * Maps a {@link Layer2ClusterMatcher} to its corresponding index in {@link #mPendingMatches}.
     */
    private final Map<Layer2ClusterMatcher, Integer> mClusterMatcherIds;

    /**
     * In charge of reassembling layer 2 packet flows.
     */
    private final Layer2FlowReassembler mFlowReassembler = new Layer2FlowReassembler();

    private final List<SignatureDetectorObserver> mObservers = new ArrayList<>();

    public Layer2SignatureDetector(List<List<List<PcapPacket>>> searchedSignature) {
        mSignature = Collections.unmodifiableList(searchedSignature);
        List<Layer2ClusterMatcher> clusterMatchers = new ArrayList<>();
        for (List<List<PcapPacket>> cluster : mSignature) {
            Layer2ClusterMatcher clusterMatcher = new Layer2ClusterMatcher(cluster);
            clusterMatcher.addObserver(this);
            clusterMatchers.add(clusterMatcher);
        }
        mClusterMatchers = Collections.unmodifiableList(clusterMatchers);
        mPendingMatches = new List[mClusterMatchers.size()];
        for (int i = 0; i < mPendingMatches.length; i++) {
            mPendingMatches[i] = new ArrayList<>();
        }
        Map<Layer2ClusterMatcher, Integer> clusterMatcherIds = new HashMap<>();
        for (int i = 0; i < mClusterMatchers.size(); i++) {
            clusterMatcherIds.put(mClusterMatchers.get(i), i);
        }
        mClusterMatcherIds = Collections.unmodifiableMap(clusterMatcherIds);
        // Register all cluster matchers to receive a notification whenever a new flow is encountered.
        mClusterMatchers.forEach(cm -> mFlowReassembler.addObserver(cm));
    }


    @Override
    public void gotPacket(PcapPacket packet) {
        // Forward packet processing to the flow reassembler that in turn notifies the cluster matchers as appropriate
        mFlowReassembler.gotPacket(packet);
    }

    @Override
    public void onMatch(AbstractClusterMatcher clusterMatcher, List<PcapPacket> match) {
        // TODO: a cluster matcher found a match
        if (clusterMatcher instanceof Layer2ClusterMatcher) {
            // Add the match at the corresponding index
            mPendingMatches[mClusterMatcherIds.get(clusterMatcher)].add(match);
            checkSignatureMatch();
        }
    }

    public void addObserver(SignatureDetectorObserver observer) {
        mObservers.add(observer);
    }

    public boolean removeObserver(SignatureDetectorObserver observer) {
        return mObservers.remove(observer);
    }


    @SuppressWarnings("Duplicates")
    private void checkSignatureMatch() {
        // << Graph-based approach using Balint's idea. >>
        // This implementation assumes that the packets in the inner lists (the sequences) are ordered by asc timestamp.

        // There cannot be a signature match until each Layer3ClusterMatcher has found a match of its respective sequence.
        if (Arrays.stream(mPendingMatches).noneMatch(l -> l.isEmpty())) {
            // Construct the DAG
            final SimpleDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph =
                    new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            // Add a vertex for each match found by all cluster matchers.
            // And maintain an array to keep track of what cluster matcher each vertex corresponds to
            final List<Vertex>[] vertices = new List[mPendingMatches.length];
            for (int i = 0; i < mPendingMatches.length; i++) {
                vertices[i] = new ArrayList<>();
                for (List<PcapPacket> sequence : mPendingMatches[i]) {
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
            // The source is connected to all vertices that wrap the sequences detected by cluster matcher at index 0.
            // Note: zero cost edges as this is just a dummy link to facilitate search from a common start node.
            for (Vertex v : vertices[0]) {
                DefaultWeightedEdge edge = graph.addEdge(source, v);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Similarly, all vertices that wrap the sequences detected by the last cluster matcher of the signature
            // are connected to the sink node.
            for (Vertex v : vertices[vertices.length-1]) {
                DefaultWeightedEdge edge = graph.addEdge(v, sink);
                graph.setEdgeWeight(edge, 0.0);
            }
            // Now link sequences detected by the cluster matcher at index i to sequences detected by the cluster
            // matcher at index i+1 if they obey the timestamp constraint (i.e., that the latter is later in time than
            // the former).
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
                        mPendingMatches[signatureMatch.size()-1].remove(v.sequence);
                    }
                    // Declare success: notify observers
                    mObservers.forEach(obs -> obs.onSignatureDetected(mSignature,
                            Collections.unmodifiableList(signatureMatch)));
                }
            }
        }
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
