#!/bin/bash

# Base directory where the smarthome evaluation traces and timestamp files are stored,
# (i.e., /some/arbitrary/local/path/experimental_result/smarthome)
TIMESTAMPS_BASE_DIR=$1
readonly TIMESTAMPS_BASE_DIR

# Base directory for the detection results files for the smarthome experiment
RESULTS_BASE_DIR=$2
readonly RESULTS_BASE_DIR



# ==================================================== ARLO CAMERA =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/arlo-camera/timestamps/arlo-camera-nov-13-2018.timestamps"
RESULTS_FILE="$RESULTS_BASE_DIR/arlo-camera/arlo-camera.wlan1.validation.pcap___phone-side.detectionresults"
# Put the analysis results in the same folder as the detection results.
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"


PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/blossom-sprinkler/timestamps/blossom-sprinkler-jan-14-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-plug/timestamps/dlink-plug-nov-7-2018.timestamps"

# DEVICE SIDE
# This one is going to generate 97 FPs because every event is counted twice (same signatures for ON and OFF).
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-plug/dlink-plug.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/dlink-siren/timestamps/dlink-siren-nov-9-2018.timestamps"

#PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/dlink-siren/dlink-siren.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== HUE BULB ========================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/hue-bulb/timestamps/hue-bulb-aug-7-2018.timestamps"

#PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/hue-bulb/hue-bulb.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/kwikset-doorlock/timestamps/kwikset-doorlock-nov-10-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/nest-thermostat/timestamps/nest-thermostat-nov-15-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/nest-thermostat/nest-thermostat.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/st-plug/timestamps/st-plug-nov-12-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/st-plug/st-plug.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-bulb/timestamps/tplink-bulb-nov-16-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-bulb/tplink-bulb.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-plug/timestamps/tplink-plug-nov-8-2018.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-plug/tplink-plug.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# DEVICE SIDE OUTBOUND
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-plug/tplink-plug.wlan1.validation.pcap___device-side-outbound.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/wemo-insight-plug/timestamps/wemo-insight-plug-nov-21-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
# This one is going to generate 100 FPs because every event is counted twice (same signatures for ON and OFF).
RESULTS_FILE="$RESULTS_BASE_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/wemo-plug/timestamps/wemo-plug-nov-20-2018.timestamps"

# Has no device side signature.

# PHONE SIDE
# This one is going to generate 100 FPs because every event is counted twice (same signatures for ON and OFF).
RESULTS_FILE="$RESULTS_BASE_DIR/wemo-plug/wemo-plug.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================


# NEW DEVICES
# ================================================= TP LINK BULB COLOR =================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-bulb-color/timestamps/tplink-bulb-color-apr-12-2019.timestamps"

# PHONE SIDE
# This one is going to generate 100 FPs because every event is counted twice (same signatures for ON and OFF).
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-bulb-color/tplink-bulb-color.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== TP LINK BULB INTENSITY ===============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/tplink-bulb-intensity/timestamps/tplink-bulb-intensity-apr-12-2019.timestamps"

# PHONE SIDE
# This one is going to generate 100 FPs because every event is counted twice (same signatures for ON and OFF).
RESULTS_FILE="$RESULTS_BASE_DIR/tplink-bulb-intensity/tplink-bulb-intensity.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== BLOSSOM SPRINKLER MODE ===============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/blossom-sprinkler-mode/timestamps/blossom-sprinkler-mode-apr-15-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/blossom-sprinkler-mode/blossom-sprinkler-mode.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== NEST THERMOSTAT MODE =================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/nest-thermostat-mode/timestamps/nest-thermostat-mode-apr-16-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/nest-thermostat-mode/nest-thermostat-mode.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ==================================================== AMAZON PLUG =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/amazon-plug/timestamps/amazon-plug-apr-16-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/amazon-plug/amazon-plug.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =================================================== SENGLED BULB =====================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/sengled-bulb/timestamps/sengled-bulb-apr-16-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb/sengled-bulb.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb/sengled-bulb.eth1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== SENGLED BULB INTENSITY ===============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/sengled-bulb-intensity/timestamps/sengled-bulb-intensity-apr-17-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb-intensity/sengled-bulb-intensity.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/sengled-bulb-intensity/sengled-bulb-intensity.eth1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= ECOBEE THERMOSTAT ==================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/ecobee-thermostat/timestamps/ecobee-thermostat-apr-17-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/ecobee-thermostat/ecobee-thermostat.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== ECOBEE THERMOSTAT FAN ================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/ecobee-thermostat-fan/timestamps/ecobee-thermostat-fan-apr-18-2019.timestamps"

# PHONE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/ecobee-thermostat-fan/ecobee-thermostat-fan.wlan1.validation.pcap___phone-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =================================================== RACHIO SPRINKLER =================================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/rachio-sprinkler/timestamps/rachio-sprinkler-apr-18-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/rachio-sprinkler/rachio-sprinkler.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================

# =============================================== RACHIO SPRINKLER STANDBY =============================================
TIMESTAMPS_FILE="$TIMESTAMPS_BASE_DIR/rachio-sprinkler-standby/timestamps/rachio-sprinkler-standby-apr-18-2019.timestamps"

# DEVICE SIDE
RESULTS_FILE="$RESULTS_BASE_DIR/rachio-sprinkler-standby/rachio-sprinkler-standby.wlan1.validation.pcap___device-side.detectionresults"
ANALYSIS_RESULTS_FILE="$RESULTS_FILE.analysis"
EXACT_MATCH="true"
PROGRAM_ARGS="'$TIMESTAMPS_FILE' '$RESULTS_FILE' '$ANALYSIS_RESULTS_FILE' '$EXACT_MATCH'"
#./gradlew run -DmainClass=edu.uci.iotproject.evaluation.DetectionResultsAnalyzer --args="$PROGRAM_ARGS"
# ======================================================================================================================