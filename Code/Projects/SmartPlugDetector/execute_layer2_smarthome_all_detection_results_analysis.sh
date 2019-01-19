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