package edu.uci.iotproject;

import java.util.Objects;

/**
 * Represents a network packet.
 *
 * @author Janus Varmarken
 */
public class Packet {

    /**
     * The packet's length.
     */
    private final int length;

    // TODO should we use hostname for src/dst such that we can map packets pertaining to the same host as similar even if they are sent to different IPs (due to load balancing)?

    /**
     * The packet's source (IP).
     */
    private final String source;

    /**
     * The packet's destination (IP).
     */
    private final String destionation;

    public Packet(String src, String dst, int length) {
        this.source = Objects.requireNonNull(src);
        this.destionation = Objects.requireNonNull(dst);
        this.length = length;
    }

}
