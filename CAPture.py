#!/usr/local/bin/python2.7

""" -----------------------------------------------------------------------------
    CAPture - a pcap file analyzer and report generator
    (c) 2017 - Rahmadi Trimananda
    University of California, Irvine - Programming Language and Systems
    -----------------------------------------------------------------------------
    Credits to tutorial: https://dpkt.readthedocs.io/en/latest/
    -----------------------------------------------------------------------------
""" 

import datetime
import dpkt
from dpkt.compat import compat_ord

import socket
import sys

""" -----------------------------------------------------------------------------
    Global variable declarations
    -----------------------------------------------------------------------------
"""
# Command line arguments
INPUT = "-i"
OUTPUT = "-o"
POINT_TO_MANY = "-pm"
VERBOSE = "-v"


def mac_addr(address):
    # Courtesy of: https://dpkt.readthedocs.io/en/latest/
    """ Convert a MAC address to a readable/printable string
        Args:
            address (str): a MAC address in hex form (e.g. '\x01\x02\x03\x04\x05\x06')
        Returns:
            str: Printable/readable MAC address
    """
    return ':'.join('%02x' % compat_ord(b) for b in address)


def inet_to_str(inet):
    # Courtesy of: https://dpkt.readthedocs.io/en/latest/
    """ Convert inet object to a string
        Args:
            inet (inet struct): inet network address
        Returns:
            str: Printable/readable IP address
    """
    # First try ipv4 and then ipv6
    try:
        return socket.inet_ntop(socket.AF_INET, inet)
    except ValueError:
        return socket.inet_ntop(socket.AF_INET6, inet)


def show_usage():
    """ Show usage of this Python script 
    """
    print "Usage: python CAPture.py [ -i <file-name>.pcap ] [ -o <file-name>.pcap ] [ -pm ] [ -v ]"
    print
    print "[ -o ]  = output file"
    print "[ -pm ] = point-to-many analysis"
    print "[ -v ]  = verbose output"
    print "By default, this script does simple statistical analysis of IP, TCP, and UDP packets."
    print "(c) 2017 - University of California, Irvine - Programming Language and Systems"


def show_progress(verbose, counter):
    """ Show packet processing progress
        Args:
            verbose: verbose output (True/False)
            counter: counter of all packets
    """
    if verbose:
        print "Processing packet number: ", counter
    else:
        if counter % 100000 == 0:
            print "Processing %s packets..." % counter


def show_summary(counter, ip_counter, tcp_counter, udp_counter):
    """ Show summary of statistics of PCAP file
        Args:
            counter: counter of all packets
            ip_counter: counter of all IP packets
            tcp_counter: counter of all TCP packets
            udp_counter: counter of all UDP packets
    """
    print
    print "Total number of packets in the pcap file: ", counter
    print "Total number of ip packets: ", ip_counter
    print "Total number of tcp packets: ", tcp_counter
    print "Total number of udp packets: ", udp_counter
    print


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
    f.write("\n\n" + str(tbl_header) + "\n");
    # Iterate over dictionary and write (key, value) pairs
    for key, value in dictionary.iteritems():
        f.write(str(key) + ", " + str(value) + "\n")

    f.close()
    print "Writing output to file: ", filename_out


