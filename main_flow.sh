#!/bin/sh

# This is the main script that calls every other script that is needed for the main flow
if [ $# -ne 4 ]
    then
        echo "Usage: main_flow.sh <json-file> <output-file-name> <device-name> <device-mac-address>"
        echo "       <json-file>    = JSON file of local/traffic captured on WLAN interfaces"
        echo "       <output-file-name>   = base name for the output files"
        echo "       <device-name>        = device name"
        echo "       <device-mac-address> = device MAC address"
        exit 1
fi

# Check result folder and create one if it does not exist yet
[ -d result ] || mkdir result

# Run the analysis
python ./base_gexf_generator.py $1 $2.gexf
python ./parser/parse_packet_frequency.py $1 $2 $3 $4

