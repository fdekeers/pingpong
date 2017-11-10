#!/usr/bin/python

"""
Script that constructs a graph in which hosts are nodes.
An edge between two hosts indicate that the hosts communicate.
Hosts are labeled and identified by their IPs.
The graph is written to a file in Graph Exchange XML format for later import and visual inspection in Gephi.

The input to this script is the JSON output by extract_from_tshark.py by Anastasia Shuba.

This script is a simplification of Milad Asgari's parser_data_to_gephi.py script.
It serves as a baseline for future scripts that want to include more information in the graph.
"""

import socket
import json
import tldextract
import networkx as nx
import sys
import csv
import re
from decimal import *

import parser.parse_dns

# List of devices
DEVICE_MAC_LIST = "devicelist.dat"
EXCLUSION_MAC_LIST = "exclusion.dat"
COLUMN_MAC = "MAC_address"
COLUMN_DEVICE_NAME = "device_name"
# Fields
JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"
JSON_KEY_FRAME = "frame"
JSON_KEY_FRAME_PROTOCOLS = "frame.protocols"
JSON_KEY_FRAME_TIME_EPOCH = "frame.time_epoch"
JSON_KEY_ETH = "eth"
JSON_KEY_ETH_SRC = "eth.src"
JSON_KEY_ETH_DST = "eth.dst"
JSON_KEY_IP = "ip"
JSON_KEY_IP_SRC = "ip.src"
JSON_KEY_IP_DST = "ip.dst"
# Checked protocols
JSON_KEY_UDP = "udp"
JSON_KEY_TCP = "tcp"
# List of checked protocols
listchkprot = [ "arp",
                "bootp",
                "dhcpv6",
                "dns",
                "llmnr",
                "mdns",
                "ssdp" ]


def create_device_list(dev_list_file):
    """ Create list for smart home devices from a CSV file
        Args:
            dev_list_file: CSV file path that contains list of device MAC addresses
    """
    # Open the device MAC list file
    with open(dev_list_file) as csvfile:
        mac_list = csv.DictReader(csvfile, (COLUMN_MAC, COLUMN_DEVICE_NAME))
        crude_list = list()
        for item in mac_list:
            crude_list.append(item)
    # Create key-value dictionary
    dev_list = dict()
    for item in crude_list:
        dev_list[item[COLUMN_MAC]] = item[COLUMN_DEVICE_NAME]
        #print item["MAC_address"] + " => " + item["device_name"]
    #for key, value in devlist.iteritems():
    #    print key + " => " + value

    return dev_list


