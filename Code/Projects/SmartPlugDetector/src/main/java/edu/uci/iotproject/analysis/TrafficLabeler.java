package edu.uci.iotproject.analysis;

import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;

import java.time.Instant;
import java.util.*;

/**
 * A {@link PacketListener} that marks network traffic as (potentially) related to a user's actions by comparing the
 * timestamp of each packet to the timestamps of the provided list of user actions.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class TrafficLabeler implements PacketListener {

    private final Map<UserAction, List<PcapPacket>> mActionToTrafficMap;
    private final List<UserAction> mActionsSorted;

    public TrafficLabeler(List<UserAction> userActions) {
        // Init map with empty lists (no packets have been mapped to UserActions at the onset).
        mActionToTrafficMap = new HashMap<>();
        userActions.forEach(ua -> mActionToTrafficMap.put(ua, new ArrayList<>()));
        // Sort list of UserActions by timestamp in order to facilitate fast Packet-to-UserAction mapping.
        // For safety reasons, we create an internal copy of the list to prevent external code from changing the list's
        // contents as that would render our assumptions about order of elements invalid.
        // In addition, this also ensures that we do not break assumptions made by external code as we avoid reordering
        // the elements of the list passed from the external code.
        // If performance is to be favored over safety, assign userActions to mActionsSorted directly.
        mActionsSorted = new ArrayList<>();
        mActionsSorted.addAll(userActions);
        Collections.sort(mActionsSorted, (ua1, ua2) -> ua1.getTimestamp().compareTo(ua2.getTimestamp()));
    }


    @Override
    public void gotPacket(PcapPacket packet) {
        // Locate UserAction corresponding to packet, if any.
        int index = Collections.binarySearch(mActionsSorted, new UserAction(null, packet.getTimestamp()), (listItem, key) -> {
            // Start of inclusion interval is the time of the user action
            Instant intervalStart = listItem.getTimestamp();
            // End of inclusion interval is some arbitrary number of milliseconds after the user action.
            Instant intervalEnd = intervalStart.plusMillis(TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS);
            if (key.getTimestamp().isAfter(intervalStart) && key.getTimestamp().isBefore(intervalEnd)) {
                // Packet lies within specified interval after the current UserAction, so we're done.
                // Communicate termination to binarySearch by returning 0 which indicates equality.
                return 0;
            }
            // If packet lies outside inclusion interval of current list item, continue search in lower or upper half of
            // list depending on whether the timestamp of the current list item is smaller or greater than that of the
            // packet.
            return listItem.getTimestamp().compareTo(key.getTimestamp());
        });
        if (index >= 0) {
            // Associate the packet to the its corresponding user action (located during the binary search above).
            mActionToTrafficMap.get(mActionsSorted.get(index)).add(packet);
        }
        // Ignore packet if it is not found to be in temporal proximity of a user action.
    }

}
