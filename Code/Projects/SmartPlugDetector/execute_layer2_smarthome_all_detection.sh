#!/bin/bash
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
ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= BLOSSOM SPRINKLER ==================================================
PCAP_FILE="$PCAPS_BASE_DIR/blossom-sprinkler/wlan1/blossom-sprinkler.wlan1.detection.pcap"

# DEVICE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___device-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/signatures/blossom-sprinkler-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK PLUG =====================================================
PCAP_FILE="$PCAPS_BASE_DIR/dlink-plug/wlan1/dlink-plug.wlan1.detection.pcap"

# DEVICE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___device-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== D-LINK SIREN ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/dlink-siren/wlan1/dlink-siren.wlan1.detection.pcap"

# DEVICE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___device-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== HUE BULB =======================================================
PCAP_FILE="$PCAPS_BASE_DIR/hue-bulb/wlan1/hue-bulb.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/hue-bulb/signatures/hue-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/hue-bulb/hue-bulb.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= KWIKSET DOORLOCK ===================================================
PCAP_FILE="$PCAPS_BASE_DIR/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ================================================= NEST THERMOSTAT ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/nest-thermostat/wlan1/nest-thermostat.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ====================================================== ST PLUG =======================================================
PCAP_FILE="$PCAPS_BASE_DIR/st-plug/wlan1/st-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK BULB ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/tplink-bulb/wlan1/tplink-bulb.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/signatures/tplink-bulb-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ==================================================== TP-LINK PLUG ====================================================
PCAP_FILE="$PCAPS_BASE_DIR/tplink-plug/wlan1/tplink-plug.wlan1.detection.pcap"

# DEVICE SIDE (both the 112, 115 and 556, 1293 sequences)
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"

# DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig"
RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side-outbound.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"

# Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
# ======================================================================================================================



# ================================================== WEMO INSIGHT PLUG =================================================
PCAP_FILE="$PCAPS_BASE_DIR/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.detection.pcap___phone-side.detectionresults"

PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================



# ===================================================== WEMO PLUG ======================================================
PCAP_FILE="$PCAPS_BASE_DIR/wemo-plug/wlan1/wemo-plug.wlan1.detection.pcap"

# Has no device side signature.

# PHONE SIDE
ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig"
OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig"
RESULTS_FILE="$OUTPUT_DIR/wemo-plug/wlan1/wemo-plug.wlan1.detection.pcap___phone-side.detectionresults"


PROGRAM_ARGS="'$PCAP_FILE' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE'"
./gradlew run --args="$PROGRAM_ARGS"
# ======================================================================================================================