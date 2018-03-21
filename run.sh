#!/bin/sh

# This lists down all the calls to the main_flow.sh script.
# Basically, we make one call per one device that we want to analyze.
ROUTER=Router
ROUTER_MAC=b0:b9:8a:73:69:8f
PHONE=Nexus_5_Black
PHONE_MAC=64:89:9a:86:a9:7d
#PHONE=Motorola
#PHONE_MAC=a8:96:75:2f:0c:9c

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

#PREFIX=dlink
#DEVICE=DLink_Switch
#DEVICE_MAC=90:8d:78:e3:81:0c
#PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/dlink/setup
#PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/dlink/local
#PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/dlink/remote

# For smartthings-plug we need to uncomment the eth1 command line below instead of wlan1
PREFIX=smartthings-plug
DEVICE=SmartThings_Plug
DEVICE_MAC=d0:52:a8:a3:60:0f
PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/smartthings-plug/setup
PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/smartthings-plug/local
PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/smartthings-plug/remote

#PREFIX=smartthings-mp-sensor
#DEVICE=SmartThings_Multipurpose_Sensor
#DEVICE_MAC=d0:52:a8:a3:60:0f
#PATH_SETUP=/scratch/traffic_measurements/Switches-Feb2018/smartthings-mp-sensor/setup
#PATH_LOCAL=/scratch/traffic_measurements/Switches-Feb2018/smartthings-mp-sensor/local
#PATH_REMOTE=/scratch/traffic_measurements/Switches-Feb2018/smartthings-mp-sensor/remote

PATH_SETUP_WLAN1_JSON=$PREFIX.wlan1.setup.json
PATH_SETUP_ETH0_JSON=$PREFIX.eth0.setup.json
PATH_SETUP_ETH1_JSON=$PREFIX.eth1.setup.json
PATH_LOCAL_WLAN1_DNS_JSON=$PREFIX.wlan1.local.dns.json
PATH_LOCAL_WLAN1_JSON=$PREFIX.wlan1.local.json

PATH_LOCAL_ETH0_DNS_JSON=$PREFIX.eth0.local.dns.json
PATH_LOCAL_ETH0_JSON=$PREFIX.eth0.local.json
PATH_LOCAL_ETH1_DNS_JSON=$PREFIX.eth1.local.dns.json
PATH_LOCAL_ETH1_JSON=$PREFIX.eth1.local.json
PATH_REMOTE_WLAN1_DNS_JSON=$PREFIX.wlan1.remote.dns.json
PATH_REMOTE_WLAN1_JSON=$PREFIX.wlan1.remote.json
PATH_REMOTE_ETH0_DNS_JSON=$PREFIX.eth0.remote.dns.json
PATH_REMOTE_ETH0_JSON=$PREFIX.eth0.remote.json
PATH_REMOTE_ETH1_DNS_JSON=$PREFIX.eth1.remote.dns.json
PATH_REMOTE_ETH1_JSON=$PREFIX.eth1.remote.json
PATH_GNUPLOT=./plot_scripts/plot_ts_graph_$PREFIX
PATH_GNUPLOT_LABELED=./plot_scripts/plot_ts_graph_$PREFIX\_labeled
PATH_GNUPLOT_COMBINED=./plot_scripts/plot_ts_graph_$PREFIX\_combined
PATH_DIR_RESULT=result
PATH_RESULT_SETUP_WLAN1=$PREFIX\_switch_wlan1_setup
PATH_RESULT_SETUP_ETH1=$PREFIX\_switch_eth1_setup
PATH_RESULT_SETUP_ETH0=$PREFIX\_switch_eth0_setup
PATH_RESULT_LOCAL_WLAN1=$PREFIX\_switch_wlan1_local
PATH_RESULT_LOCAL_ETH1=$PREFIX\_switch_eth1_local
PATH_RESULT_LOCAL_ETH0=$PREFIX\_switch_eth0_local
PATH_RESULT_REMOTE_WLAN1=$PREFIX\_switch_wlan1_remote
PATH_RESULT_REMOTE_ETH1=$PREFIX\_switch_eth1_remote
PATH_RESULT_REMOTE_ETH0=$PREFIX\_switch_eth0_remote
PATH_RESULT_PHONE_LOCAL_WLAN1=$PREFIX\_phone_wlan1_local
PATH_RESULT_PHONE_REMOTE_WLAN1=$PREFIX\_phone_wlan1_remote

#python ./base_gexf_generator.py $PATH_SETUP/$PATH_SETUP_WLAN1_JSON $PATH_DIR_RESULT/$PATH_RESULT_SETUP_WLAN1.gexf
#python ./base_gexf_generator.py $PATH_SETUP/$PATH_SETUP_ETH0_JSON $PATH_DIR_RESULT/$PATH_RESULT_SETUP_ETH0.gexf
#python ./base_gexf_generator.py $PATH_SETUP/$PATH_SETUP_ETH1_JSON $PATH_DIR_RESULT/$PATH_RESULT_SETUP_ETH1.gexf

#python ./base_gexf_generator.py $PATH_LOCAL/$PATH_LOCAL_WLAN1_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_WLAN1.gexf
#python ./parser/parse_packet_frequency.py $PATH_LOCAL/$PATH_LOCAL_WLAN1_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_WLAN1 $DEVICE $DEVICE_MAC
#python ./base_gexf_generator.py $PATH_LOCAL/$PATH_LOCAL_ETH0_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH0.gexf
#python ./parser/parse_packet_frequency.py $PATH_LOCAL/$PATH_LOCAL_ETH0_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH0 $ROUTER $ROUTER_MAC
#python ./base_gexf_generator.py $PATH_LOCAL/$PATH_LOCAL_ETH1_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH1.gexf
python ./parser/parse_packet_frequency.py $PATH_LOCAL/$PATH_LOCAL_ETH1_JSON $PATH_DIR_RESULT/$PATH_RESULT_LOCAL_ETH1 $DEVICE $DEVICE_MAC

#python ./base_gexf_generator.py $PATH_REMOTE/$PATH_REMOTE_WLAN1_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_WLAN1.gexf
#python ./parser/parse_packet_frequency.py $PATH_REMOTE/$PATH_REMOTE_WLAN1_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_WLAN1 $DEVICE $DEVICE_MAC
#python ./base_gexf_generator.py $PATH_REMOTE/$PATH_REMOTE_ETH0_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_ETH0.gexf
#python ./parser/parse_packet_frequency.py $PATH_REMOTE/$PATH_REMOTE_ETH0_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_ETH0 $ROUTER $ROUTER_MAC
#python ./base_gexf_generator.py $PATH_REMOTE/$PATH_REMOTE_ETH1_DNS_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_ETH1.gexf
python ./parser/parse_packet_frequency.py $PATH_REMOTE/$PATH_REMOTE_ETH1_JSON $PATH_DIR_RESULT/$PATH_RESULT_REMOTE_ETH1 $DEVICE $DEVICE_MAC

#python ./parser/parse_packet_frequency.py $PATH_LOCAL/$PATH_LOCAL_WLAN1_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_LOCAL_WLAN1 $PHONE $PHONE_MAC
#python ./parser/parse_packet_frequency.py $PATH_REMOTE/$PATH_REMOTE_WLAN1_JSON $PATH_DIR_RESULT/$PATH_RESULT_PHONE_REMOTE_WLAN1 $PHONE $PHONE_MAC

#gnuplot $PATH_GNUPLOT
gnuplot $PATH_GNUPLOT_LABELED   # only the WLAN1/ETH1 part - basically just the device perspective
#gnuplot $PATH_GNUPLOT_COMBINED
