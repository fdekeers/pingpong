#!/bin/bash

# This script does clicking action automatically
# We just need to set up the timer duration

# Range of random number (in seconds)
# Five websites
BRO_RAN_STA=0
BRO_RAN_END=4
# Browsing interval (every 100-500 seconds)
SLP_RAN_STA=100
SLP_RAN_END=500

# List of websites
WEBSITE[0]="https://www.google.com"
WEBSITE[1]="https://www.youtube.com"
WEBSITE[2]="https://www.facebook.com"
WEBSITE[3]="https://www.amazon.com"
WEBSITE[4]="https://www.reddit.com"
WEBSITE[5]="https://www.wikipedia.org"
WEBSITE[6]="https://www.yahoo.com"
WEBSITE[7]="https://twitter.com"
WEBSITE[8]="https://www.instagram.com"
WEBSITE[9]="https://www.linkedin.com"

while true
do
	# Choose a website randomly
	#RAND=$[( $RANDOM % $RAN_END ) + $RAN_STA]
	RAND=$[`jot -r 1 $BRO_RAN_STA $BRO_RAN_END`]
	echo "${WEBSITE[$RAND]}"
	curl ${WEBSITE[$RAND]}
	
	# Sleep with random delay time	
	RAND=$[`jot -r 1 $SLP_RAN_STA $SLP_RAN_END`]
	echo "Delay: $RAND seconds"
	sleep $[$RAND]s

	# pick a prime number > 120 seconds
	#sleep 131s
done


