package edu.uci.iotproject;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.pcap4j.core.PcapPacket;

import java.util.List;
import java.util.Optional;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class StateMachine {


    private final SimpleDirectedGraph<Vertex, DefaultEdge> mGraph = new SimpleDirectedGraph<>(DefaultEdge.class);


    public StateMachine(List<List<PcapPacket>> subCluster) {

        for (List<PcapPacket> seqVariation : subCluster) {

            Vertex currVtx;
            Vertex prevVtx = null;

            for (int i = 0; i < seqVariation.size(); i++) {
                // Create new vertex corresponding to this packet of the sequence
                PcapPacket currPkt = seqVariation.get(i);
                currVtx = new Vertex(currPkt.getOriginalLength(), i);





                mGraph.addVertex(currVtx);



                if (prevVtx != null) {
                    // Link vertex representing previous packet of sequence to this vertex.
                    mGraph.addEdge(prevVtx, currVtx);

                }

                // Current vertex becomes previous vertex for next iteration.
                prevVtx = currVtx;
            }
        }

    }


    private Vertex mCurrentState;

//    @Override
//    public void gotPacket(PcapPacket packet) {
//        // Generate a vertex corresponding to the received packet.
//        // We expect a packet at the layer that follows the current state's layer.
//        Vertex pktVtx = new Vertex(packet.getOriginalLength(), mCurrentState.mLayer + 1);
//        // Check if such a vertex is present as a successor of the current state
//        Optional<Vertex> match = Graphs.successorListOf(mGraph, mCurrentState).stream().
//                filter(v -> v.equals(pktVtx)).findFirst();
//        // If yes, we move to that new state (new vertex).
//        match.ifPresent(v -> mCurrentState = v);
//        // TODO buffer the packets that got us here
//        // TODO check if we've reached the final layer...
//
//    }


    /**
     * Attempts to use {@code packet} to advance this state machine.
     * @param packet
     * @return {@code true} if this state machine could progress by consuming {@code packet}, {@code false} otherwise.
     */
    public boolean attemptAdvance(PcapPacket packet) {
        // Generate a vertex corresponding to the received packet.
        // We expect a packet at the layer that follows the current state's layer.
        Vertex pktVtx = new Vertex(packet.getOriginalLength(), mCurrentState.mLayer + 1);
        // Check if such a vertex is present as a successor of the current state
        Optional<Vertex> match = Graphs.successorListOf(mGraph, mCurrentState).stream().
                filter(v -> v.equals(pktVtx)).findFirst();
        if (match.isPresent()) {
            // If yes, we move to that new state (new vertex).
            mCurrentState = match.get();
            // TODO buffer the packet to keep track of what packets got us here (keep track of the match)
            // TODO check if we've reached the final layer...

            return true;
        }
        return false;
    }

    private static class Vertex {

        // TODO how to include direction of packets here...

        private final int mPktLength;
        private final int mLayer;


        private Vertex(int pktLength, int layer) {
            mPktLength = pktLength;
            mLayer = layer;
        }


        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Vertex)) return false;
            Vertex that = (Vertex) obj;
            return that.mPktLength == this.mPktLength && that.mLayer == this.mLayer;
        }

        @Override
        public int hashCode() {
//            return Integer.hashCode(mPktLength);
            // Hack: use string's hashCode implementation.
            return (Integer.toString(mPktLength) + " " + Integer.toString(mLayer)).hashCode();
        }

    }
}
