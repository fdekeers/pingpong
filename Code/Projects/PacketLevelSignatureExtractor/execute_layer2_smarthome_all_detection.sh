#!/bin/bash

#set -x # echo invoked commands to std out

# Base dir should point to the experimental_result folder which contains the subfolders:
# - 'smarthome' which contains the traces collected while other devices are idle
# - 'standalone' which contains signatures and the traces used to generate the signatures.
BASE_DIR=$1
readonly BASE_DIR

OUTPUT_DIR=$2
readonly OUTPUT_DIR

PCAPS_BASE_DIR="$BASE_DIR/smarthome"
readonly PCAPS_BASE_DIR

SIGNATURES_BASE_DIR="$BASE_DIR/standalone"
readonly SIGNATURES_BASE_DIR

# ==================================================== ARLO CAMERA =====================================================
PCAP_FILE="$PCAPS_BASE_DIR/arlo-camera/wlan1/arlo-camera.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE (TODO: may possibly be the .incomplete signatures)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="548"
EPSILON="10.0"
ON_SKIPPED_PACKETS="8"
OFF_SKIPPED_PACKETS="10"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
PCAP_FILE="$PCAPS_BASE_DIR/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.detection.pcap"

# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="9274"
EPSILON="10.0"
#ON_SKIPPED_PACKETS="9"
ON_SKIPPED_PACKETS="11"
OFF_SKIPPED_PACKETS="4"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3670"
EPSILON="10.0"
ON_SKIPPED_PACKETS="28"
OFF_SKIPPED_PACKETS="8"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
PCAP_FILE="$PCAPS_BASE_DIR/dlink-plug/wlan1/dlink-plug.wlan1.detection.pcap"

# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="8866"
EPSILON="10.0"
ON_SKIPPED_PACKETS="33"
OFF_SKIPPED_PACKETS="33"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="193"
EPSILON="10.0"
#ON_SKIPPED_PACKETS="2"
ON_SKIPPED_PACKETS="4"
OFF_SKIPPED_PACKETS="4"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/dlink-siren/wlan1/dlink-siren.wlan1.detection.pcap"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="71"
EPSILON="10.0"
ON_SKIPPED_PACKETS="3"
OFF_SKIPPED_PACKETS="5"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
PCAP_FILE="$PCAPS_BASE_DIR/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3161"
EPSILON="10.0"
#ON_SKIPPED_PACKETS="12"
ON_SKIPPED_PACKETS="33"
OFF_SKIPPED_PACKETS="6"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/nest-thermostat/wlan1/nest-thermostat.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="1179"
EPSILON="10.0"
ON_SKIPPED_PACKETS="12"
OFF_SKIPPED_PACKETS="39"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
PCAP_FILE="$PCAPS_BASE_DIR/st-plug/wlan1/st-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="2445"
ON_SKIPPED_PACKETS="136"
OFF_SKIPPED_PACKETS="9"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/tplink-bulb/wlan1/tplink-bulb.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analyses/tplink-bulb-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analyses/tplink-bulb-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="162"
EPSILON="10.0"
ON_SKIPPED_PACKETS="9"
OFF_SKIPPED_PACKETS="20"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/tplink-plug/wlan1/tplink-plug.wlan1.detection.pcap"

# DEVICE SIDE (both the 112, 115 and 556, 1293 sequences)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side.detectionresults"
SIGNATURE_DURATION="3660"
EPSILON="10.0"
ON_SKIPPED_PACKETS="33"
OFF_SKIPPED_PACKETS="33"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side-outbound.detectionresults"
SIGNATURE_DURATION="224"
EPSILON="10.0"
ON_SKIPPED_PACKETS="3"
OFF_SKIPPED_PACKETS="4"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
PCAP_FILE="$PCAPS_BASE_DIR/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="106"
EPSILON="10.0"
ON_SKIPPED_PACKETS="5"
OFF_SKIPPED_PACKETS="5"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
PCAP_FILE="$PCAPS_BASE_DIR/wemo-plug/wlan1/wemo-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-plug/wemo-plug.wlan1.detection.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="147"
EPSILON="10.0"
ON_SKIPPED_PACKETS="4"
OFF_SKIPPED_PACKETS="4"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================
