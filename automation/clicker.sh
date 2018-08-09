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
	if (( $i % 2 ))
	then
		# live view
		./adb shell input tap 92 139
		./adb shell input tap 92 139
	else
		# stop live view (go to playback)
		./adb shell input tap 92 139
		./adb shell input tap 92 250
	fi
	#date +%r
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	#RAND=$[`jot -r 1 $RAN_STA $RAN_END`]
	#echo "Delay: $RAND seconds"
	#sleep $[$RAND]s
	
	# pick a prime number > 120 seconds
	sleep 131s
done
