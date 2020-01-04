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

PCAP_FILE[1]="$PCAPS_BASE_DIR/amazon-plug/wlan1/amazon-plug.wlan1.detection.pcap"
PCAP_FILE[2]="$PCAPS_BASE_DIR/arlo-camera/wlan1/arlo-camera.wlan1.detection.pcap"
PCAP_FILE[3]="$PCAPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/wlan1/blossom-sprinkler-quickrun.wlan1.detection.pcap"
PCAP_FILE[4]="$PCAPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/wlan1/blossom-sprinkler-mode.wlan1.detection.pcap"
PCAP_FILE[5]="$PCAPS_BASE_DIR/dlink-plug/wlan1/dlink-plug.wlan1.detection.pcap"
PCAP_FILE[6]="$PCAPS_BASE_DIR/dlink-siren/wlan1/dlink-siren.wlan1.detection.pcap"
PCAP_FILE[7]="$PCAPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/wlan1/ecobee-thermostat-hvac.wlan1.detection.pcap"
PCAP_FILE[8]="$PCAPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/wlan1/ecobee-thermostat-fan.wlan1.detection.pcap"
PCAP_FILE[9]="$PCAPS_BASE_DIR/kwikset-doorlock/wlan1/kwikset-doorlock.wlan1.detection.pcap"
PCAP_FILE[10]="$PCAPS_BASE_DIR/nest-thermostat/wlan1/nest-thermostat.wlan1.detection.pcap"
PCAP_FILE[11]="$PCAPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/wlan1/rachio-sprinkler-quickrun.wlan1.detection.pcap"
PCAP_FILE[12]="$PCAPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/wlan1/rachio-sprinkler-mode.wlan1.detection.pcap"
PCAP_FILE[13]="$PCAPS_BASE_DIR/ring-alarm/wlan1/ring-alarm.wlan1.detection.pcap"
PCAP_FILE[14]="$PCAPS_BASE_DIR/roomba-vacuum-robot/wlan1/roomba-vacuum-robot.wlan1.detection.pcap"
PCAP_FILE[15]="$PCAPS_BASE_DIR/sengled-bulb/sengled-bulb-onoff/wlan1/sengled-bulb-onoff.wlan1.detection.pcap"
PCAP_FILE[16]="$PCAPS_BASE_DIR/sengled-bulb/sengled-bulb-intensity/wlan1/sengled-bulb-intensity.wlan1.detection.pcap"
PCAP_FILE[17]="$PCAPS_BASE_DIR/st-plug/wlan1/st-plug.wlan1.detection.pcap"
PCAP_FILE[18]="$PCAPS_BASE_DIR/tplink-bulb/tplink-bulb-onoff/wlan1/tplink-bulb-onoff.wlan1.detection.pcap"
PCAP_FILE[19]="$PCAPS_BASE_DIR/tplink-bulb/tplink-bulb-color/wlan1/tplink-bulb-color.wlan1.detection.pcap"
PCAP_FILE[20]="$PCAPS_BASE_DIR/tplink-bulb/tplink-bulb-intensity/wlan1/tplink-bulb-intensity.wlan1.detection.pcap"
PCAP_FILE[21]="$PCAPS_BASE_DIR/tplink-plug/wlan1/tplink-plug.wlan1.detection.pcap"
PCAP_FILE[22]="$PCAPS_BASE_DIR/wemo-insight-plug/wlan1/wemo-insight-plug.wlan1.detection.pcap"
PCAP_FILE[23]="$PCAPS_BASE_DIR/wemo-plug/wlan1/wemo-plug.wlan1.detection.pcap"

