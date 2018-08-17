#!/bin/bash

# This script does clicking action automatically
# We just need to set up the timer duration

# Loop variables
BEGIN=1
END=100
INC=1

# Range of random number (in seconds)
RAN_STA=100
RAN_END=200

for ((i=$BEGIN; i<=$END; i+=$INC));
do
	
	date +"%m/%d/%Y %r"
	#./adb shell getevent -l - use this command to get the position
	# TP-Link switch
	#./adb shell input tap 1002 913
	# TP-Link bulb on/off
	#./adb shell input tap 994 560
	# D-Link switch
	#./adb shell input tap 987 346
	# SmartThings
	#./adb shell input tap 921 1188
	# WeMo
	#./adb shell input tap 981 532
	# WeMo Insight
	#./adb shell input tap 981 326
	# Kwikset doorlock
	#if (( $i % 2 ))
	#then
		# locking
	#	./adb shell input tap 153 1211
	#else
		# unlocking
	#	./adb shell input tap 520 1211
	#fi
	# Hue bulb
	#./adb shell input tap 923 383
	# Lifx bulb
	#./adb shell input tap 506 580
	# Amcrest camera
	#if (( $i % 2 ))
	#then
		# live view
	#	./adb shell input tap 92 139
	#	./adb shell input tap 92 139
	#else
		# stop live view (go to playback)
	#	./adb shell input tap 92 139
	#	./adb shell input tap 92 250
	#fi
	# Arlo camera
	#if (( $i % 2 ))
	#then
		# live view
	#	./adb shell input tap 532 740
	#else
		# stop live view
	#	./adb shell input tap 101 1012
	#fi
	# Blossom
	#if (( $i % 2 ))
	#then
		# start watering
	#	./adb shell input tap 538 1597
	#else
		# stop watering
	#	./adb shell input tap 496 1533
	#fi
	# Dlink siren
	#./adb shell input tap 994 802
	# Nest thermostat
	#if (( $i % 2 ))
	#then
		# start fan
	#	./adb shell input tap 524 1668
	#	./adb shell input tap 936 1709
	#else
		# stop fan
	#	./adb shell input tap 524 1668
	#	./adb shell input tap 679 1702
	#fi
	# Alexa
        if (( $i % 2 ))
        then
		# Using Google Translate and 
		# 	having her speak to Alexa
                # Question 1
                ./adb shell input tap 907 145
                ./adb shell input tap 543 692
				./adb shell input tap 148 775
        else
                # Question 2
                ./adb shell input tap 907 145
                ./adb shell input tap 463 1668
				./adb shell input tap 148 775
        fi
	#date +%r
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	#RAND=$[`jot -r 1 $RAN_STA $RAN_END`]
	#echo "Delay: $RAND seconds"
	#sleep $[$RAND]s
	
	# pick a prime number > 120 seconds
	sleep 131s
done
