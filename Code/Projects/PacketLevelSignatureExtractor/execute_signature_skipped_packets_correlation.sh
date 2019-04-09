#!/bin/bash

#set -x # echo invoked commands to std out

# Base dir should point to the experimental_result folder which contains the subfolders:
# - 'smarthome' which contains the traces collected while other devices are idle
# - 'standalone' which contains signatures and the traces used to generate the signatures.
BASE_DIR=$1
readonly BASE_DIR

OUTPUT_DIR=$2
readonly OUTPUT_DIR

SIGNATURES_BASE_DIR="$BASE_DIR/standalone"
readonly SIGNATURES_BASE_DIR

# ==================================================== ARLO CAMERA =====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/arlo-camera/wlan1/arlo-camera.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE (TODO: may possibly be the .incomplete signatures)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="548"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.local.pcap"

# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-spri#./gradlewnkler/blossom-sprinkler.wlan1.validation.pcap___device-side.detectionresults"
SIGNATURE_DURATION="9274"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/analyses/blossom-sprinkler-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3670"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/dlink-plug/wlan1/dlink-plug.wlan1.local.pcap"

# DEVICE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.validation.pcap___device-side.detectionresults"
SIGNATURE_DURATION="8866"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="193"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/dlink-siren/wlan1/dlink-siren.wlan1.local.pcap"

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="71"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== HUE BULB =======================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/hue-bulb/wlan1/hue-bulb.wlan1.local.pcap"

# Has no signature: we need to use the old October PCAP file and timestamp to generate signatures for Hue Bulb.
# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb/analyses/hue-bulb-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb/analyses/hue-bulb-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/hue-bulb/hue-bulb.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="25"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3161"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/nest-thermostat/wlan1/nest-thermostat.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="1179"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/st-plug/wlan1/st-plug.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="2445"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/tplink-bulb/wlan1/tplink-bulb.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analyses/tplink-bulb-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/analyses/tplink-bulb-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="162"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/tplink-plug/wlan1/tplink-plug.wlan1.local.pcap"

# DEVICE SIDE (both the 112, 115 and 556, 1293 sequences)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.validation.pcap___device-side.detectionresults"
SIGNATURE_DURATION="3660"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

PCAP_FILE="$SIGNATURES_BASE_DIR/tplink-plug/wlan1/tplink-plug.wlan1.local.pcap"

# DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.validation.pcap___device-side-outbound.detectionresults"
SIGNATURE_DURATION="224"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

# Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="106"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/wemo-plug/wlan1/wemo-plug.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-plug/wemo-plug.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="147"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================




# NEW DEVICES WITH ST APP
# ================================================= WEMO PLUG ST APP ===================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/wemo-plug-stapp/wlan1/wemo-plug-stapp.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug-stapp/analyses/wemo-plug-stapp-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug-stapp/analyses/wemo-plug-stapp-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug-stapp/signatures/wemo-plug-stapp-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug-stapp/signatures/wemo-plug-stapp-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-plug-stapp/wemo-plug-stapp.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="2073"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= WEMO PLUG ST APP ===================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/wemo-insight-plug-stapp/wlan1/wemo-insight-plug-stapp.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug-stapp/analyses/wemo-insight-plug-stapp-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug-stapp/analyses/wemo-insight-plug-stapp-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug-stapp/signatures/wemo-insight-plug-stapp-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug-stapp/signatures/wemo-insight-plug-stapp-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug-stapp/wemo-insight-plug-stapp.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="5457"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= HUE BULB ST APP ====================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/hue-bulb-stapp/wlan1/hue-bulb-stapp.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb-stapp/analyses/hue-bulb-stapp-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/hue-bulb-stapp/analyses/hue-bulb-stapp-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb-stapp/signatures/hue-bulb-stapp-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb-stapp/signatures/hue-bulb-stapp-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/hue-bulb-stapp/hue-bulb-stapp.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3086"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================

# ================================================= LIFX BULB ST APP ===================================================
PCAP_FILE="$SIGNATURES_BASE_DIR/lifx-bulb-stapp/wlan1/lifx-bulb-stapp.wlan1.local.pcap"

# Has no device side signature.

# PHONE SIDE
ON_ANALYSIS="$SIGNATURES_BASE_DIR/lifx-bulb-stapp/analyses/lifx-bulb-stapp-onClusters-phone-side.cls"
OFF_ANALYSIS="$SIGNATURES_BASE_DIR/lifx-bulb-stapp/analyses/lifx-bulb-stapp-offClusters-phone-side.cls"
ON_SIGNATURE="$SIGNATURES_BASE_DIR/lifx-bulb-stapp/signatures/lifx-bulb-stapp-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/lifx-bulb-stapp/signatures/lifx-bulb-stapp-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/lifx-bulb-stapp/lifx-bulb-stapp.wlan1.validation.pcap___phone-side.detectionresults"
SIGNATURE_DURATION="3086"
EPSILON="10.0"
ON_SKIPPED_PACKETS="-1"
OFF_SKIPPED_PACKETS="-1"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
#./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
# ======================================================================================================================