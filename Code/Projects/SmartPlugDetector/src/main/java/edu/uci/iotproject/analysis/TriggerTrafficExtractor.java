package edu.uci.iotproject.analysis;

import org.pcap4j.core.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * TODO add class documentation.
 *
 * @author Janus Varmarken
 */
public class TriggerTrafficExtractor implements PcapPacketFilter {

    private final String mPcapFilePath;
    private final List<Instant> mTriggerTimes;
    private final String mDeviceIp;

    private int mTriggerIndex = 0;


    private static final int INCLUSION_WINDOW_MILLIS = 3_000;

    public TriggerTrafficExtractor(String pcapFilePath, List<Instant> triggerTimes, String deviceIp) throws PcapNativeException, NotOpenException {
        mPcapFilePath = pcapFilePath;
        // Ensure that trigger times are sorted in ascending as we rely on this fact in the logic that works out if a
        // packet is related to a trigger.
        Collections.sort(triggerTimes, (i1, i2) -> {
            if (i1.isBefore(i2)) return -1;
            else if (i2.isBefore(i1)) return 1;
            else return 0;
        });
        mTriggerTimes = Collections.unmodifiableList(triggerTimes);
        mDeviceIp = deviceIp;
    }


    public void performExtraction(PacketListener... extractedPacketsConsumers) throws PcapNativeException, NotOpenException, TimeoutException {
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(mPcapFilePath, PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException pne) {
            handle = Pcaps.openOffline(mPcapFilePath);
        }
        // Use the native support for BPF to immediately filter irrelevant traffic.
        handle.setFilter("ip host " + mDeviceIp, BpfProgram.BpfCompileMode.OPTIMIZE);
        PcapHandleReader pcapReader = new PcapHandleReader(handle, this, extractedPacketsConsumers);
        pcapReader.readFromHandle();
        // Reset trigger index (in case client code chooses to rerun the extraction)
        mTriggerIndex = 0;
    }

//    public TriggerTrafficExtractor(String deviceIp, PcapHandle handle, List<Instant> triggerTimes) throws PcapNativeException, NotOpenException {
//        mDeviceIp = deviceIp;
//        mHandle = handle;
//        mHandle.setFilter("ip host " + deviceIp, BpfProgram.BpfCompileMode.OPTIMIZE);
//        // Sort in ascending order.
//        Collections.sort(triggerTimes, (i1, i2) -> {
//            if (i1.isBefore(i2)) return -1;
//            else if (i2.isBefore(i1)) return 1;
//            else return 0;
//        });
//        mTriggerTimes = Collections.unmodifiableList(triggerTimes);
//    }



    //    private void process() {
//        try {
//            PcapPacket prevPacket = null;
//            PcapPacket packet;
//            while ((packet = mHandle.getNextPacketEx()) != null) {
//                if (prevPacket != null && packet.getTimestamp().isBefore(prevPacket.getTimestamp())) {
//                    // Fail early if assumption doesn't hold.
//                    throw new RuntimeException("Packets not in ascending temporal order");
//                }
//                if (shouldIncludePacket(packet)) {
//                    // TODO output packet
//                }
//                prevPacket = packet;
//            }
//        } catch (PcapNativeException | TimeoutException | NotOpenException e) {
//            e.printStackTrace();
//        } catch (EOFException e) {
//            System.out.println("Reached end of pcap file");
//        }
//    }

    @Override
    public boolean shouldIncludePacket(PcapPacket packet) {
        // TODO hmm, is this correct?
        Instant trigger = mTriggerTimes.get(mTriggerIndex);
        if (trigger.isBefore(packet.getTimestamp()) &&
                packet.getTimestamp().isBefore(trigger.plusMillis(INCLUSION_WINDOW_MILLIS))) {
            // Packet lies within INCLUSION_WINDOW_MILLIS after currently considered trigger, include it.
            return true;
        } else {
            if (!trigger.isBefore(packet.getTimestamp())) {
                // Packet is before currently considered trigger, so it shouldn't be included
                return false;
            } else {
                // Packet is >= INCLUSION_WINDOW_MILLIS after currently considered trigger.
                // Proceed to next trigger to see if it lies in range of that.
                // Note that there's an assumption here that no two trigger intervals don't overlap!
                mTriggerIndex++;
                return shouldIncludePacket(packet);
            }
        }






        /*
        if (mTriggerIndex >= mTriggerTimes.size()) {
            // Don't include packet if we've exhausted the list of trigger timestamps.
            return false;
        }
        Instant trigger = mTriggerTimes.get(mTriggerIndex);
        if (trigger.isBefore(packet.getTimestamp()) &&
                packet.getTimestamp().isBefore(trigger.plusMillis(INCLUSION_WINDOW_MILLIS))) {
            // Packet lies within INCLUSION_WINDOW_MILLIS after currently considered trigger, include it.
            return true;
        }
        if (mTriggerIndex >= mTriggerTimes.size()-1) {
            // No additional triggers left to be considered.
            return false;
        }
        trigger = mTriggerTimes.get(mTriggerIndex + 1);
        if (packet.getTimestamp().isBefore(trigger)) {
            return false;
        } else {
            mTriggerIndex++;
            return includePacket(packet);
        }
        */


//        else if (trigger.isBefore(packet.getTimestamp()) &&
//                !packet.getTimestamp().isBefore(trigger.plusMillis(INCLUSION_WINDOW_MILLIS)) {
//
//        }
    }


    private Instant findTriggerTime(PcapPacket packet) {
        mTriggerTimes.stream().filter(i ->
                i.isBefore(packet.getTimestamp()) && packet.getTimestamp().isBefore(i.plusMillis(3000)));

        while (mTriggerIndex < mTriggerTimes.size() &&
                !(mTriggerTimes.get(mTriggerIndex).isBefore(packet.getTimestamp()) &&
                        packet.getTimestamp().isBefore(mTriggerTimes.get(mTriggerIndex).plusMillis(3_000)))
                ) {
            mTriggerIndex++;
        }
        return mTriggerIndex < mTriggerTimes.size() ? mTriggerTimes.get(mTriggerIndex) : null;
        /*
        // Trigger time must logically be BEFORE packet timestamp, so advance
        while (mTriggerIndex < mTriggerTimes.size() &&
                mTriggerTimes.get(mTriggerIndex).isAfter(packet.getTimestamp())) {
            mTriggerIndex++;
        }
        return mTriggerIndex < mTriggerTimes.size() ? mTriggerTimes.get(mTriggerIndex) : null;
        */
    }
}
