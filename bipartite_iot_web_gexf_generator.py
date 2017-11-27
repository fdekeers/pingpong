#!/usr/bin/python

"""
Extension of base_gefx_generator.py.
This script constructs a bipartite graph with IoT devices on one side and Internet hosts on the other side.
As a result, this graph does NOT show inter IoT device communication.

The input to this script is the Wirshark's/tshark's JSON representation of a packet trace.

"""

import socket
import json
import tldextract
import networkx as nx

from networkx.algorithms import bipartite 

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
JSON_KEY_UDP = "udp"
JSON_KEY_TCP = "tcp"
JSON_KEY_MDNS = "mdns"
JSON_KEY_BOOTP = "bootp"
JSON_KEY_SSDP = "ssdp"
JSON_KEY_DHCPV6 = "dhcpv6"
JSON_KEY_LLMNR = "llmnr"


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

            # Skip all MDNS traffic.
            if JSON_KEY_MDNS in layers:
                continue

            # Skip all LLMNR traffic.
            if JSON_KEY_LLMNR in layers:
                continue

            # Skip all SSDP traffic - we don't care about disovery, only the actual communication.
            if JSON_KEY_SSDP in layers:
                continue

            # Skip all bootp traffic (DHCP related)
            if JSON_KEY_BOOTP in layers:
                continue

            # Skip DHCPv6 for now.
            if JSON_KEY_DHCPV6 in layers:
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

            print "ip.src =", ip_src, "ip.dst =", ip_dst
            src_is_local = ip_src.startswith("192.168.") 
            dst_is_local = ip_dst.startswith("192.168.")

            src_node = None
            dst_node = None

            # Values for the 'bipartite' attribute of a node when constructing the bipartite graph
            bipartite_iot = 0
            bipartite_web_server = 1

            # Skip inter-IoT device communication.
            if src_is_local and dst_is_local:
                continue

            if src_is_local:
                G.add_node(eth_src, Name=devlist[eth_src], bipartite=bipartite_iot)
                src_node = eth_src
            else:
                # If the source is not local, then it's inbound traffic, and hence the eth_dst is the MAC of the IoT device.
                hostname = device_dns_mappings[eth_dst].hostname_for_ip_at_time(ip_src, packet_timestamp)
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_src
                G.add_node(hostname, bipartite=bipartite_web_server)
                src_node = hostname
            if dst_is_local:
                G.add_node(eth_dst, Name=devlist[eth_src], bipartite=bipartite_iot)
                dst_node = eth_dst
            else:
                # If the destination is not local, then it's outbound traffic, and hence the eth_src is the MAC of the IoT device.
                hostname = device_dns_mappings[eth_src].hostname_for_ip_at_time(ip_dst, packet_timestamp)
                if hostname is None:
                    # Use IP if no hostname mapping
                    hostname = ip_dst
                G.add_node(hostname, bipartite=bipartite_web_server)
                dst_node = hostname
            G.add_edge(src_node, dst_node)
    return G

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "Usage:", sys.argv[0], "input_file output_file"
        print "outfile_file should end in .gexf"
        sys.exit(0)
    # Input file: Path to Wireshark/tshark JSON file.
    input_file = sys.argv[1]
    print "[ input_file  =", input_file, "]"
    # Output file: Path to file where the Gephi XML should be written.
    output_file = sys.argv[2]
    print "[ output_file =", output_file, "]"
    # Construct graph from JSON
    G = parse_json(input_file)
    # Write Graph in Graph Exchange XML format
    nx.write_gexf(G, output_file)
