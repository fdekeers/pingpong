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

DEVICE_MAC_LIST = "devicelist.dat"
COLUMN_MAC = "MAC_address"
COLUMN_DEVICE_NAME = "device_name"


JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"
JSON_KEY_FRAME = "frame"
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
listchkprot = [ "bootp",
                "dhcpv6",
                "dns",
                "llmnr",
                "mdns",
                "ssdp" ]


def parse_json(file_path):

    # Open the device MAC list file
    with open(DEVICE_MAC_LIST) as csvfile:
        maclist = csv.DictReader(csvfile, (COLUMN_MAC, COLUMN_DEVICE_NAME))
        crudelist = list()
        for item in maclist:
            crudelist.append(item)
            #print(item)
    # Create key-value dictionary
    devlist = dict()
    for item in crudelist:
        devlist[item[COLUMN_MAC]] = item[COLUMN_DEVICE_NAME]
        #print item["MAC_address"] + " => " + item["device_name"]
    #for key, value in devlist.iteritems():
    #    print key + " => " + value

    # First parse the file once, constructing a map that contains information about individual devices' DNS resolutions.
    device_dns_mappings = parser.parse_dns.parse_json_dns(file_path) # "./json/eth1.dump.json"
    
    # Init empty graph
    G = nx.DiGraph() 
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

            # Fetch timestamp of packet (router's timestamp)
            packet_timestamp = Decimal(layers[JSON_KEY_FRAME][JSON_KEY_FRAME_TIME_EPOCH])
            print "timestamp", packet_timestamp
            # Fetch source and destination MACs
            eth = layers.get(JSON_KEY_ETH, None)
            if eth is None:
                print "[ WARNING: eth data not found ]"
                continue
            eth_src = eth.get(JSON_KEY_ETH_SRC, None)
            eth_dst = eth.get(JSON_KEY_ETH_DST, None)
            # And source and destination IPs
            ip_src = layers[JSON_KEY_IP][JSON_KEY_IP_SRC]
            ip_dst = layers[JSON_KEY_IP][JSON_KEY_IP_DST]

            # Categorize source and destination IP addresses: local vs. non-local
            ipre = re.compile(r'\b192.168.[0-9.]+')
            src_is_local = ipre.search(ip_src) 
            dst_is_local = ipre.search(ip_dst)
            print "ip.src =", ip_src, "ip.dst =", ip_dst

            src_node = None
            dst_node = None
            if src_is_local:
                G.add_node(eth_src, Name=devlist[eth_src])
                src_node = eth_src
            else:
                hostname = None
                # Check first if the key (eth_dst) exists in the dictionary
                if eth_dst in device_dns_mappings:
                    # If the source is not local, then it's inbound traffic, and hence the eth_dst is the MAC of the IoT device.
                    hostname = device_dns_mappings[eth_dst].hostname_for_ip_at_time(ip_src, packet_timestamp)                   
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_src
                G.add_node(hostname)
                src_node = hostname
            if dst_is_local:
                G.add_node(eth_dst, Name=devlist[eth_src])
                dst_node = eth_dst
            else:
                hostname = None
                # Check first if the key (eth_dst) exists in the dictionary
                if eth_src in device_dns_mappings:
                    # If the destination is not local, then it's outbound traffic, and hence the eth_src is the MAC of the IoT device.
                    hostname = device_dns_mappings[eth_src].hostname_for_ip_at_time(ip_dst, packet_timestamp)
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_dst
                G.add_node(hostname)
                dst_node = hostname
            G.add_edge(src_node, dst_node)

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
