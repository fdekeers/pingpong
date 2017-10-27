#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) with DNS traffic
and constructs a map (dictionary) in which a hostname points to a set that contains the
IP addresses that is associated with that hostname.
"""

import sys
import json
from collections import defaultdict

JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"
JSON_KEY_DNS = "dns"
JSON_KEY_QUERIES = "Queries"
JSON_KEY_ANSWERS = "Answers"
JSON_KEY_DNS_RESP_TYPE = "dns.resp.type"
JSON_KEY_DNS_A = "dns.a" # Key for retrieving IP. 'a' for type A DNS record.
JSON_KEY_DNS_RESP_NAME = "dns.resp.name"
JSON_KEY_DNS_CNAME = "dns.cname"

def main():
	if len(sys.argv) < 2:
		print "Usage: python", sys.argv[0], "input_file"
		return
	hn_ip_map = parse_json(sys.argv[1])
	for hn in hn_ip_map.keys():
		print "====================================================================="
		print hn, "maps to:"
		for ip in hn_ip_map[hn]:
			print "    -", ip
	print "====================================================================="

# Convert JSON file containing DNS traffic to a map in which a hostname points to its set of associated IPs.
def parse_json(file_path):
	# Maps hostnames to IPs
	host_ip_mappings = defaultdict(set)
	with open(file_path) as jf:
		# Read JSON.
        # data becomes reference to root JSON object (or in our case json array)
		data = json.load(jf)
		# Loop through json objects in data
		# Each entry is a pcap entry (request/response (packet) and associated metadata)
		for p in data:
			# p is a JSON object, not an index
			# Drill down to DNS part: _source->layers->dns
			layers = p[JSON_KEY_SOURCE][JSON_KEY_LAYERS]
			dns = layers.get(JSON_KEY_DNS, None)
			# Skip any non DNS traffic
			if dns is None:
				print "[ WARNING: Non DNS traffic ]"
				continue
			# We only care about DNS responses as these also contain a copy of the query that they answer
			answers = dns.get(JSON_KEY_ANSWERS, None)
			if answers is None:
				continue
			## Now that we know that it is an answer, the queries should also be available.
			queries = dns.get(JSON_KEY_QUERIES)
			if len(queries.keys()) > 1:
				# Unclear if script will behave correctly for DNS lookups with multiple queries
				print "[ WARNING: Multi query DNS lookup ]"
			for ak in answers.keys():
				a = answers[ak]
				# We are looking for type A records as these are the ones that contain the IP.
				# Type A == type 1
				if a[JSON_KEY_DNS_RESP_TYPE] == "1":
					# get the IP
					ip = a[JSON_KEY_DNS_A]
					# The answer may be the canonical name.
					# Now trace back the answer stack, looking for any higher level aliases.
					hostname = find_alias_hostname(answers, a[JSON_KEY_DNS_RESP_NAME])
					# Add mapping of hostname to ip to our data structure
					host_ip_mappings[hostname].add(ip)
	return host_ip_mappings

# Recursively traverse set of answers trying to find the top most alias for a canonical name
def find_alias_hostname(answers, hostname):
	for ak in answers.keys():
		a = answers[ak]
		cname = a.get(JSON_KEY_DNS_CNAME, None)
		# We only care about type=CNAME records
		if cname is None:
			continue
		if cname == hostname:
			# Located the right answer, perform recursive search for higher level aliases.
			return find_alias_hostname(answers, a[JSON_KEY_DNS_RESP_NAME])
	return hostname

if __name__ == '__main__':
	main()