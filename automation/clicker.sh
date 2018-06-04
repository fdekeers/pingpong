#!/bin/bash

# This script does clicking action automatically
# We just need to set up the timer duration

# Loop variables
BEGIN=1
END=5
INC=1

# Range of random number (in seconds)
RAN_STA=2
RAN_END=6

for ((i=$BEGIN; i<=$END; i+=$INC));
do
	# TP-Link switch
	#./adb shell input tap 1002 913
	date +%r
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	RAND=$[`jot -r 1 $RAN_STA $RAN_END`]
	#echo "Delay: $RAND seconds"
	sleep $[$RAND]s
done
