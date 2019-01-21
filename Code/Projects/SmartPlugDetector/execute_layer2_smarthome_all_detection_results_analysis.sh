#!/bin/bash

# Base directory where the smarthome evaluation traces and timestamp files are stored,
# (i.e., /some/arbitrary/local/path/experimental_result/smarthome)
TIMESTAMPS_BASE_DIR=$1
readonly TIMESTAMPS_BASE_DIR

# Base directory for the detection results files for the smarthome experiment
RESULTS_BASE_DIR=$2
readonly RESULTS_BASE_DIR



# ==================================================== ARLO CAMERA =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/arlo-camera/timestamps/arlo-camera-smarthome-nov-15-2018.timestamps"
RESULTS_FILE="$RESULTS_BASE_DIR/arlo-camera/arlo-camera.wlan1.detection.pcap___phone-side.detectionresults"
# Put the analysis results in the same folder as the detection results.
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"


PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/blossom-sprinkler/timestamps/blossom-sprinkler-smarthome-jan-14-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-plug/timestamps/dlink-plug-smarthome-nov-8-2018.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-siren/timestamps/dlink-siren-smarthome-nov-10-2018.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

#PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== HUE BULB =======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/hue-bulb/timestamps/hue-bulb-smarthome-nov-20-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/hue-bulb/hue-bulb.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/kwikset-doorlock/timestamps/kwikset-doorlock-smarthome-nov-10-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/nest-thermostat/timestamps/nest-thermostat-smarthome-nov-16-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/nest-thermostat/nest-thermostat.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/st-plug/timestamps/st-plug-smarthome-nov-13-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/st-plug/st-plug.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-bulb/timestamps/tplink-bulb-smarthome-nov-19-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-bulb/tplink-bulb.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-plug/timestamps/tplink-plug-smarthome-nov-9-2018.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# DEVICE SIDE OUTBOUND
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side-outbound.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/wemo-insight-plug/timestamps/wemo-insight-plug-smarthome-nov-22-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/wemo-plug/timestamps/wemo-plug-smarthome-nov-21-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/wemo-plug/wemo-plug.wlan1.detection.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE'"
./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================