#!/bin/sh

# This lists down all the calls to the main_flow.sh script.
# Basically, we make one call per one device that we want to analyze.
./main_flow.sh json/eth1.dump.json json/eth1.dump.json result/wemo_switch WeMo_Switch 94:10:3e:36:60:09
#./main_flow.sh json/eth1.dump.json json/eth1.dump.json result/google_nexus Google_Nexus 64:bc:0c:43:3f:40
