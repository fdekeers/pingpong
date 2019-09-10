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

PCAP_FILE[1]="$PCAPS_BASE_DIR/amazon-plug/eth0/amazon-plug.eth0.detection.pcap"
PCAP_FILE[2]="$PCAPS_BASE_DIR/arlo-camera/eth0/arlo-camera.eth0.detection.pcap"
PCAP_FILE[3]="$PCAPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/eth0/blossom-sprinkler-quickrun.eth0.detection.pcap"
PCAP_FILE[4]="$PCAPS_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/eth0/blossom-sprinkler-mode.eth0.detection.pcap"
PCAP_FILE[5]="$PCAPS_BASE_DIR/dlink-plug/eth0/dlink-plug.eth0.detection.pcap"
PCAP_FILE[6]="$PCAPS_BASE_DIR/dlink-siren/eth0/dlink-siren.eth0.detection.pcap"
PCAP_FILE[7]="$PCAPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/eth0/ecobee-thermostat-hvac.eth0.detection.pcap"
PCAP_FILE[8]="$PCAPS_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/eth0/ecobee-thermostat-fan.eth0.detection.pcap"
PCAP_FILE[9]="$PCAPS_BASE_DIR/kwikset-doorlock/eth0/kwikset-doorlock.eth0.detection.pcap"
PCAP_FILE[10]="$PCAPS_BASE_DIR/nest-thermostat/eth0/nest-thermostat.eth0.detection.pcap"
PCAP_FILE[11]="$PCAPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/eth0/rachio-sprinkler-quickrun.eth0.detection.pcap"
PCAP_FILE[12]="$PCAPS_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/eth0/rachio-sprinkler-mode.eth0.detection.pcap"
PCAP_FILE[13]="$PCAPS_BASE_DIR/ring-alarm/eth0/ring-alarm.eth0.detection.pcap"
PCAP_FILE[14]="$PCAPS_BASE_DIR/roomba-vacuum-robot/eth0/roomba-vacuum-robot.eth0.detection.pcap"
PCAP_FILE[15]="$PCAPS_BASE_DIR/sengled-bulb/sengled-bulb-onoff/eth0/sengled-bulb-onoff.eth0.detection.pcap"
PCAP_FILE[16]="$PCAPS_BASE_DIR/sengled-bulb/sengled-bulb-intensity/eth0/sengled-bulb-intensity.eth0.detection.pcap"
PCAP_FILE[17]="$PCAPS_BASE_DIR/st-plug/eth0/st-plug.eth0.detection.pcap"
PCAP_FILE[18]="$PCAPS_BASE_DIR/tplink-plug/eth0/tplink-plug.eth0.detection.pcap"