def parse_json(file_path):

    # Create a smart home device list
    dev_list = create_device_list(DEVICE_MAC_LIST)
    # Create an exclusion list
    exc_list = create_device_list(EXCLUSION_MAC_LIST)

    # First parse the file once, constructing a map that contains information about individual devices' DNS resolutions.
    device_dns_mappings = parser.parse_dns.parse_json_dns(file_path) # "./json/eth1.dump.json"
    
    # Init empty graph
    G = nx.DiGraph()
    # Mapping from node to a set of protocols
    edge_to_prot = dict()

    # Parse file again, this time constructing a graph of device<->server and device<->device communication.
    with open(file_path) as jf:
        # Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)

        # Loop through json objects (packets) in data
        for p in data:
            # p is a JSON object, not an index
            # Drill down to object containing data from the different layers
            layers = p[JSON_KEY_SOURCE][JSON_KEY_LAYERS]

            iscontinue = False
            for prot in listchkprot:
                if prot in layers:
                    iscontinue = True
            if iscontinue:
                continue            

            # Skip any non udp/non tcp traffic
            if JSON_KEY_UDP not in layers and JSON_KEY_TCP not in layers:
                continue

            # Fetch source and destination MACs
            eth = layers.get(JSON_KEY_ETH, None)
            if eth is None:
                print "[ WARNING: eth data not found ]"
                continue
            eth_src = eth.get(JSON_KEY_ETH_SRC, None)
            eth_dst = eth.get(JSON_KEY_ETH_DST, None)
            # Exclude devices in the exclusion list
            if eth_src in exc_list:
                print "[ WARNING: Source ", eth_src, " is excluded from graph! ]"
                continue
            if eth_dst in exc_list:
                print "[ WARNING: Destination ", eth_dst, " is excluded from graph! ]"
                continue

            # Fetch timestamp of packet (router's timestamp)
            timestamp = Decimal(layers[JSON_KEY_FRAME][JSON_KEY_FRAME_TIME_EPOCH])
            # Get the protocol and strip just the name of it
            long_protocol = layers[JSON_KEY_FRAME][JSON_KEY_FRAME_PROTOCOLS]
            # Split once starting from the end of the string and get it
            #protocol = long_protocol.rsplit(':', 1)[1]
            split_protocol = long_protocol.split(':')
            protocol = None
            if len(split_protocol) < 5:
                last_index = len(split_protocol) - 1
                protocol = split_protocol[last_index]
            else:
                protocol = split_protocol[3] + ":" + split_protocol[4]
            print "timestamp: ", timestamp, " - new protocol added: ", protocol, "\n"

            # Store protocol into the set (source)
            protocols = None
            # Key to search for protocol list in the dictionary is
            #   <src-mac-address>-<dst-mac_address>
            protocol_key = eth_src + "-" + eth_dst
            if protocol_key not in edge_to_prot:
                edge_to_prot[protocol_key] = set()
            protocols = edge_to_prot[protocol_key]
            protocols.add(protocol)
            protocols_str = ', '.join(protocols)
            print "protocols: ", protocols_str, "\n"
            # And source and destination IPs
            ip_src = layers[JSON_KEY_IP][JSON_KEY_IP_SRC]
            ip_dst = layers[JSON_KEY_IP][JSON_KEY_IP_DST]

            # Categorize source and destination IP addresses: local vs. non-local
            ipre = re.compile(r'\b192.168.[0-9.]+')
            src_is_local = ipre.search(ip_src) 
            dst_is_local = ipre.search(ip_dst)
            print "ip.src =", ip_src, "ip.dst =", ip_dst, "\n"
            
            src_node = None
            dst_node = None
            if src_is_local:
                G.add_node(eth_src, Name=dev_list[eth_src])
                src_node = eth_src
            else:
                hostname = None
                # Check first if the key (eth_dst) exists in the dictionary
                if eth_dst in device_dns_mappings:
                    # If the source is not local, then it's inbound traffic, and hence the eth_dst is the MAC of the IoT device.
                    hostname = device_dns_mappings[eth_dst].hostname_for_ip_at_time(ip_src, timestamp)                   
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_src
                G.add_node(hostname)
                src_node = hostname

            if dst_is_local:
                G.add_node(eth_dst, Name=dev_list[eth_dst])
                dst_node = eth_dst
            else:
                hostname = None
                # Check first if the key (eth_dst) exists in the dictionary
                if eth_src in device_dns_mappings:
                    # If the destination is not local, then it's outbound traffic, and hence the eth_src is the MAC of the IoT device.
                    hostname = device_dns_mappings[eth_src].hostname_for_ip_at_time(ip_dst, timestamp)
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_dst
                G.add_node(hostname)
                dst_node = hostname
            G.add_edge(src_node, dst_node, Protocol=protocols_str)

    # Print DNS mapping for reference
	for mac in device_dns_mappings:
		ddm = device_dns_mappings[mac]
		ddm.print_mappings()
    
    return G


# ------------------------------------------------------
# Not currently used.
# Might be useful later on if we wish to resolve IPs.
def get_domain(host):
    ext_result = tldextract.extract(str(host))
    # Be consistent with ReCon and keep suffix
    domain = ext_result.domain + "." + ext_result.suffix
    return domain

def is_IP(addr):
    try:
        socket.inet_aton(addr)
        return True
    except socket.error:
        return False
# ------------------------------------------------------


if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "Usage:", sys.argv[0], "input_file output_file"
        print "outfile_file should end in .gexf"
        sys.exit(0)
    # Input file: Path to JSON file generated from tshark JSON output using Anastasia's script (extract_from_tshark.py).
    input_file = sys.argv[1]
    print "[ input_file  =", input_file, "]"
    # Output file: Path to file where the Gephi XML should be written.
    output_file = sys.argv[2]
    print "[ output_file =", output_file, "]"
    # Construct graph from JSON
    G = parse_json(input_file)
    # Write Graph in Graph Exchange XML format
    nx.write_gexf(G, output_file)
