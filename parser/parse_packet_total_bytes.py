#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) and analyze
the traffic total bytes of a certain device at a certain time.
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
JSON_KEY_FRAME_TIME = "frame.time"
JSON_KEY_FRAME_LENGTH = "frame.len"
TABLE_HEADER_X = "Timestamp (hh:mm:ss)"
TABLE_HEADER_Y = "Total bytes (bytes)"
INCOMING_APPENDIX = "_incoming"
OUTGOING_APPENDIX = "_outgoing"
FILE_APPENDIX = ".dat"

# Use this constant as a flag
WINDOW_SIZE = 5
USE_MOVING_AVERAGE = False


def moving_average(array, window=3):
    """ Calculate moving average
        Args:
            array: array of numbers
            window: window of moving average (default = 3)
        Adapted from: 
            https://stackoverflow.com/questions/14313510/how-to-calculate-moving-average-using-numpy
    """
    # Check if window > len(array)
    if window > len(array):
        window = len(array)
    # Calculate cumulative sum of each array element
    retarr = np.cumsum(array, dtype=float)
    # Adjust cumulative sum of each array element
    #   based on window size
    retarr[window:] = retarr[window:] - retarr[:-window]
    # Pad the first array elements with zeroes
    retarr[:window - 1] = np.zeros(window - 1)
    # Calculate moving average starting from the element
    #   at window size, e.g. element 4 for window=5
    retarr[window - 1:] = retarr[window - 1:] / window
    return retarr


def save_to_file(tblheader, dictionary, filenameout):
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
    if not dictionary:
        f.write("0 0")
        f.close()
        print "Writing zeroes to file: ", filenameout
        return

    if USE_MOVING_AVERAGE:
        # Use moving average if this flag is true
        sortedarr = []
        for key in sorted(dictionary):
            sortedarr.append(dictionary[key])
        valarr = moving_average(sortedarr, WINDOW_SIZE)
        #print vallist
        # Iterate over dictionary and write (key, value) pairs
        ind = 0
        for key in sorted(dictionary):
            # Space separated
            f.write(str(key) + " " + str(valarr[ind]) + "\n")
            ind += 1
    else:
        # Iterate over dictionary and write (key, value) pairs
        for key in sorted(dictionary):
            # Space separated
            f.write(str(key) + " " + str(dictionary[key]) + "\n")
    f.close()
    print "Writing output to file: ", filenameout


def main():
    """ Main function
    """
    if len(sys.argv) < 5:
        print "Usage: python", sys.argv[0], "<input_file> <output_file> <device_name> <mac_address>"
        return
    # Parse the file for the specified MAC address
    timefreq_incoming = parse_json(sys.argv[1], sys.argv[4], True)
    timefreq_outgoing = parse_json(sys.argv[1], sys.argv[4], False)
    # Write statistics into file
    print "====================================================================="
    print "==> Analyzing incoming traffic ..."
    save_to_file(sys.argv[3] + INCOMING_APPENDIX, timefreq_incoming, sys.argv[2] + INCOMING_APPENDIX + FILE_APPENDIX)
    print "====================================================================="
    print "==> Analyzing outgoing traffic ..."
    save_to_file(sys.argv[3] + OUTGOING_APPENDIX, timefreq_outgoing, sys.argv[2] + OUTGOING_APPENDIX + FILE_APPENDIX)
    print "====================================================================="
    #for time in time_freq.keys():
    #for key in sorted(time_freq):
    #    print key, " => ", time_freq[key]
    #print "====================================================================="


# Convert JSON file containing DNS traffic to a map in which a hostname points to its set of associated IPs.
def parse_json(filepath, macaddress, incomingoutgoing):
    """ Show summary of statistics of PCAP file
        Args:
            filepath: path of the read file
            macaddress: MAC address of a device to analyze
            incomingoutgoing: boolean to define whether we collect incoming or outgoing traffic
                              True = incoming, False = outgoing
    """
    # Maps timestamps to frequencies of packets
    packetbytes = dict()
    with open(filepath) as jf:
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
            datetime = frame.get(JSON_KEY_FRAME_TIME, None)
            bytes = frame.get(JSON_KEY_FRAME_LENGTH, None)
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
            datetimeobj = parser.parse(datetime)
            # Remove the microsecond part
            timestr = str(datetimeobj.time())[:8]
            print str(timestr) + " - src:" + str(src) + " - dest:" + str(dst)
            # Get and count the traffic for the specified MAC address
            if incomingoutgoing:           
                if dst == macaddress:
                    # Check if timestamp already exists in the map
                    # If yes, then just accumulate the value
                    if timestr in packetbytes:
                        packetbytes[timestr] = packetbytes[timestr] + Decimal(bytes)
                    else: # If not, then put the value there
                        packetbytes[timestr] = Decimal(bytes)
            else:
                if src == macaddress:
                    # Check if timestamp already exists in the map
                    # If yes, then just accumulate the value
                    if timestr in packetbytes:
                        packetbytes[timestr] = packetbytes[timestr] + Decimal(bytes)
                    else: # If not, then put the value there
                        packetbytes[timestr] = Decimal(bytes)

    return packetbytes


if __name__ == '__main__':
    main()