def statistical_analysis(verbose, pcap, counter, ip_counter, tcp_counter, udp_counter):
    """ This is the default analysis of packet statistics (generic)
        Args:
            verbose: verbose output (True/False)
            pcap: object that handles PCAP file content
            counter: counter of all packets
            ip_counter: counter of all IP packets
            tcp_counter: counter of all TCP packets
            udp_counter: counter of all UDP packets
    """
    for time_stamp, packet in pcap:

        counter += 1
        eth = dpkt.ethernet.Ethernet(packet)

        if verbose:
            # Print out the timestamp in UTC
            print "Timestamp: ", str(datetime.datetime.utcfromtimestamp(time_stamp))
            # Print out the MAC addresses
            print "Ethernet frame: ", mac_addr(eth.src), mac_addr(eth.dst), eth.data.__class__.__name__

        # Process only IP data
        if not isinstance(eth.data, dpkt.ip.IP):

            is_ip = False
            if verbose:
                print "Non IP packet type not analyzed... skipping..."
        else:
            is_ip = True

        if is_ip:
            ip = eth.data
            ip_counter += 1

            # Pull out fragment information (flags and offset all packed into off field, so use bitmasks)
            do_not_fragment = bool(ip.off & dpkt.ip.IP_DF)
            more_fragments = bool(ip.off & dpkt.ip.IP_MF)
            fragment_offset = ip.off & dpkt.ip.IP_OFFMASK

            if verbose:
                # Print out the complete IP information
                print "IP: %s -> %s   (len=%d ttl=%d DF=%d MF=%d offset=%d)\n" % \
                    (inet_to_str(ip.src), inet_to_str(ip.dst), ip.len, ip.ttl, do_not_fragment, 
                     more_fragments, fragment_offset)

            # Count TCP packets
            if ip.p == dpkt.ip.IP_PROTO_TCP:  
                tcp_counter += 1

            # Count UDP packets
            if ip.p == dpkt.ip.IP_PROTO_UDP:
                udp_counter += 1

        show_progress(verbose, counter)

    # Print general statistics
    show_summary(counter, ip_counter, tcp_counter, udp_counter)


def point_to_many_analysis(filename_out, dev_add, verbose, pcap, counter, ip_counter, 
        tcp_counter, udp_counter):
    """ This analysis presents how 1 device (MAC address or IP address) communicates
        to every other device in the analyzed PCAP file.
        Args:
            dev_add: device address (MAC or IP address)
            verbose: verbose output (True/False)
            pcap: object that handles PCAP file content
            counter: counter of all packets
            ip_counter: counter of all IP packets
            tcp_counter: counter of all TCP packets
            udp_counter: counter of all UDP packets
    """
    # Dictionary that preserves the mapping between destination address to frequency
    mac2freq = dict()
    ip2freq = dict()
    for time_stamp, packet in pcap:

        counter += 1
        eth = dpkt.ethernet.Ethernet(packet)

        # Save the timestamp and MAC addresses
        tstamp = str(datetime.datetime.utcfromtimestamp(time_stamp))
        mac_src = mac_addr(eth.src)
        mac_dst = mac_addr(eth.dst)

        # Process only IP data
        if not isinstance(eth.data, dpkt.ip.IP):

            is_ip = False
            if verbose:
                print "Non IP packet type not analyzed... skipping..."
                print 
        else:
            is_ip = True

        if is_ip:
            ip = eth.data
            ip_counter += 1

            # Pull out fragment information (flags and offset all packed into off field, so use bitmasks)
            do_not_fragment = bool(ip.off & dpkt.ip.IP_DF)
            more_fragments = bool(ip.off & dpkt.ip.IP_MF)
            fragment_offset = ip.off & dpkt.ip.IP_OFFMASK
            
            # Save IP addresses
            ip_src = inet_to_str(ip.src)
            ip_dst = inet_to_str(ip.dst)

            if verbose:
                # Print out the complete IP information
                print "IP: %s -> %s   (len=%d ttl=%d DF=%d MF=%d offset=%d)\n" % \
                    (ip_src, ip_dst, ip.len, ip.ttl, do_not_fragment, 
                     more_fragments, fragment_offset)

            # Categorize packets based on source device address
            # Save the destination device addresses (point-to-many)
            if dev_add == ip_src:
                if ip_dst in ip2freq:
                    freq = ip2freq[ip_dst]
                    ip2freq[ip_dst] = freq + 1
                else:
                    ip2freq[ip_dst] = 1

            if dev_add == mac_src:
                if mac_dst in ip2freq:
                    freq = mac2freq[mac_dst]
                    mac2freq[mac_dst] = freq + 1
                else:
                    mac2freq[mac_dst] = 1

            # Count TCP packets
            if ip.p == dpkt.ip.IP_PROTO_TCP:  
                tcp_counter += 1

            # Count UDP packets
            if ip.p == dpkt.ip.IP_PROTO_UDP:
                udp_counter += 1

        show_progress(verbose, counter)

    # Print general statistics
    show_summary(counter, ip_counter, tcp_counter, udp_counter)
    # Save results into file if filename_out is not empty
    if not filename_out == "":
        print "Saving results into file: ", filename_out
        ip_tbl_header = "Point-to-many Analysis - IP destinations for " + dev_add
        mac_tbl_header = "Point-to-many Analysis - MAC destinations for " + dev_add
        save_to_file(ip_tbl_header, ip2freq, filename_out)
        save_to_file(mac_tbl_header, mac2freq, filename_out)
    else:
        print "Output file name is not specified... exitting now!"


