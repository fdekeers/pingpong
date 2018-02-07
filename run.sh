#!/bin/sh

# This lists down all the calls to the main_flow.sh script.
# Basically, we make one call per one device that we want to analyze.
PREFIX=wemo
DEVICE=WeMo_Switch
DEVICE_MAC=94:10:3e:36:60:09
ROUTER=Router
ROUTER_MAC=b0:b9:8a:73:69:8f
PHONE=Nexus_5_Black
PHONE_MAC=64:89:9a:86:a9:7d
PATH_LOCAL=/scratch/traffic_measurements/WeMo-February-2018/wemo-local
PATH_INTERNET=/scratch/traffic_measurements/WeMo-February-2018/wemo-internet
PATH_LOCAL_WLAN_JSON=$PREFIX.wlan1.local.json
PATH_LOCAL_ETH_JSON=$PREFIX.eth0.local.json
PATH_INTERNET_WLAN_JSON=$PREFIX.wlan1.internet.json
PATH_INTERNET_ETH_JSON=$PREFIX.eth0.internet.json
PATH_GNUPLOT=./plot_scripts/plot_ts_graph_$PREFIX
PATH_DIR_RESULT=result
PATH_RESULT_LOCAL_WLAN=$PREFIX\_switch_wlan_local
PATH_RESULT_LOCAL_ETH=$PREFIX\_switch_eth_local
PATH_RESULT_INTERNET_WLAN=$PREFIX\_switch_wlan_internet
PATH_RESULT_INTERNET_ETH=$PREFIX\_switch_eth_internet
PATH_RESULT_PHONE_LOCAL_WLAN=$PREFIX\_phone_wlan_local
PATH_RESULT_PHONE_INTERNET_WLAN=$PREFIX\_phone_wlan_internet

./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_WLAN $DEVICE $DEVICE_MAC
./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_ETH_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH Router b0:b9:8a:73:69:8f
./main_flow.sh $PATH_INTERNET/$PATH_INTERNET_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_INTERNET_WLAN WeMo_Switch 94:10:3e:36:60:09
./main_flow.sh $PATH_INTERNET/$PATH_INTERNET_ETH_JSON $PATH_DIR_RESULT/$PATH_RESULT_INTERNET_ETH Router b0:b9:8a:73:69:8f
./main_flow.sh $PATH_LOCAL/$PATH_LOCAL_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_LOCAL_WLAN Nexus_5_Black 64:89:9a:86:a9:7d
./main_flow.sh $PATH_INTERNET/$PATH_INTERNET_WLAN_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_INTERNET_WLAN Nexus_5_Black 64:89:9a:86:a9:7d
gnuplot $PATH_GNUPLOT

