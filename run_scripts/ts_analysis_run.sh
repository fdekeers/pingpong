#!/bin/sh

# ts_analysis = timestamp analysis
# Check input arguments - we need 2 arguments
if [ $# -ne 2 ]
    then
        echo "Usage: ts_analysis_run.sh <path-and-json-file, e.g./a/b/c/d.json> <path-to-output-file, e.g. result_ts/>"
        exit 1
fi

# Check result folder and create one if it does not exist yet
[ -d $2 ] || mkdir $2

# Run the analysis
python ../parser/parse_packet_frequency.py $1 $2/wemo_switch WeMo_Switch 94:10:3e:36:60:09
python ../parser/parse_packet_frequency.py $1 $2/wemo_insight WeMo_Insight 14:91:82:25:10:77
python ../parser/parse_packet_frequency.py $1 $2/tplink_switch TPLink_Switch 50:c7:bf:33:1f:09
python ../parser/parse_packet_frequency.py $1 $2/dlink_switch DLink_Switch 90:8d:78:e3:81:0c
python ../parser/parse_packet_frequency.py $1 $2/amcrest_camera Amcrest_Camera 3c:ef:8c:6f:79:5a
python ../parser/parse_packet_frequency.py $1 $2/netgear_arlo_camera Netgear_Arlo_Camera 40:5d:82:2f:50:2a
python ../parser/parse_packet_frequency.py $1 $2/lifx_lightbulb_1 Lifx_LightBulb_1 d0:73:d5:12:8e:30
python ../parser/parse_packet_frequency.py $1 $2/lifx_lightbulb_2 Lifx_LightBulb_2 d0:73:d5:02:41:da
python ../parser/parse_packet_frequency.py $1 $2/philips_hue Philips_Hue 00:17:88:69:ee:e4
python ../parser/parse_packet_frequency.py $1 $2/tplink_lightbulb TPLink_LightBulb 50:c7:bf:59:d5:84
python ../parser/parse_packet_frequency.py $1 $2/nxeco_sprinkler Nxeco_Sprinkler ac:cf:23:5a:9c:e2
python ../parser/parse_packet_frequency.py $1 $2/blossom_sprinkler Blossom_Sprinkler e4:95:6e:b0:20:39
python ../parser/parse_packet_frequency.py $1 $2/dlink_alarm DLink_Alarm c4:12:f5:de:38:20
python ../parser/parse_packet_frequency.py $1 $2/dlink_motion_sensor DLink_Motion_Sensor c4:12:f5:e3:dc:17
python ../parser/parse_packet_frequency.py $1 $2/nest_thermostat Nest_Thermostat 18:b4:30:bf:34:7e
python ../parser/parse_packet_frequency.py $1 $2/amazon_echo_dot Amazon_Echo_Dot 68:37:e9:d2:26:0d
python ../parser/parse_packet_frequency.py $1 $2/smartthings_hub SmartThings_Hub d0:52:a8:a3:60:0f

