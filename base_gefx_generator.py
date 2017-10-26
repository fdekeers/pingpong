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

def parse_json(file_path):
    # Init empty graph
    G = nx.DiGraph() 
    with open(file_path) as jf:
        # Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
        data = json.load(jf)
        # Loop through json objects in data
        for k in data:
            # Fetch source and destination IPs.
            # Each of these become a Node in the Graph.
            src_ip = data[k]["src_ip"]
            dst_ip = data[k]["dst_ip"]
            ''' Graph construction '''
            # No need to check if the Nodes and/or Edges we add already exist:
            # NetworkX won't add already existing nodes/edges (except in the case of a MultiGraph or MultiDiGraph (see NetworkX doc)).
            # Add a node for each host.
            G.add_node(src_ip)
            G.add_node(dst_ip)
            # Connect these two nodes.
            G.add_edge(src_ip, dst_ip)
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