def parse_cli_args(argv):
    """ Parse command line arguments and store them in a dictionary
        Args:
            argv: list of command line arguments and their values
        Returns:
            str: dictionary that maps arguments to their values
    """
    options = dict()
    # First argument is "CAPture.py", so skip it
    argv = argv[1:]
    # Loop and collect arguments and their values
    while argv:
        print "Examining argument: ", argv[0]
        # Check the first character of each argv list
        # If it is a '-' then it is a command line argument
        if argv[0][0] == '-':
            if argv[0] == VERBOSE:
                # We don't have value for the argument VERBOSE
                options[argv[0]] = argv[0]
                # Remove one command line argument and its value
                argv = argv[1:]
            else:
                options[argv[0]] = argv[1]
                # Remove one command line argument and its value
                argv = argv[2:]

    return options


""" -----------------------------------------------------------------------------
    Main Running Methods
    -----------------------------------------------------------------------------
""" 
def main():
    # Variable declarations
    global CAP_EXTENSION
    global PCAP_EXTENSION
    global VERBOSE
    global POINT_TO_MANY

    # Counters
    counter = 0
    ip_counter = 0
    tcp_counter = 0
    udp_counter = 0
    # Booleans as flags
    verbose = False
    is_ip = True
    is_statistical_analysis = True
    is_point_to_many_analysis = False
    # Names
    filename_in = ""
    filename_out = ""
    dev_add = ""

    # Welcome message
    print
    print "Welcome to CAPture version 1.0 - A PCAP file instant analyzer!"

    # Get file name from user input
    # Show usage if file name is not specified (only accept 1 file name for now)
    if len(sys.argv) < 2:
        show_usage()
        print
        return

    # Check and process sys.argv
    options = parse_cli_args(sys.argv)
    for key, value in options.iteritems():
        # Process "-i" - input PCAP file
        if key == INPUT:
            filename_in = value
        elif key == OUTPUT:
            filename_out = value
        elif key == VERBOSE:
            verbose = True
        elif key == POINT_TO_MANY:
            is_statistical_analysis = False
            is_point_to_many_analysis = True
            dev_add = value

    # Show manual again if input is not correct
    if filename_in == "":
        print "File name is empty!"
        print
        show_usage()
        print
        return

    # dev_add is needed for these analyses
    if is_point_to_many_analysis and dev_add == "":
        print "Device address is empty!"
        print
        show_usage()
        print
        return

    # One PCAP file name is specified - now analyze!
    print "Analyzing PCAP file: ", filename_in

    # Opening and analyzing PCAP file
    f = open(filename_in,'rb')
    pcap = dpkt.pcap.Reader(f)

    # Choose from the existing options
    if is_statistical_analysis:
        statistical_analysis(verbose, pcap, counter, ip_counter, tcp_counter, udp_counter)
    elif is_point_to_many_analysis:
        point_to_many_analysis(filename_out, dev_add, verbose, pcap, counter, ip_counter, 
                               tcp_counter, udp_counter)


if __name__ == "__main__":
    # call main function since this is being run as the start
    main()


