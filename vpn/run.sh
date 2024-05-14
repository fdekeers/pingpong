#!/bin/sh

# File that is to be injected by traffic
FILE_MAIN_PCAP=$1

# File that contains event packets that are sent from router
FILE_PACKETS_FROM_ROUTER=$2

# File that contains event packets that are sent to router
FILE_PACKETS_TO_ROUTER=$3

./run-in-loop.sh "$FILE_PACKETS_FROM_ROUTER" &
./run-in-loop.sh "$FILE_PACKETS_TO_ROUTER" &
tcpreplay -i eth1 -q "$FILE_MAIN_PCAP"
echo "==> Finished with the file $FILE_MAIN_PCAP: "
date +"%m/%d/%Y %r"
