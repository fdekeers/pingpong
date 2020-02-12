#!/bin/bash

# This script does clicking action automatically
# We just need to set up the timer duration

# Parameters:
# $1 : device identification (run "adb devices" to figure out)

# Loop variables
BEGIN=1
#END=100
END=4
INC=1
DEVICE=$1

# Range of random number (in seconds)
RAN_STA=100
RAN_END=200

for ((i=$BEGIN; i<=$END; i+=$INC));
do
	
	date +"%m/%d/%Y %r"
	#./adb -s "$DEVICE" shell getevent -l - use this command to get the position
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
	# Blossom
	#if (( $i % 2 ))
	#then
		# start watering
	#	./adb -s "$DEVICE" shell input tap 538 1597
	#else
		# stop watering
	#	./adb -s "$DEVICE" shell input tap 496 1533
	#fi
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

	# TP-Link bulb - change colors
        #if (( $i % 2 ))
        #then
                # color 1 - horizontal
                #./adb -s "$DEVICE" shell input tap 250 1000
		# color 3 - vertical
        #        ./adb -s "$DEVICE" shell input tap 500 1250
        #else
                # color 2 - horizontal
                #./adb -s "$DEVICE" shell input tap 750 1000
		# color 4 - vertical
        #        ./adb -s "$DEVICE" shell input tap 500 750
        #fi
	
	# pick a prime number > 120 seconds
	sleep 131s
	#sleep 53s
done
