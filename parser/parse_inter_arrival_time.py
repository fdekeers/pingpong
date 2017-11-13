#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) and analyze
the packet inter-arrival times of a certain device at a certain time.
"""

import sys
import json
import numpy as np
from collections import defaultdict
from dateutil import parser
from decimal import *

JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"

JSON_KEY_ETH = "eth"
JSON_KEY_ETH_DST = "eth.dst"
JSON_KEY_ETH_SRC = "eth.src"
JSON_KEY_FRAME = "frame"
JSON_KEY_FRAME_TIME = "frame.time_epoch"
TABLE_HEADER_X = "Packet number"
TABLE_HEADER_Y = "Time (seconds)"
INCOMING_APPENDIX = "_incoming"
OUTGOING_APPENDIX = "_outgoing"
FILE_APPENDIX = ".dat"


def save_to_file(tblheader, timestamp_list, filenameout):
    """ Show summary of statistics of PCAP file
        Args:
            tblheader: header for the saved table
            dictionary: dictionary to be saved
            filename_out: file name to save
    """
    # Appending, not overwriting!
    f = open(filenameout, 'a')
    # Write the table header
    f.write("# " + tblheader + "\n")
    f.write("# " + TABLE_HEADER_X + " " + TABLE_HEADER_Y + "\n")
    # Write "0 0" if dictionary is empty
    if not timestamp_list:
        f.write("0 0")
        f.close()
        print "Writing zeroes to file: ", filenameout
        return
    ind = 0
    # Iterate over list and write index-value pairs
    for val in timestamp_list:
        # Space separated
        f.write(str(ind) + " " + str(timestamp_list[ind]) + "\n")
        ind += 1
    f.close()
    print "Writing output to file: ", filenameout


def main():
    """ Main function
    """
    if len(sys.argv) < 5:
        print "Usage: python", sys.argv[0], "<input_file> <output_file> <device_name> <mac_address>"
        return
    # Parse the file for the specified MAC address
    timestamplist_incoming = parse_json(sys.argv[1], sys.argv[4])
    # Write statistics into file
    print "====================================================================="
    print "==> Analyzing incoming traffic ..."
    save_to_file(sys.argv[3] + INCOMING_APPENDIX, timestamplist_incoming, sys.argv[2] + INCOMING_APPENDIX + FILE_APPENDIX)
    print "====================================================================="
    #print "==> Analyzing outgoing traffic ..."
    #save_to_file(sys.argv[3] + OUTGOING_APPENDIX, timestamplist_outgoing, sys.argv[2] + OUTGOING_APPENDIX + FILE_APPENDIX)
    #print "====================================================================="


# Convert JSON file containing DNS traffic to a map in which a hostname points to its set of associated IPs.
def parse_json(filepath, macaddress):
    """ Show summary of statistics of PCAP file
        Args:
            filepath: path of the read file
            macaddress: MAC address of a device to analyze
    """
    # Maps timestamps to frequencies of packets
    timestamplist = list()
    with open(filepath) as jf:
        # Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)
        # Loop through json objects in data
        # Each entry is a pcap entry (request/response (packet) and associated metadata)
        # Preserve two pointers prev and curr to iterate over the timestamps
        prev = None
        curr = None
        for p in data:
            # p is a JSON object, not an index
            layers = p[JSON_KEY_SOURCE][JSON_KEY_LAYERS]
            # Get timestamp
            frame = layers.get(JSON_KEY_FRAME, None)
            timestamp = Decimal(frame.get(JSON_KEY_FRAME_TIME, None))
            # Get into the Ethernet address part
            eth = layers.get(JSON_KEY_ETH, None)
            # Skip any non DNS traffic
            if eth is None:
                print "[ WARNING: Packet has no ethernet address! ]"
                continue
            # Get source and destination MAC addresses
            src = eth.get(JSON_KEY_ETH_SRC, None)
            dst = eth.get(JSON_KEY_ETH_DST, None)
            # Get and count the traffic for the specified MAC address
            if dst == macaddress:
                # Check if timestamp already exists in the map
                # If yes, then just increment the frequency value...
                print str(timestamp) + " - src:" + str(src) + " - dest:" + str(dst)
                curr = timestamp
                if prev is not None:
                    inter_arrival_time = curr - prev
                    timestamplist.append(inter_arrival_time)
                prev = curr

    return timestamplist


if __name__ == '__main__':
    main()

