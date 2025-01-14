#!/bin/bash

INPUT_PCAP=$1
INPUT_PCAP_BASENAME=$(basename $INPUT_PCAP)
readonly INPUT_PCAP

TIMESTAMP_FILE=$2
readonly TIMESTAMP_FILE

OUTPUT_DIR=$(dirname $INPUT_PCAP)/output
mkdir -p $OUTPUT_DIR
readonly OUTPUT_DIR



### TP-LINK PLUG ###

# LAN signature.
OUTPUT_PCAP="$OUTPUT_DIR/$INPUT_PCAP_BASENAME.processed"
DEVICE_IP="192.168.1.135"
ON_SIGNATURE="$OUTPUT_DIR/tplink-plug-onSignature-device-side.sig"
OFF_SIGNATURE="$OUTPUT_DIR/tplink-plug-offSignature-device-side.sig"
ON_ANALYSIS="$OUTPUT_DIR/tplink-plug-onClusters-device-side.cls"
OFF_ANALYSIS="$OUTPUT_DIR/tplink-plug-offClusters-device-side.cls"
EPSILON="10.0"
DELETED_SEQUENCES_ON="-1"
DELETED_SEQUENCES_OFF="-1"

PROGRAM_ARGS="'$INPUT_PCAP' '$OUTPUT_PCAP' '$TIMESTAMP_FILE' '$DEVICE_IP' '$ON_SIGNATURE' '$OFF_SIGNATURE' '$ON_ANALYSIS' '$OFF_ANALYSIS' '$EPSILON' '$DELETED_SEQUENCES_ON' '$DELETED_SEQUENCES_OFF'"
./gradlew run -DmainClass=edu.uci.iotproject.SignatureGenerator --args="$PROGRAM_ARGS"
