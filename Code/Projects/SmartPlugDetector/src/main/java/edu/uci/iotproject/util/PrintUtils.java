package edu.uci.iotproject.util;

import edu.uci.iotproject.DnsMap;
import edu.uci.iotproject.analysis.PcapPacketPair;

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
     * @return a CSV string containing the packet lengths of the two packets of the given {@code PcapPacketPair}.
     */
    public static String toCsv(PcapPacketPair packetPair) {
        return String.format("%d, %d", packetPair.getFirst().getOriginalLength(),
                packetPair.getSecond().getOriginalLength());
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
     * @return a CSV string containing the packet lengths of the two packets of the given {@code PcapPacketPair} as well
     *         as their respective sources.
     */
    public static String toCsv(PcapPacketPair packetPair, DnsMap ipHostnameMappings) {
        // First obtain source IPs
        String firstSrc = PcapPacketUtils.getSourceIp(packetPair.getFirst());
        String secondSrc = PcapPacketUtils.getSourceIp(packetPair.getSecond());

        // If possible, map source IPs to hostnames.
        Set<String> firstHostnames = ipHostnameMappings.getHostnamesForIp(firstSrc);
        Set<String> secondHostnames = ipHostnameMappings.getHostnamesForIp(secondSrc);
        final String delimiter = " ";
        if (firstHostnames != null) {
            // If one IP maps to multiple hostnames, we concatenate the hostnames (separated by a delimiter)
            firstSrc = firstHostnames.stream().collect(Collectors.joining(delimiter));
        }
        if (secondHostnames != null) {
            // If one IP maps to multiple hostnames, we concatenate the hostnames (separated by a delimiter)
            secondSrc = secondHostnames.stream().collect(Collectors.joining(delimiter));
        }

        return String.format("%d, %d, %s, %s", packetPair.getFirst().getOriginalLength(),
                packetPair.getSecond().getOriginalLength(), firstSrc, secondSrc);
    }

}
