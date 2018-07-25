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
	#./adb shell getevent -l - use this command to get the position
	# TP-Link switch
	#./adb shell input tap 1002 913
	# D-Link switch
	#./adb shell input tap 987 346
	# SmartThings
	./adb shell input tap 921 1188
	#date +%r
	date +"%m/%d/%Y %r"
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	#RAND=$[`jot -r 1 $RAN_STA $RAN_END`]
	#echo "Delay: $RAND seconds"
	#sleep $[$RAND]s
	
	# pick a prime number > 120 seconds
	sleep 131s
done
