#!/bin/bash

# This script does clicking action automatically
# We just need to set up the timer duration

# Loop variables
BEGIN=1
END=100
#END=2
INC=1
DEVICE=$1

# Range of random number (in seconds)
RAN_STA=100
RAN_END=200

for ((i=$BEGIN; i<=$END; i+=$INC));
do
	
	date +"%m/%d/%Y %r"
	#./adb shell getevent -l - use this command to get the position
	# Click on screen
	#./adb -s "$DEVICE" shell input tap 500 1500
	# TP-Link switch
	#./adb -s "$DEVICE" shell input tap 1002 913
	# TP-Link bulb on/off
	#./adb -s "$DEVICE" shell input tap 994 560
	# D-Link switch
	#./adb -s "$DEVICE" shell input tap 987 346
	# D-Link motion sensor
	#./adb -s "$DEVICE" shell input tap 975 840
	# SmartThings plug
	#./adb -s "$DEVICE" shell input tap 921 1188
	#./adb -s "$DEVICE" shell input tap 533 653
	# WeMo, WeMo Insight, LiFX bulbs, Hue bulbs with ST-app
	./adb -s "$DEVICE" shell input tap 533 653
	# WeMo
	#./adb -s "$DEVICE" shell input tap 981 532
	# WeMo Insight
	#./adb -s "$DEVICE" shell input tap 981 326
	# Kwikset doorlock
	#if (( $i % 2 ))
	#then
		# locking
	#	./adb -s "$DEVICE" shell input tap 153 1211
	#else
		# unlocking
	#	./adb -s "$DEVICE" shell input tap 520 1211
	#fi
	# Hue bulb
	#./adb -s "$DEVICE" shell input tap 923 383
	# Lifx bulb
	#./adb -s "$DEVICE" shell input tap 506 580
	# Amcrest camera
	#if (( $i % 2 ))
	#then
		# live view
	#	./adb -s "$DEVICE" shell input tap 92 139
	#	./adb -s "$DEVICE" shell input tap 92 139
	#else
		# stop live view (go to playback)
	#	./adb -s "$DEVICE" shell input tap 92 139
	#	./adb -s "$DEVICE" shell input tap 92 250
	#fi
	# Arlo camera
	#if (( $i % 2 ))
	#then
		# live view
		#./adb -s "$DEVICE" shell input tap 532 740
	#	./adb -s "$DEVICE" shell input tap 533 653
	#else
		# stop live view
		#./adb -s "$DEVICE" shell input tap 101 1012
	#	./adb -s "$DEVICE" shell input tap 533 653
	#	./adb -s "$DEVICE" shell input tap 533 653
	#	./adb -s "$DEVICE" shell input tap 533 653
	#fi
	# Blossom - turning on/off 1 zone
	#if (( $i % 2 ))
	#then
		# start watering
	#	./adb -s "$DEVICE" shell input tap 538 1597
	#else
		# stop watering
	#	./adb -s "$DEVICE" shell input tap 496 1533
	#fi
	# Blossom - change mode active/hibernate
	#./adb -s "$DEVICE" shell input tap 1002 1176
	# Dlink siren
	#./adb -s "$DEVICE" shell input tap 994 802
	# Nest thermostat
	#if (( $i % 2))
	#then
		# start fan
	#	./adb -s "$DEVICE" shell input tap 524 1668
	#	./adb -s "$DEVICE" shell input tap 936 1709
	#else
		# stop fan
	#	./adb -s "$DEVICE" shell input tap 524 1668
	#	./adb -s "$DEVICE" shell input tap 679 1702
	#fi
	# Alexa
        #if (( $i % 2))
        #then
		# Using Google Translate and 
		# 	having her speak to Alexa
                # Question 1
        #        ./adb -s "$DEVICE" shell input tap 907 145
        #        ./adb -s "$DEVICE" shell input tap 543 692
	#	./adb -s "$DEVICE" shell input tap 148 775
        #else
                # Question 2
        #        ./adb -s "$DEVICE" shell input tap 907 145
        #        ./adb -s "$DEVICE" shell input tap 463 1668
	#	./adb -s "$DEVICE" shell input tap 148 775
        #fi
	#date +%r
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	#RAND=$[`jot -r 1 $RAN_STA $RAN_END`]
	#echo "Delay: $RAND seconds"
	#sleep $[$RAND]s

	# TP-Link bulb - change colors & intensity
        #if (( $i % 2 ))
        #then
                # color 1 - horizontal
                #./adb -s "$DEVICE" shell input tap 250 1000
		# color 3 - vertical
                #./adb -s "$DEVICE" shell input tap 500 1250
		# intensity change
        #        ./adb -s "$DEVICE" shell input tap 150 1405
        #else
                # color 2 - horizontal
                #./adb -s "$DEVICE" shell input tap 750 1000
		# color 4 - vertical
                #./adb -s "$DEVICE" shell input tap 500 750
		# intensity change
        #        ./adb -s "$DEVICE" shell input tap 950 1405
        #fi
	# Hue bulb - change intensity
        #if (( $i % 2 ))
        #then
                # intensity change (bright)
        #        ./adb -s "$DEVICE" shell input tap 300 550
        #else
                # intensity change (dimmed)
        #        ./adb -s "$DEVICE" shell input tap 800 550
        #fi
	# Arlo camera - arm/disarm
        #if (( $i % 2 ))
        #then
                # Arm
        #        ./adb -s "$DEVICE" shell input tap 500 500
        #else
                # Disarm
        #        ./adb -s "$DEVICE" shell input tap 500 750
        #fi
	# Arlo camera - record
        #./adb -s "$DEVICE" shell input tap 515 923
	#./adb -s "$DEVICE" shell input tap 524 883
	# Arlo camera - sound (on/off)
        #./adb -s "$DEVICE" shell input tap 70 850
	# Nest thermostat mode change (heat/cool)
        #if (( $i % 2))
        #then
                # auto (heat/cool)
        #       ./adb -s "$DEVICE" shell input tap 110 1700
        #       ./adb -s "$DEVICE" shell input tap 110 1600
        #else
                # off
        #       ./adb -s "$DEVICE" shell input tap 110 1700
        #       ./adb -s "$DEVICE" shell input tap 110 1700
        #fi        
	# Nest thermostat mode change (start/stop eco mode)
        #./adb -s "$DEVICE" shell input tap 350 1700
        #./adb -s "$DEVICE" shell input tap 350 1600
	# Sengled bulb - ON/OFF
        #./adb -s "$DEVICE" shell input tap 550 800
	# Sengled bulb - intensity
	#if (( $i % 2 ))
        #then
                # 1%
        #        ./adb -s "$DEVICE" shell input tap 70 1150
        #else
                # 100%
        #        ./adb -s "$DEVICE" shell input tap 1020 1150
        #fi
	# Rachio sprinkler - ON/OFF
	#if (( $i % 2 ))
        #then
                # ON
        #        ./adb -s "$DEVICE" shell input tap 900 1500
        #        ./adb -s "$DEVICE" shell input tap 300 600
        #        ./adb -s "$DEVICE" shell input tap 800 1700
        #        ./adb -s "$DEVICE" shell input tap 800 1700
        #else
                # OFF/PAUSE
        #        ./adb -s "$DEVICE" shell input tap 920 1650
        #fi
	# Rachio sprinkler - standby/active
        #if (( $i % 2 ))
        #then
                # Standby mode
        #        ./adb -s "$DEVICE" shell input tap 700 700
        #        ./adb -s "$DEVICE" shell input tap 700 1100
        #else
                # Active mode
        #        ./adb -s "$DEVICE" shell input tap 600 700
        #fi
	# Ring doorbell - ring alerts
	#./adb -s "$DEVICE" shell input tap 1000 570
	# Ring doorbell - motion alerts
	#./adb -s "$DEVICE" shell input tap 1000 750
	# Ecobee thermostat - ON/OFF
        #if (( $i % 2 ))
        #then
                # ON (Auto)
        #        ./adb -s "$DEVICE" shell input tap 550 550
        #else
                # OFF 
        #        ./adb -s "$DEVICE" shell input tap 550 650
        #fi
	# Ecobee thermostat - fan ON/Auto
        #if (( $i % 2 )) 
        #then
                # ON 
        #        ./adb -s "$DEVICE" shell input tap 900 650
        #else
                # Auto
        #        ./adb -s "$DEVICE" shell input tap 700 650
        #fi
	# Nest camera - ON/OFF
        #./adb -s "$DEVICE" shell input tap 1020 320
	# Nest camera - Audio Talk/Done
        #./adb -s "$DEVICE" shell input tap 550 1650
	# Nest camera - Mic ON/OFF
        #./adb -s "$DEVICE" shell input tap 1020 630
	# Roomba - ON/OFF
	#if (( $i % 2 ))
        #then
                # ON
        #        ./adb -s "$DEVICE" shell input tap 550 950
        #else
                # OFF
        #        ./adb -s "$DEVICE" shell input tap 550 950
	#	sleep 1s # need a short sleep here
        #        ./adb -s "$DEVICE" shell input tap 550 1600
	#	sleep 1s # need a short sleep here
        #        ./adb -s "$DEVICE" shell input tap 550 950
        #fi
	# Amazon plug - ON/OFF
        #./adb -s "$DEVICE" shell input tap 550 700
	# Ring Alarm - Arm/Disarm
        #if (( $i % 2 ))
        #then
                # Arm (Away)
        #        ./adb -s "$DEVICE" shell input tap 850 350
        #else
                # Disarm
        #        ./adb -s "$DEVICE" shell input tap 300 350
        #fi

	# pick a prime number > 120 seconds
	sleep 131s
	#sleep 53s
done
