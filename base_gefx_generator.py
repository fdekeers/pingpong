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
JSON_KEY_FRAME_LENGTH = "frame.len"
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

# Switch to generate graph that only shows local communication
ONLY_INCLUDE_LOCAL_COMMUNICATION = True


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


def traverse_and_merge_nodes(G, dev_list_file):
    """ Merge nodes that have similar properties, e.g. same protocols
        But, we only do this for leaves (outer nodes), and not for
        nodes that are in the middle/have many neighbors.
        The pre-condition is that the node:
        (1) only has one neighbor, and
        (2) not a smarthome device.
        then we compare the edges, whether they use the same protocols
        or not. If yes, then we collapse that node and we attach
        it to the very first node that uses that set of protocols.
        Args:
            G: a complete networkx graph
            dev_list_file: CSV file path that contains list of device MAC addresses
    """
    nodes = G.nodes()
    #print "Nodes: ", nodes
    node_to_merge = dict()
    # Create list of smarthome devices
    dev_list = create_device_list(DEVICE_MAC_LIST)
    # Traverse every node
    # Check that the node is not a smarthome device
    for node in nodes:
        neighbors = G[node] #G.neighbors(node)
        #print "Neighbors: ", neighbors, "\n"
        # Skip if the node is a smarthome device
        if node in dev_list:
            continue
        # Skip if the node has many neighbors (non-leaf) or no neighbor at all
        if len(neighbors) is not 1:
            continue
        #print "Node: ", node
        neighbor = neighbors.keys()[0] #neighbors[0]
        #print "Neighbor: ", neighbors
        protocols = G[node][neighbor]['Protocol']
        #print "Protocol: ", protocols
        # Store neighbor-protocol as key in dictionary
        neigh_proto = neighbor + "-" + protocols
        if neigh_proto not in node_to_merge:
            node_to_merge[neigh_proto] = node
        else:
        # Merge this node if there is already an entry
            # First delete
            G.remove_node(node)
            node_to_merge_with = node_to_merge[neigh_proto]
            merged_nodes = G.node[node_to_merge_with]['Merged']
            # Check if this is the first node
            if merged_nodes is '':
                merged_nodes = node
            else:
            # Put comma if there is already one or more nodes
                merged_nodes += ", " + node
            # Then attach as attribute
            G.node[node_to_merge_with]['Merged'] = merged_nodes

    return G


def place_in_graph(G, eth_src, eth_dst, device_dns_mappings, dev_list, layers, 
        edge_to_prot, edge_to_vol):
    """ Place nodes and edges on the graph
        Args:
            G: the complete graph
            eth_src: MAC address of source
            eth_dst: MAC address of destination
            device_dns_mappings: device to DNS mappings (data structure)
            dev_list: list of existing smarthome devices
            layers: layers of JSON file structure
            edge_to_prot: edge to protocols mappings
            edge_to_vol: edge to traffic volume mappings
    """
    # Get timestamp of packet (router's timestamp)
    timestamp = Decimal(layers[JSON_KEY_FRAME][JSON_KEY_FRAME_TIME_EPOCH])
    # Get packet length
    packet_len = Decimal(layers[JSON_KEY_FRAME][JSON_KEY_FRAME_LENGTH])
    # Get the protocol and strip just the name of it
    long_protocol = layers[JSON_KEY_FRAME][JSON_KEY_FRAME_PROTOCOLS]
    # Split once starting from the end of the string and get it
    split_protocol = long_protocol.split(':')
    protocol = None
    if len(split_protocol) < 5:
        last_index = len(split_protocol) - 1
        protocol = split_protocol[last_index]
    else:
        protocol = split_protocol[3] + ":" + split_protocol[4]
    #print "timestamp: ", timestamp, " - new protocol added: ", protocol, "\n"
    # Store protocol into the set (source)
    protocols = None
    # Key to search in the dictionary is <src-mac-address>-<dst-mac_address>
    dict_key = eth_src + "-" + eth_dst
    if dict_key not in edge_to_prot:
        edge_to_prot[dict_key] = set()
    protocols = edge_to_prot[dict_key]
    protocols.add(protocol)
    protocols_str = ', '.join(protocols)
    #print "protocols: ", protocols_str, "\n"
    # Check packet length and accumulate to get traffic volume
    if dict_key not in edge_to_vol:
        edge_to_vol[dict_key] = 0;
    edge_to_vol[dict_key] = edge_to_vol[dict_key] + packet_len
    volume = str(edge_to_vol[dict_key])
    # And source and destination IPs
    ip_src = layers[JSON_KEY_IP][JSON_KEY_IP_SRC]
    ip_dst = layers[JSON_KEY_IP][JSON_KEY_IP_DST]
    # Categorize source and destination IP addresses: local vs. non-local
    ip_re = re.compile(r'\b192.168.[0-9.]+')
    src_is_local = ip_re.search(ip_src) 
    dst_is_local = ip_re.search(ip_dst)

    # Skip device to cloud communication if we are interested in the local graph.
    # TODO should this go before the protocol dict is changed?
    if ONLY_INCLUDE_LOCAL_COMMUNICATION and not (src_is_local and dst_is_local):
        return

    #print "ip.src =", ip_src, "ip.dst =", ip_dst, "\n"
    # Place nodes and edges
    src_node = None
    dst_node = None
    # Integer values used for tagging nodes, indicating to Gephi if they are local IoT devices or web servers.
    remote_node = 0
    local_node = 1
    if src_is_local:
        G.add_node(eth_src, Name=dev_list[eth_src], islocal=local_node)
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
        # Non-smarthome devices can be merged later
        G.add_node(hostname, Merged='', islocal=remote_node)
        src_node = hostname

    if dst_is_local:
        G.add_node(eth_dst, Name=dev_list[eth_dst], islocal=local_node)
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
        # Non-smarthome devices can be merged later
        G.add_node(hostname, Merged='', islocal=remote_node)
        dst_node = hostname
    G.add_edge(src_node, dst_node, Protocol=protocols_str, Volume=volume)


def parse_json(file_path):
    """ Parse JSON file and create graph
        Args:
            file_path: path to the JSON file
    """
    # Create a smart home device list
    dev_list = create_device_list(DEVICE_MAC_LIST)
    # Create an exclusion list
    exc_list = create_device_list(EXCLUSION_MAC_LIST)
    # First parse the file once, constructing a map that contains information about individual devices' DNS resolutions.
    device_dns_mappings = parser.parse_dns.parse_json_dns(file_path) # "./json/eth1.dump.json"
    # Init empty graph
    G = nx.DiGraph()
    # Mapping from edge to a set of protocols
    edge_to_prot = dict()
    # Mapping from edge to traffic volume
    edge_to_vol = dict()
    # Parse file again, this time constructing a graph of device<->server and device<->device communication.
    with open(file_path) as jf:
        # Read JSON; data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)
        # Loop through json objects (packets) in data
        for p in data:
            # p is a JSON object, not an index - drill down to object containing data from the different layers
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
           
            # Place nodes and edges in graph
            place_in_graph(G, eth_src, eth_dst, device_dns_mappings, dev_list, layers, 
                edge_to_prot, edge_to_vol)

    # Print DNS mapping for reference
	#for mac in device_dns_mappings:
	#	ddm = device_dns_mappings[mac]
	#	ddm.print_mappings()
    
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
    # Contract nodes that have the same properties, i.e. same protocols
    #G = traverse_and_merge_nodes(G, DEVICE_MAC_LIST)
    # Write Graph in Graph Exchange XML format
    nx.write_gexf(G, output_file)
