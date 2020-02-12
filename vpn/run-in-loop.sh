#!/bin/sh

FILE_TO_INJECT=$1
#RAND_START=0
#RAND_END=60
TIMES=101

COUNT=1
while [ $COUNT -lt $TIMES ]
do
	#RAND=$[`shuf -i $RAND_START-$RAND_END -n 1`]
	#RAND="$(awk 'BEGIN{srand();print int(rand()*60)}')"
	# This one works well to generate a random number between 0-999 on OpenWrt
	RAND=$(head -30 /dev/urandom | tr -dc "0123456789" | head -c3)
	sleep $(($RAND%240))
  	tcpreplay -i eth1 -q "$FILE_TO_INJECT"
	echo "Delay for $FILE_TO_INJECT: $(($RAND%240)) seconds"
	date +"%m/%d/%Y %r"
	COUNT=`expr $COUNT + 1`
done
