#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) and analyze
the traffic frequency of a certain device at a certain time.
"""

import sys
import json
import numpy as np
from collections import defaultdict
from dateutil import parser
from datetime import datetime
from decimal import *

JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"

JSON_KEY_ETH = "eth"
JSON_KEY_ETH_DST = "eth.dst"
JSON_KEY_ETH_SRC = "eth.src"
JSON_KEY_FRAME = "frame"
JSON_KEY_FRAME_TIME = "frame.time"
JSON_KEY_FRAME_LEN = "frame.len"
TABLE_HEADER_X = "Timestamp (hh:mm:ss)"
TABLE_HEADER_Y = "Packet frequency"
INCOMING_APPENDIX = "_incoming"
OUTGOING_APPENDIX = "_outgoing"
FILE_APPENDIX = ".dat"

# Use this constant as a flag
WINDOW_SIZE = 5
USE_MOVING_AVERAGE = False
USE_BINNING = True
# Range = 6, i.e. 3 to left and 3 to right (in seconds)
#TOTAL_RANGE = 60 # TOTAL_RANGE = 2 x RANGE
#RANGE = 30
TOTAL_RANGE = 20 # TOTAL_RANGE = 2 x RANGE
RANGE = 10

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

def hms_to_seconds(t):
    """ Calculate hms to seconds
        Args:
            t = time in hh:mm:ss string
        Adapted from:
            https://stackoverflow.com/questions/10742296/python-time-conversion-hms-to-seconds
    """
    h, m, s = [int(i) for i in t.split(':')]
    return 3600*h + 60*m + s
    
def seconds_to_hms(t):
    """ Calculate seconds to hms
        Args:
            t = time in seconds
        Adapted from:
            https://stackoverflow.com/questions/10742296/python-time-conversion-hms-to-seconds
    """
    h = t / 3600
    m = (t - (h * 3600)) / 60
    s = t - (h * 3600) - (m * 60)
    hh = str(h)
    if len(hh) is 1:
        hh = "0" + hh
    mm = str(m)
    if len(mm) is 1:
        mm = "0" + mm
    ss = str(s) 
    if len(ss) is 1:
        ss = "0" + ss
    return hh + ":" + mm + ":" + ss
    
def include_timestamps_zero_packets(timelen):
    """ Include every second that has zero packets (no packets/transmission)
        Args:
            timelen = dictionary that maps timestamps to packet length
    """
    sortedkeylist = []
    for key in sorted(timelen):
        sortedkeylist.append(key)
    first = sortedkeylist[0]
    last = sortedkeylist[len(sortedkeylist)-1]
    # Calculate the number of seconds between first and last packets
    first_seconds = hms_to_seconds(first)
    last_seconds = hms_to_seconds(last)
    seconds = last_seconds - first_seconds
    # Start counting and filling in timestamps with zero packets
    counter = 0
    while counter < seconds:
        timestamp = seconds_to_hms(first_seconds + counter)
        if timestamp not in timelen:
            timelen[timestamp] = 0
        counter += 1
    return timelen
    

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

    elif USE_BINNING:
        sortedlist = []
        # Iterate over dictionary and write (key, value) pairs
        ind = 0
        first = 0
        last = 0
        for key in sorted(dictionary):
            sortedlist.append(key)
            print "Key: ", key, " - Value: ", dictionary[key], " - Ind: ", ind
            ind += 1
        first = hms_to_seconds(sortedlist[0])
        #print "First: ", key
        last = hms_to_seconds(sortedlist[ind-1])
        #print "Last: ", key
        resultdict = dict()
        # Put new binning keys
        time_ind = first
        ind = 0
        while time_ind < last:
            # Initialize with the first key in the list
            curr_key = sortedlist[ind]
            curr_key_secs = hms_to_seconds(curr_key)
            # Initialize with 0 first
            resultdict[time_ind] = 0
            # Check if this is still within RANGE - bin the value if it is
            while time_ind - RANGE <= curr_key_secs and curr_key_secs <= time_ind + RANGE:
                resultdict[time_ind] += dictionary[curr_key]
                print "Time index: ", seconds_to_hms(time_ind), " Value: ", resultdict[time_ind]
                ind += 1
                if ind > len(dictionary)-1:
                    break
                # Initialize with the key in the list
                curr_key = sortedlist[ind]
                curr_key_secs = hms_to_seconds(curr_key)
            # Increment time index
            time_ind += TOTAL_RANGE
        # Now write to file after binning
        for key in sorted(resultdict):
            # Space separated
            f.write(seconds_to_hms(key) + " " + str(resultdict[key]) + "\n")
            #print seconds_to_hms(key) + " " + str(resultdict[key])

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
    timelen_incoming = parse_json(sys.argv[1], sys.argv[4], True)
    timelen_incoming = include_timestamps_zero_packets(timelen_incoming)
    timelen_outgoing = parse_json(sys.argv[1], sys.argv[4], False)
    timelen_outgoing = include_timestamps_zero_packets(timelen_outgoing)
    # Write statistics into file
    print "====================================================================="
    print "==> Analyzing incoming traffic ..."
    save_to_file(sys.argv[3] + INCOMING_APPENDIX, timelen_incoming, sys.argv[2] + INCOMING_APPENDIX + FILE_APPENDIX)
    print "====================================================================="
    print "==> Analyzing outgoing traffic ..."
    save_to_file(sys.argv[3] + OUTGOING_APPENDIX, timelen_outgoing, sys.argv[2] + OUTGOING_APPENDIX + FILE_APPENDIX)
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
    # Maps timestamps to lengths of packets
    timelen = dict()
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
            # Get frame length
            length = frame.get(JSON_KEY_FRAME_LEN, None)
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
            print str(timestr) + " - src:" + str(src) + " - dest:" + str(dst) + " - length: ", length
            # Get and count the traffic for the specified MAC address
            if incomingoutgoing:           
                if dst == macaddress:
                    # Check if timestamp already exists in the map
                    # If yes, then just increment the frequency value...
                    if timestr in timelen:
                        timelen[timestr] = timelen[timestr] + int(length)
                    else: # If not, then put the value one there
                        timelen[timestr] = int(length)
            else:
                if src == macaddress:
                    # Check if timestamp already exists in the map
                    # If yes, then just increment the frequency value...
                    if timestr in timelen:
                        timelen[timestr] = timelen[timestr] + int(length)
                    else: # If not, then put the value one there
                        timelen[timestr] = int(length)

    return timelen


if __name__ == '__main__':
    main()

