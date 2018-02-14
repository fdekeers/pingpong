#!/bin/sh

# This lists down all the calls to the main_flow.sh script.
# Basically, we make one call per one device that we want to analyze.
ROUTER=Router
ROUTER_MAC=b0:b9:8a:73:69:8f
#PHONE=Nexus_5_Black
#PHONE_MAC=64:89:9a:86:a9:7d
PHONE=Motorola
PHONE_MAC=a8:96:75:2f:0c:9c

#PREFIX=wemo
#DEVICE=WeMo_Switch
#DEVICE_MAC=94:10:3e:36:60:09
#PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/wemo/setup
#PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/wemo/local
#PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/wemo/remote

#PREFIX=tplink
#DEVICE=TPLink_Switch
#DEVICE_MAC=50:c7:bf:33:1f:09
#PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/tplink/setup
#PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/tplink/local
#PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/tplink/remote

#PREFIX=wemo-insight
#DEVICE=WeMo_Insight_Switch
#DEVICE_MAC=14:91:82:25:10:77
#PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/wemo-insight/setup
#PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/wemo-insight/local
#PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/wemo-insight/remote

PREFIX=dlink
DEVICE=DLink_Switch
DEVICE_MAC=90:8d:78:e3:81:0c
PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/dlink/setup
PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/dlink/local
PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/dlink/remote

PATH_SETUP_WLAN_JSON=$PREFIX.wlan1.setup.json
PATH_SETUP_ETH_JSON=$PREFIX.eth0.setup.json
PATH_LOCAL_WLAN_JSON=$PREFIX.wlan1.local.json
PATH_LOCAL_ETH_JSON=$PREFIX.eth0.local.json
PATH_REMOTE_WLAN_JSON=$PREFIX.wlan1.remote.json
PATH_REMOTE_ETH_JSON=$PREFIX.eth0.remote.json
PATH_GNUPLOT=./plot_scripts/plot_ts_graph_$PREFIX
PATH_GNUPLOT_COMBINED=./plot_scripts/plot_ts_graph_$PREFIX\_combined
PATH_DIR_RESULT=result
PATH_RESULT_SETUP_WLAN=$PREFIX\_switch_wlan_setup
PATH_RESULT_SETUP_ETH=$PREFIX\_switch_eth_setup
PATH_RESULT_LOCAL_WLAN=$PREFIX\_switch_wlan_local
PATH_RESULT_LOCAL_ETH=$PREFIX\_switch_eth_local
PATH_RESULT_REMOTE_WLAN=$PREFIX\_switch_wlan_remote
PATH_RESULT_REMOTE_ETH=$PREFIX\_switch_eth_remote
PATH_RESULT_PHONE_LOCAL_WLAN=$PREFIX\_phone_wlan_local
PATH_RESULT_PHONE_REMOTE_WLAN=$PREFIX\_phone_wlan_remote

./main_flow.sh $PATH_SETUP/$PATH_SETUP_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_SETUP_WLAN $DEVICE $DEVICE_MAC
./main_flow.sh $PATH_SETUP/$PATH_SETUP_ETH_JSON $PATH_DIR_RESULT/$PATH_RESULT_SETUP_ETH $ROUTER $ROUTER_MAC
./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_WLAN $DEVICE $DEVICE_MAC
./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_ETH_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH $ROUTER $ROUTER_MAC
./main_flow.sh $PATH_REMOTE/$PATH_REMOTE_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_WLAN $DEVICE $DEVICE_MAC
./main_flow.sh $PATH_REMOTE/$PATH_REMOTE_ETH_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_ETH $ROUTER $ROUTER_MAC
./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_LOCAL_WLAN $PHONE $PHONE_MAC
./main_flow.sh $PATH_REMOTE/$PATH_REMOTE_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_REMOTE_WLAN $PHONE $PHONE_MAC
#gnuplot $PATH_GNUPLOT
gnuplot $PATH_GNUPLOT_COMBINED
