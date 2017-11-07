#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) and analyze
the traffic frequency of a certain device at a certain time.
"""

import sys
import json
from collections import defaultdict
from dateutil import parser

JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"

JSON_KEY_ETH = "eth"
JSON_KEY_ETH_DST = "eth.dst"
JSON_KEY_ETH_SRC = "eth.src"
JSON_KEY_FRAME = "frame"
JSON_KEY_FRAME_TIME = "frame.time"
TABLE_HEADER_X = "Timestamp (hh:mm:ss)"
TABLE_HEADER_Y = "Packet frequency (pps)"


def save_to_file(tbl_header, dictionary, filename_out):
    """ Show summary of statistics of PCAP file
        Args:
            tbl_header: header for the saved table
            dictionary: dictionary to be saved
            filename_out: file name to save
    """
    # Appending, not overwriting!
    f = open(filename_out, 'a')
    # Write the table header
    f.write("# " + TABLE_HEADER_X + " " + TABLE_HEADER_Y + "\n");
    # Iterate over dictionary and write (key, value) pairs
    for key in sorted(dictionary):
        # Comma separated
        #f.write(str(key) + ", " + str(dictionary[key]) + "\n")
        # Space separated
        f.write(str(key) + " " + str(dictionary[key]) + "\n")
    # Write "0 0" if dictionary is empty
    if not dictionary:
        f.write("0 0");

    f.close()
    print "Writing output to file: ", filename_out


def main():
    """ Main function
    """
    if len(sys.argv) < 5:
        print "Usage: python", sys.argv[0], "<input_file> <output_file> <device_name> <mac_address>"
        return
    # Parse the file for the specified MAC address
    time_freq = parse_json(sys.argv[1], sys.argv[4])
    # Write statistics into file
    save_to_file(sys.argv[3], time_freq, sys.argv[2])
    print "====================================================================="
    #for time in time_freq.keys():
    for key in sorted(time_freq):
        print key, " => ", time_freq[key]
    print "====================================================================="


# Convert JSON file containing DNS traffic to a map in which a hostname points to its set of associated IPs.
def parse_json(file_path, mac_address):
    """ Show summary of statistics of PCAP file
        Args:
            file_path: path of the read file
            mac_address: MAC address of a device to analyze
    """
    # Maps timestamps to frequencies of packets
    time_freq = dict()
    with open(file_path) as jf:
        # Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)
        # Loop through json objects in data
        # Each entry is a pcap entry (request/response (packet) and associated metadata)
        for p in data:
            # p is a JSON object, not an index
            layers = p[JSON_KEY_SOURCE][JSON_KEY_LAYERS]
            # Get timestamp
            frame = layers.get(JSON_KEY_FRAME, None)
            date_time = frame.get(JSON_KEY_FRAME_TIME, None)
            # Get into the Ethernet address part
            eth = layers.get(JSON_KEY_ETH, None)
            # Skip any non DNS traffic
            if eth is None:
                print "[ WARNING: Packet has no ethernet address! ]"
                continue
            # Get source and destination MAC addresses
            src = eth.get(JSON_KEY_ETH_SRC, None)
            dst = eth.get(JSON_KEY_ETH_DST, None)
            # Get just the time part
            date_time_obj = parser.parse(date_time)
            # Remove the microsecond part
            time_str = str(date_time_obj.time())[:8]
            print str(time_str) + " - src:" + str(src) + " - dest:" + str(dst)
            # Get and count the traffic for the specified MAC address
            if src == mac_address or dst == mac_address:
                # Check if timestamp already exists in the map
                # If yes, then just increment the frequency value...
                if time_str in time_freq:
                    time_freq[time_str] = time_freq[time_str] + 1
                else: # If not, then put the value one there
                    time_freq[time_str] = 1
    return time_freq

if __name__ == '__main__':
    main()

