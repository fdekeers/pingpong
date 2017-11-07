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
from decimal import *

import parser.parse_dns

DEVICE_MAC_LIST = "devicelist.dat"
COLUMN_MAC = "MAC_address"
COLUMN_DEVICE_NAME = "device_name"

JSON_KEY_ETH_SRC = "eth.src"
JSON_KEY_ETH_DST = "eth.dst"

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

    device_dns_mappings = parser.parse_dns.parse_json_dns("./json/dns.json")

    # Init empty graph
    G = nx.DiGraph() 
    with open(file_path) as jf:
        # Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)
        # Loop through json objects in data
        for k in data:
            # Fetch timestamp of packet
            packet_timestamp = Decimal(data[k]["ts"])
            # Fetch eth source and destination info
            eth_src = data[k][JSON_KEY_ETH_SRC]
            eth_dst = data[k][JSON_KEY_ETH_DST]
            # Traffic can be both outbound and inbound.
            # Determine which one of the two by looking up device MAC in DNS map.
            iot_device = None
            if eth_src in device_dns_mappings:
                iot_device = eth_src
            elif eth_dst in device_dns_mappings:
                iot_device = eth_dst
            else:
#                print "[ WARNING: DNS mapping not found for device with MAC", eth_src, "OR", eth_dst, "]"
                # This must be local communication between two IoT devices OR an IoT device talking to a hardcoded IP.
                # For now let's assume local communication.
                # Add a node for each device and an edge between them.
                G.add_node(eth_src, Name=devlist[eth_src])
                G.add_node(eth_dst, Name=devlist[eth_src])
                G.add_edge(eth_src, eth_dst)
                # TODO add regex check on src+dst IP to figure out if hardcoded server IP (e.g. check if one of the two are NOT a 192.168.x.y IP)
                continue
            # It is outbound traffic if iot_device matches src, otherwise it must be inbound traffic.
            outbound_traffic = iot_device == eth_src

            ''' Graph construction '''
            # No need to check if the Nodes and/or Edges we add already exist:
            # NetworkX won't add already existing nodes/edges (except in the case of a MultiGraph or MultiDiGraph (see NetworkX doc)).
            
            # Add a node for each host.
            # First add node for IoT device.
            G.add_node(iot_device, Name=devlist[eth_src])
            # Then add node for the server.
            # For this we need to distinguish between outbound and inbound traffic so that we look up the proper IP in our DNS map.
            # For outbound traffic, the server's IP is the destination IP.
            # For inbound traffic, the server's IP is the source IP.
            server_ip = data[k]["dst_ip"] if outbound_traffic else data[k]["src_ip"]
            hostname = device_dns_mappings[iot_device].hostname_for_ip_at_time(server_ip, packet_timestamp)
            if hostname is None:
                # TODO this can occur when two local devices communicate OR if IoT device has hardcoded server IP.
                # However, we only get here for the DNS that have not performed any DNS lookups
                # We should use a regex check early in the loop to see if it is two local devices communicating.
                # This way we would not have to consider these corner cases later on.
#                print "[ WARNING: no ip-hostname mapping found for ip", server_ip, " -- adding eth.src->eth.dst edge, but note that this may be incorrect if IoT device has hardcoded server IP ]"
                G.add_node(eth_src, Name=devlist[eth_src])
                G.add_node(eth_dst, Name=devlist[eth_src])
                G.add_edge(eth_src, eth_dst)
                continue
            G.add_node(hostname)
            # Connect the two nodes we just added.
            if outbound_traffic:
                G.add_edge(iot_device, hostname)
            else:
                G.add_edge(hostname, iot_device)
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
