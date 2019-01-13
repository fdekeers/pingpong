package edu.uci.iotproject.detection;

import edu.uci.iotproject.Conversation;
import org.pcap4j.core.PcapPacket;

import java.util.List;
import java.util.Objects;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
abstract public class AbstractClusterMatcher {

    protected final List<List<PcapPacket>> mCluster;


    protected AbstractClusterMatcher(List<List<PcapPacket>> cluster) {
        // ===================== PRECONDITION SECTION =====================
        cluster = Objects.requireNonNull(cluster, "cluster cannot be null");
        if (cluster.isEmpty() || cluster.stream().anyMatch(inner -> inner.isEmpty())) {
            throw new IllegalArgumentException("cluster is empty (or contains an empty inner List)");
        }
        mCluster = pruneCluster(cluster);
    }

    /**
     * Allows subclasses to specify how to prune input cluster provided to the constructor.
     * @param cluster The input cluster provided to the constructor.
     * @return The pruned cluster to use in place of the input cluster.
     */
    abstract protected List<List<PcapPacket>> pruneCluster(List<List<PcapPacket>> cluster);

    // TODO: move Direction outside Conversation so that this is less confusing.
//    abstract protected Conversation.Direction[] getPacketDirections(List<PcapPacket> packets);

}
