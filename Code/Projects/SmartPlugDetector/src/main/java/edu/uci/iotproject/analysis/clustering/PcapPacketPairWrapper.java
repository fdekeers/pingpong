package edu.uci.iotproject.analysis.clustering;

import edu.uci.iotproject.DnsMap;
import edu.uci.iotproject.analysis.PcapPacketPair;
import org.apache.commons.math3.stat.clustering.Clusterable;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.uci.iotproject.util.PcapPacketUtils.getSourceIp;

/**
 * A wrapper for a {@link PcapPacketPair}, allowing it to be clustered using
 * {@link org.apache.commons.math3.stat.clustering.DBSCANClusterer}. Specifically, this wrapper implements
 * {@link org.apache.commons.math3.stat.clustering.Clusterable}, so that the interface of {@link PcapPacketPair}
 * is not cluttered up by this helper method of the clustering API.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PcapPacketPairWrapper implements Clusterable<PcapPacketPair> {

    /**
     * The wrapped {@link PcapPacketPair}.
     */
    private final PcapPacketPair mPktPair;

    /**
     * IP to hostname mappings.
     * Allows for grouping packets with different source IPs that map to the same hostname into one cluster.
     */
    private final DnsMap mDnsMap;

    public PcapPacketPairWrapper(PcapPacketPair wrappedObject, DnsMap ipHostnameMap) {
        mPktPair = wrappedObject;
        mDnsMap = ipHostnameMap;
    }

    @Override
    public double distanceFrom(PcapPacketPair that) {
        // Extract src ips of both packets of each pair.
        String thisSrc1 = getSourceIp(mPktPair.getFirst());
        String thisSrc2 = mPktPair.getSecond().map(pp -> getSourceIp(pp)).orElse("");
        String thatSrc1 = getSourceIp(that.getFirst());
        String thatSrc2 = that.getSecond().map(pp -> getSourceIp(pp)).orElse("");

        // Replace IPs with hostnames if possible.
        thisSrc1 = mapToHostname(thisSrc1);
        thisSrc2 = mapToHostname(thisSrc2);
        thatSrc1 = mapToHostname(thatSrc1);
        thatSrc2 = mapToHostname(thatSrc2);

        if(!thisSrc1.equals(thatSrc1) || !thisSrc2.equals(thatSrc2)) {
            // Distance is maximal if sources differ.
            return Double.MAX_VALUE;
        }

        // If the sources match, the distance is the Euclidean distance between each pair of packet lengths.
        int thisLen1 = mPktPair.getFirst().getOriginalLength();
        // TODO should discard pairs w/o second packet from clustering; replace below with getSecond().get() when done.
        int thisLen2 = mPktPair.getSecond().map(pp -> pp.getOriginalLength()).orElse(0);
        int thatLen1 = that.getFirst().getOriginalLength();
        // TODO should discard pairs w/o second packet from clustering; replace below with getSecond().get() when done.
        int thatLen2 = that.getSecond().map(pp -> pp.getOriginalLength()).orElse(0);
        return Math.sqrt(
                Math.pow(thisLen1 - thatLen1, 2) +
                        Math.pow(thisLen2 - thatLen2, 2)
        );
    }

    @Override
    public PcapPacketPair centroidOf(Collection<PcapPacketPair> p) {
        // No notion of centroid in DBSCAN
        throw new UnsupportedOperationException("Not implemented; no notion of a centroid in DBSCAN.");
    }


    private String mapToHostname(String ip) {
        Set<String> hostnames = mDnsMap.getHostnamesForIp(ip);
        if (hostnames != null && hostnames.size() > 0) {
            // append hostnames back-to-back separated by a delimiter if more than one item in set
            // note: use sorted() to ensure that output remains consistent (as Set has no internal ordering of elements)
            String result = hostnames.stream().sorted().collect(Collectors.joining(" "));
            if (hostnames.size() > 1) {
                // One IP can map to multiple hostnames, although that is rare. For now just raise a warning.
                String warningStr = String.format(
                        "%s.mapToHostname(): encountered an IP (%s) that maps to multiple hostnames (%s)",
                        getClass().getSimpleName(), ip, result);
                System.err.println(warningStr);
            }
            return result;
        }
        // If unable to map to a hostname, return ip for ease of use; caller can overwrite input value, defaulting to
        // the original value if no mapping is found:
        // String src = "<some-ip>";
        // src = mapToHostname(src); // src is now either a hostname or the original ip.
        return ip;
    }

}