for i in {1..18}
do
    # ==================================================== AMAZON PLUG =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/amazon-plug/analyses/amazon-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/amazon-plug/analyses/amazon-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/amazon-plug/signatures/amazon-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/amazon-plug/signatures/amazon-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/amazon-plug/amazon-plug.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="4990"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 1 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== ARLO CAMERA =====================================================
    # Has no device side signature.

    # PHONE SIDE (TODO: may possibly be the .incomplete signatures)
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/arlo-camera/analyses/arlo-camera-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/arlo-camera/signatures/arlo-camera-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/arlo-camera/arlo-camera.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="548"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 2 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================= BLOSSOM SPRINKLER QUICK RUN ============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="9274"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 3 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/analyses/blossom-sprinkler-quickrun-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/signatures/blossom-sprinkler-quickrun-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-quickrun/blossom-sprinkler-quickrun.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="3670"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 3 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================== BLOSSOM SPRINKLER MODE ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/analyses/blossom-sprinkler-mode-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/analyses/blossom-sprinkler-mode-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/signatures/blossom-sprinkler-mode-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/blossom-sprinkler/blossom-sprinkler-mode/signatures/blossom-sprinkler-mode-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/blossom-sprinkler/blossom-sprinkler-mode/blossom-sprinkler-mode.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1977"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 4 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== D-LINK PLUG =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="8866"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 5 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-plug/analyses/dlink-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-plug/signatures/dlink-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-plug/dlink-plug.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="193"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 5 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== D-LINK SIREN ====================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/dlink-siren/analyses/dlink-siren-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/dlink-siren/signatures/dlink-siren-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/dlink-siren/dlink-siren.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="71"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 6 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== ECOBEE THERMOSTAT HVAC ===============================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/analyses/ecobee-thermostat-hvac-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/analyses/ecobee-thermostat-hvac-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/signatures/ecobee-thermostat-hvac-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-hvac/signatures/ecobee-thermostat-hvac-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ecobee-thermostat/ecobee-thermostat-hvac/ecobee-thermostat-hvac.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="733"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 7 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # ======================================================================================================================

    # =============================================== ECOBEE THERMOSTAT FAN ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/analyses/ecobee-thermostat-fan-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/analyses/ecobee-thermostat-fan-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/signatures/ecobee-thermostat-fan-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ecobee-thermostat/ecobee-thermostat-fan/signatures/ecobee-thermostat-fan-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ecobee-thermostat/ecobee-thermostat-fan/ecobee-thermostat-fan.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1953"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 8 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # ======================================================================================================================

    # ================================================= KWIKSET DOORLOCK ===================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/kwikset-doorlock/analyses/kwikset-doorlock-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/kwikset-doorlock/signatures/kwikset-doorlock-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/kwikset-doorlock/kwikset-doorlock.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="3161"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 9 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= NEST THERMOSTAT ====================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/nest-thermostat/analyses/nest-thermostat-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/nest-thermostat/signatures/nest-thermostat-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/nest-thermostat/nest-thermostat.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="1179"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 10 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ============================================== RACHIO SPRINKLER QUICK RUN ============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/analyses/rachio-sprinkler-quickrun-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/analyses/rachio-sprinkler-quickrun-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/signatures/rachio-sprinkler-quickrun-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/signatures/rachio-sprinkler-quickrun-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/rachio-sprinkler/rachio-sprinkler-quickrun/rachio-sprinkler-quickrun.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2695"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 11 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= RACHIO SPRINKLER MODE ==============================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/analyses/rachio-sprinkler-mode-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/analyses/rachio-sprinkler-mode-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/signatures/rachio-sprinkler-mode-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/rachio-sprinkler/rachio-sprinkler-mode/signatures/rachio-sprinkler-mode-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/rachio-sprinkler/rachio-sprinkler-mode/rachio-sprinkler-mode.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2791"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 12 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ===================================================== RING ALARM =====================================================
    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/ring-alarm/analyses/ring-alarm-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/ring-alarm/analyses/ring-alarm-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/ring-alarm/signatures/ring-alarm-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/ring-alarm/signatures/ring-alarm-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/ring-alarm/ring-alarm.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="665"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 13 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ================================================= ROOMBA VACUUM ROBOT ================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/analyses/roomba-vacuum-robot-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/analyses/roomba-vacuum-robot-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/signatures/roomba-vacuum-robot-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/roomba-vacuum-robot/signatures/roomba-vacuum-robot-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/roomba-vacuum-robot/roomba-vacuum-robot.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="5959"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 14 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== SENGLED BULB ON/OFF ==================================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-onoff/sengled-bulb-onoff.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="10045"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 15 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/analyses/sengled-bulb-onoff-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-onoff/signatures/sengled-bulb-onoff-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-onoff/sengled-bulb-onoff.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="8959"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 15 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # =============================================== SENGLED BULB INTENSITY ===============================================
    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-intensity/sengled-bulb-intensity.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="7888"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 16 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi

    # DEVICE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/analyses/sengled-bulb-intensity-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/sengled-bulb/sengled-bulb-intensity/signatures/sengled-bulb-intensity-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/sengled-bulb/sengled-bulb-intensity/sengled-bulb-intensity.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="906"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 16 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ====================================================== ST PLUG =======================================================
    # Has no device side signature.

    # PHONE SIDE
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-onClusters-phone-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/st-plug/analyses/st-plug-offClusters-phone-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-onSignature-phone-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/st-plug/signatures/st-plug-offSignature-phone-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/st-plug/st-plug.eth0.detection.pcap___phone-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="2445"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 17 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # ======================================================================================================================

    # ==================================================== TP-LINK PLUG ====================================================
    # DEVICE SIDE OUTBOUND (contains only those packets that go through the WAN port, i.e., only the 556, 1293 sequence)
    ON_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-onClusters-device-side.cls"
    OFF_ANALYSIS="$SIGNATURES_BASE_DIR/tplink-plug/analyses/tplink-plug-offClusters-device-side.cls"
    ON_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-onSignature-device-side.sig"
    OFF_SIGNATURE="$SIGNATURES_BASE_DIR/tplink-plug/signatures/tplink-plug-offSignature-device-side.sig"
    RESULTS_FILE="$OUTPUT_DIR/tplink-plug/tplink-plug.eth0.detection.pcap___device-side-${PCAP_FILE[i]}.detectionresults"
    SIGNATURE_DURATION="224"
    EPSILON="10.0"

    PROGRAM_ARGS="'${PCAP_FILE[i]}' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$RESULTS_FILE' '$SIGNATURE_DURATION' '$EPSILON'"
    if [ $i -ne 18 ]
    then
        ./gradlew run -DmainClass=edu.uci.iotproject.detection.layer3.Layer3SignatureDetector --args="$PROGRAM_ARGS"
    fi
    # Phone side does not make sense as it is merely a subset of the device side and does not differentiate ONs from OFFs.
    # ======================================================================================================================
done