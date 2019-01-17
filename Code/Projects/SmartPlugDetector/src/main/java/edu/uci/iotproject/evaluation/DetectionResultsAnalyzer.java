package edu.uci.iotproject.evaluation;

import edu.uci.iotproject.analysis.TriggerTrafficExtractor;
import edu.uci.iotproject.analysis.UserAction;
import edu.uci.iotproject.io.TriggerTimesFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility for comparing detected events to logged (actual) events.
 *
 * @author Janus Varmarken {@literal <jvarmark@uci.edu>}
 * @author Rahmadi Trimananda {@literal <rtrimana@uci.edu>}
 */
public class DetectionResultsAnalyzer {

    public static void main(String[] args) throws IOException {
        // -------------------------------------- Parse the input files --------------------------------------

        String triggerTimesFile = args[0];
        // Read the trigger times.
        // The trigger times file does not contain event types as we initially assumed that we would just be alternating
        // between ON and OFF.
        List<Instant> triggerTimestamps = new TriggerTimesFileReader().readTriggerTimes(triggerTimesFile, false);
        // Now generate user actions based on this alternating ON/OFF pattern.
        List<UserAction> triggers = new ArrayList<>();
        for (int i = 0; i < triggerTimestamps.size(); i++) {
            // NOTE: assumes triggers alternate between ON and OFF
            UserAction.Type actionType = i % 2 == 0 ? UserAction.Type.TOGGLE_ON : UserAction.Type.TOGGLE_OFF;
            triggers.add(new UserAction(actionType, triggerTimestamps.get(i)));
        }
        // Read the detection output file, assuming a format as specified in UserAction.toString()
        File detectionOutputFile = new File(args[1]);
        List<UserAction> detectedEvents = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(detectionOutputFile))) {
            String s;
            while ((s = br.readLine()) != null) {
                detectedEvents.add(UserAction.fromString(s));
            }
        }

        // -----------------  Now ready to compare the detected events with the logged events -----------------

        // To contain all detected events that could be mapped to a trigger
        List<UserAction> truePositives = new ArrayList<>();
        for (UserAction detectedEvent : detectedEvents) {
            Optional<UserAction> matchingTrigger = triggers.stream()
                    .filter(t -> t.getType() == detectedEvent.getType() &&
                            t.getTimestamp().isBefore(detectedEvent.getTimestamp()) &&
                            t.getTimestamp().plusMillis(TriggerTrafficExtractor.INCLUSION_WINDOW_MILLIS).
                                    isAfter(detectedEvent.getTimestamp())
                    ).findFirst();
            matchingTrigger.ifPresent(mt -> {
                // We've consumed the trigger (matched it with a detected event), so remove it so we don't match with
                // another detected event.
                triggers.remove(mt);
                // The current detected event was a true positive as we could match it with a trigger.
                truePositives.add(detectedEvent);
            });
        }
        // Now the false positives are those elements in detectedEvents that are not in truePositives
        List<UserAction> falsePositives = new ArrayList<>();
        falsePositives.addAll(detectedEvents);
        falsePositives.removeAll(truePositives);
        // Print the results...
        System.out.println("---------- False negatives (events that where not detected) ----------");
        for (UserAction missing : triggers) {
            System.out.println(missing);
        }
        System.out.println("Total of " + Integer.toString(triggers.size()));
        System.out.println();
        System.out.println("---------- False positives (detected, but no matching trigger) ----------");
        for (UserAction fp : falsePositives) {
            System.out.println(fp);
        }
        System.out.println("Total of " + Integer.toString(falsePositives.size()));
    }

}
