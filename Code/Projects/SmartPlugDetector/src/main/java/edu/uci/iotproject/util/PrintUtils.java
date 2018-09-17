package edu.uci.iotproject.util;

import edu.uci.iotproject.DnsMap;
import edu.uci.iotproject.analysis.PcapPacketPair;
import org.apache.commons.math3.stat.clustering.Cluster;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility methods for generating (output) strings.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class PrintUtils {

    private PrintUtils() { /* private constructor to prevent instantiation */ }


    /**
     * Converts a {@code PcapPacketPair} into a CSV string containing the packet lengths of the two packets in the pair.
     *
     * For example, the resulting string will be "123, 456" if the first packet of the pair has a length of 123 and the
     * second packet of the pair has a length of 456.
     *
     * <b>Note:</b> if the {@link PcapPacketPair} has no second element, 0 is printed as the length of the second packet
     * in the pair.
     *
     * @return a CSV string containing the packet lengths of the two packets of the given {@code PcapPacketPair}.
     */
    public static String toCsv(PcapPacketPair packetPair) {
        return String.format("%d, %d", packetPair.getFirst().getOriginalLength(),
                packetPair.getSecond().map(pp -> pp.getOriginalLength()).orElse(0));
    }

    /**
     * Converts a {@code PcapPacketPair} into a CSV string containing the packet lengths of the two packets in the pair
     * followed by the source of each packet. The source will be a (set of) hostname(s) if the source IP can be resolved
     * to a (set of) hostname(s) using the provided {@link DnsMap}.
     *
     * For example, the resulting string will be "123, 456, 192.168.1.42, domain.com" if the first packet of the pair
     * has a length of 123, the second packet of the pair has a length of 456, the first packet of the pair the pair has
     * a source IP of '192.168.1.42' that cannot be resolved to a hostname, and the second packet of the pair has an IP
     * that resolves to 'domain.com'.
     *
     * <b>Note:</b> if the {@link PcapPacketPair} has no second element, 0 is printed as the length of the second packet
     * in the pair, and null is printed for its source.
     *
     * @return a CSV string containing the packet lengths of the two packets of the given {@code PcapPacketPair} as well
     *         as their respective sources.
     */
    public static String toCsv(PcapPacketPair packetPair, DnsMap ipHostnameMappings) {
        // First obtain source IPs
        String firstSrc = PcapPacketUtils.getSourceIp(packetPair.getFirst());
        // Note: use optional for second item in pair as there might not be one.
        Optional<String> secondSrc = packetPair.getSecond().map(pkt -> PcapPacketUtils.getSourceIp(pkt));

        // If possible, map source IPs to hostnames.
        Set<String> firstHostnames = ipHostnameMappings.getHostnamesForIp(firstSrc);
        Optional<Set<String>> secondHostnames = secondSrc.map(src -> ipHostnameMappings.getHostnamesForIp(src));
        final String delimiter = " ";
        if (firstHostnames != null) {
            // If one IP maps to multiple hostnames, we concatenate the hostnames (separated by a delimiter)
            firstSrc = firstHostnames.stream().collect(Collectors.joining(delimiter));
        }
        // If one IP maps to multiple hostnames, we concatenate the hostnames (separated by a delimiter)
        Optional<String> hostnames = secondHostnames.map(hostnameSet -> hostnameSet.stream().collect(Collectors.joining(delimiter)));
        // Fall back to IP if we couldn't second pair is present, but we couldn't map to (a) hostname(s).
        secondSrc = hostnames.isPresent() ? hostnames : secondSrc;

        // Check if the first source is C (client) or S (server)
        String firstSrcCorS = packetPair.isFirstClient() ? "C" : "S";
        String secondSrcCorS = packetPair.isSecondClient() ? "C" : "S";

        return String.format("%d, %d, %s, %s, %s, %s", packetPair.getFirst().getOriginalLength(),
                packetPair.getSecond().map(pp -> pp.getOriginalLength()).orElse(0),
                firstSrc,
                secondSrc.orElse("null"),
                firstSrcCorS,
                secondSrcCorS);
    }

    /**
     * Generate a string that summarizes/describes {@code cluster}.
     * @param cluster The {@link Cluster} to summarize/describe.
     * @return A string that summarizes/describes {@code cluster}.
     */
    public static String toSummaryString(Cluster<PcapPacketPair> cluster) {
        StringBuilder sb = new StringBuilder();
        for (PcapPacketPair ppp : cluster.getPoints()) {
            sb.append(toCsv(ppp, ppp.getDnsMap()) + System.lineSeparator());
        }
        return sb.toString();
    }
}
