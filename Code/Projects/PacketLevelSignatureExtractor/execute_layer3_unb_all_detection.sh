#!/bin/bash

#set -x # echo invoked commands to std out

# Arg1 should point to the UNB trace (PCAP w/o any expected events).
PCAP_FILE=$1
readonly PCAP_FILE

# Arg2 should point to the base directory  for signature files (i.e., /some/local/path/experimental_result/standalone)
SIGNATURES_BASE_DIR=$2
readonly SIGNATURES_BASE_DIR

# Arg3 should point to folder where the detection results for the UNB trace are to be output.
OUTPUT_DIR=$3
readonly OUTPUT_DIR

# ==================================================== ARLO CAMERA =====================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analysis/arlo-camera-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analysis/arlo-camera-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.complete.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.complete.sig"
RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="548"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analysis/blossom-sprinkler-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analysis/blossom-sprinkler-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.eth0.detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="9274"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analysis/blossom-sprinkler-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analysis/blossom-sprinkler-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3670"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analysis/dlink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analysis/dlink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="8866"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analysis/dlink-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analysis/dlink-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="193"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analysis/dlink-siren-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analysis/dlink-siren-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="71"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== HUE BULB =======================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb/analysis/hue-bulb-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb/analysis/hue-bulb-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/hue-bulb/hue-bulb.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="27"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analysis/kwikset-doorlock-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analysis/kwikset-doorlock-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3161"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analysis/nest-thermostat-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analysis/nest-thermostat-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.eth0.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="1179"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analysis/st-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analysis/st-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="2445"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analysis/tplink-bulb-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analysis/tplink-bulb-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb.wlan1.wan-detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="162"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
# DEVICE SIDE (both the 112, 115 and 556, 1293 sequences)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analysis/tplink-plug-onClusters.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analysis/tplink-plug-offClusters.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.wan-detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="3660"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"

# DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analysis/tplink-plug-onClusters.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analysis/tplink-plug-offClusters.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.wan-detection.pcap___device-side-outbound.detectionresults"
SIGNATURE_DURATION="224"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"

# Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analysis/wemo-insight-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analysis/wemo-insight-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.wan-detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="106"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analysis/wemo-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analysis/wemo-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-plug/wemo-plug.wlan1.wan-detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="147"
EPSILON="10.0"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================