for i in {1..23}
do
    # ==================================================== AMAZON PLUG =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/amazon-plug/analyses/amazon-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/amazon-plug/analyses/amazon-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/amazon-plug/signatures/amazon-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/amazon-plug/signatures/amazon-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/amazon-plug/amazon-plug.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="4990"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 1 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== ARLO CAMERA =====================================================
    # Has no device side signature.

    # PHONE SIDE (TODO: may possibly be the .incomplete signatures)
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="548"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    #ON_SKIPPED_PACKETS="8"
    #OFF_SKIPPED_PACKETS="10"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 2 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================= BLOSSOM SPRINKLER QUICK RUN ============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="9274"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    #ON_SKIPPED_PACKETS="9"
    #ON_SKIPPED_PACKETS="11" - from detection PCAP
    #OFF_SKIPPED_PACKETS="4"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 3 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="3670"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    #ON_SKIPPED_PACKETS="28"
    #ON_SKIPPED_PACKETS="30" - from detection PCAP
    #OFF_SKIPPED_PACKETS="8"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 3 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================== BLOSSOM SPRINKLER MODE ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/analyses/blossom-sprinkler-mode-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/analyses/blossom-sprinkler-mode-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/signatures/blossom-sprinkler-mode-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/signatures/blossom-sprinkler-mode-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-mode/blossom-sprinkler-mode.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1977"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 4 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== D-LINK PLUG =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="8866"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="33"
    ON_SKIPPED_PACKETS="-1"
    #OFF_SKIPPED_PACKETS="33"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 5 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="193"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    #ON_SKIPPED_PACKETS="2"
    #ON_SKIPPED_PACKETS="4" - from detection PCAP
    #OFF_SKIPPED_PACKETS="4"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 5 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== D-LINK SIREN ====================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="71"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="3"
    #OFF_SKIPPED_PACKETS="5"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 6 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== ECOBEE THERMOSTAT HVAC ===============================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/analyses/ecobee-thermostat-hvac-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/analyses/ecobee-thermostat-hvac-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/signatures/ecobee-thermostat-hvac-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/signatures/ecobee-thermostat-hvac-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ecobee-thermostat/ecobee-thermostat-hvac/ecobee-thermostat-hvac.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="733"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 7 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # ======================================================================================================================

    # =============================================== ECOBEE THERMOSTAT FAN ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/analyses/ecobee-thermostat-fan-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/analyses/ecobee-thermostat-fan-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/signatures/ecobee-thermostat-fan-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/signatures/ecobee-thermostat-fan-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ecobee-thermostat/ecobee-thermostat-fan/ecobee-thermostat-fan.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1953"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 8 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # ======================================================================================================================

    # ================================================= KWIKSET DOORLOCK ===================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="3161"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="12"
    #OFF_SKIPPED_PACKETS="6"
    #OFF_SKIPPED_PACKETS="33" - from detection PCAP
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 9 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= NEST THERMOSTAT ====================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1179"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="12"
    #OFF_SKIPPED_PACKETS="39"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 10 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================== RACHIO SPRINKLER QUICK RUN ============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/analyses/rachio-sprinkler-quickrun-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/analyses/rachio-sprinkler-quickrun-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/signatures/rachio-sprinkler-quickrun-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/signatures/rachio-sprinkler-quickrun-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/rachio-sprinkler-quickrun.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2695"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 11 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= RACHIO SPRINKLER MODE ==============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/analyses/rachio-sprinkler-mode-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/analyses/rachio-sprinkler-mode-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/signatures/rachio-sprinkler-mode-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/signatures/rachio-sprinkler-mode-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/rachio-sprinkler/rachio-sprinkler-mode/rachio-sprinkler-mode.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2791"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 12 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ===================================================== RING ALARM =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ring-alarm/analyses/ring-alarm-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ring-alarm/analyses/ring-alarm-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ring-alarm/signatures/ring-alarm-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ring-alarm/signatures/ring-alarm-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ring-alarm/ring-alarm.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="665"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 13 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= ROOMBA VACUUM ROBOT ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/analyses/roomba-vacuum-robot-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/analyses/roomba-vacuum-robot-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/signatures/roomba-vacuum-robot-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/signatures/roomba-vacuum-robot-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/roomba-vacuum-robot/roomba-vacuum-robot.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="5959"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 14 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== SENGLED BULB ON/OFF ==================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-onoff/sengled-bulb-onoff.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="10045"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 15 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== SENGLED BULB INTENSITY ===============================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-intensity/sengled-bulb-intensity.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="7888"
    EPSILON="10.0"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 16 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ====================================================== ST PLUG =======================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2445"
    #ON_SKIPPED_PACKETS="136"
    #OFF_SKIPPED_PACKETS="9"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 17 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= TP LINK BULB ON/OFF ================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-onoff/analyses/tplink-bulb-onoff-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-onoff/analyses/tplink-bulb-onoff-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-onoff/signatures/tplink-bulb-onoff-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-onoff/signatures/tplink-bulb-onoff-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb-onoff/tplink-bulb-onoff.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="162"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="9"
    #OFF_SKIPPED_PACKETS="20"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 18 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================ TP-LINK BULB COLOR ==================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-color/analyses/tplink-bulb-color-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-color/analyses/tplink-bulb-color-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-color/signatures/tplink-bulb-color-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-color/signatures/tplink-bulb-color-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb-color/tplink-bulb-color.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="191"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="9"
    #OFF_SKIPPED_PACKETS="9"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 19 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================== TP-LINK BULB INTENSITY ================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-intensity/analyses/tplink-bulb-intensity-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-intensity/analyses/tplink-bulb-intensity-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-intensity/signatures/tplink-bulb-intensity-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-bulb/tplink-bulb-intensity/signatures/tplink-bulb-intensity-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-bulb/tplink-bulb-intensity/tplink-bulb-intensity.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="209"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="7"
    #OFF_SKIPPED_PACKETS="7"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 20 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== TP-LINK PLUG ====================================================
    # DEVICE SIDE (both the 112, 115 and 556, 1293 sequences)
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="3660"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="33"
    #OFF_SKIPPED_PACKETS="33"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 21 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side-outbound.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side-outbound.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.wlan1.detection.pcap___device-side-outbound.detectionresults"
    SIGNATURE_DURATION="224"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="3"
    #OFF_SKIPPED_PACKETS="4"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    ##./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"

    # Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
    # ======================================================================================================================

    # ================================================== WEMO INSIGHT PLUG =================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-insight-plug/analyses/wemo-insight-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-insight-plug/signatures/wemo-insight-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/wemo-insight-plug/wemo-insight-plug.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="106"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="5"
    #OFF_SKIPPED_PACKETS="5"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 22 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ===================================================== WEMO PLUG ======================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/wemo-plug/analyses/wemo-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/wemo-plug/signatures/wemo-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/wemo-plug/wemo-plug.wlan1.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="147"
    EPSILON="10.0"
    #ON_SKIPPED_PACKETS="4"
    #OFF_SKIPPED_PACKETS="4"
    ON_SKIPPED_PACKETS="-1"
    OFF_SKIPPED_PACKETS="-1"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON' '$ON_SKIPPED_PACKETS' '$OFF_SKIPPED_PACKETS'"
    if [ $i -ne 23 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer2.Layer2SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================
done