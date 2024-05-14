#!/bin/sh

TRACES_DIR="../smarthome"

DIR="amazon-plug"
FEATURE="amazon-plug"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="arlo-camera"
FEATURE="arlo-camera"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="blossom-sprinkler/blossom-sprinkler-quickrun"
FEATURE="blossom-sprinkler-quickrun"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="blossom-sprinkler/blossom-sprinkler-mode"
FEATURE="blossom-sprinkler-mode"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="dlink-plug"
FEATURE="dlink-plug"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="dlink-siren"
FEATURE="dlink-siren"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="ecobee-thermostat/ecobee-thermostat-hvac"
FEATURE="ecobee-thermostat-hvac"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="ecobee-thermostat/ecobee-thermostat-fan"
FEATURE="ecobee-thermostat-fan"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="kwikset-doorlock"
FEATURE="kwikset-doorlock"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="nest-thermostat"
FEATURE="nest-thermostat"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="rachio-sprinkler/rachio-sprinkler-quickrun"
FEATURE="rachio-sprinkler-quickrun"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="rachio-sprinkler/rachio-sprinkler-mode"
FEATURE="rachio-sprinkler-mode"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="ring-alarm"
FEATURE="ring-alarm"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="roomba-vacuum-robot"
FEATURE="roomba-vacuum-robot"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="sengled-bulb/sengled-bulb-onoff"
FEATURE="sengled-bulb-onoff"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
#./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="sengled-bulb/sengled-bulb-intensity"
FEATURE="sengled-bulb-intensity"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="st-plug"
FEATURE="st-plug"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

DIR="tplink-plug"
FEATURE="tplink-plug"
echo "==> START TIME for $DIR: "
date +"%m/%d/%Y %r"
./run.sh $TRACES_DIR/$DIR/eth0/$FEATURE.eth0.detection.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.from-router.pcap $TRACES_DIR/$DIR/event/$FEATURE.eth0.event.to-router.pcap

