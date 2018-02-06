#!/bin/sh

# This is the main script that calls every other script that is needed for the main flow
if [ $# -ne 5 ]
    then
        echo "Usage: main_flow.sh <json-file-local> <json-file-internet> <output-file-name>"
        echo "       <json-file-local>    = JSON file of local traffic captured on WLAN interfaces"
        echo "       <json-file-internet> = JSON file of internet traffic captured on ETH interfaces"
        echo "       <output-file-name>   = base name for the output files"
        echo "       <device-name>        = device name"
        echo "       <device-mac-address> = device MAC address"
        exit 1
fi

# Check result folder and create one if it does not exist yet
[ -d $5 ] || mkdir $5

# Run the analysis
python ./base_gexf_generator.py $1 $3_local.gexf
python ./base_gexf_generator.py $2 $3_internet.gexf
python ./parser/parse_packet_frequency.py $1 $3_local $4 $5
python ./parser/parse_packet_frequency.py $2 $3_internet $4 $5
gnuplot ./plot_scripts/plot_ts_graph_wemo

