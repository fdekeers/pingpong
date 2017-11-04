#!/usr/bin/python

"""
Script that takes a file (output by wireshark/tshark, in JSON format) with DNS traffic
and constructs a map (dictionary) in which a hostname points to a set that contains the
IP addresses that is associated with that hostname.
"""

import sys
import json
from collections import defaultdict
from decimal import *

ROUTER_MAC = "b0:b9:8a:73:69:8e"

JSON_KEY_SOURCE = "_source"
JSON_KEY_LAYERS = "layers"
JSON_KEY_DNS = "dns"
JSON_KEY_QUERIES = "Queries"
JSON_KEY_ANSWERS = "Answers"
JSON_KEY_DNS_RESP_TYPE = "dns.resp.type"
JSON_KEY_DNS_A = "dns.a" # Key for retrieving IP. 'a' for type A DNS record.
JSON_KEY_DNS_RESP_NAME = "dns.resp.name"
JSON_KEY_DNS_CNAME = "dns.cname"
JSON_KEY_ETH = "eth"
JSON_KEY_ETH_DST = "eth.dst"
JSON_KEY_FRAME = "frame"
JSON_KEY_FRAME_TIME_EPOCH = "frame.time_epoch"

def main():
	if len(sys.argv) < 2:
		print "Usage: python", sys.argv[0], "input_file"
		return
	mac_to_ddm = parse_json_dns(sys.argv[1])
	for mac in mac_to_ddm:
		ddm = mac_to_ddm[mac]
		ddm.print_mappings()
	# maps_tuple = parse_json_dns(sys.argv[1])
	
	# # print hostname to ip map
	# hn_ip_map = maps_tuple[0]
	# for hn in hn_ip_map.keys():
	# 	print "====================================================================="
	# 	print hn, "maps to:"
	# 	for ip in hn_ip_map[hn]:
	# 		print "    -", ip
	# print "====================================================================="
	
	# print " "

	# # print ip to hostname map
	# ip_hn_map = maps_tuple[1]
	# for ip in ip_hn_map.keys():
	# 	print "====================================================================="
	# 	print ip, "maps to:"
	# 	for hn in ip_hn_map[ip]:
	# 		print "    -", hn
	# print "====================================================================="

class DeviceDNSMap:
	def __init__(self, mac_address):
		# MAC address of device
		self.mac = mac_address
		# Maps an external IP to a list of (timestamp,hostname) tuples.
		# Entries in the list should be interpreted as follows:
		# the timestamp indicates WHEN this device mapped the given ip (key in dict) to the hostname.
		self.ip_mappings = defaultdict(list)

	def hostname_for_ip_at_time(self, ip, timestamp):
		# Does device have a mapping for the given IP?
		if not ip in self.ip_mappings:
			return None
		if not self.ip_mappings[ip]:
			# If list of (timestamp,hostname) tuples is empty, there is no mapping to report.
			return None
		# Best fit mapping: the mapping immediately BEFORE timestamp parameter.
		# Start with random pick (element 0).
		best_fit = self.ip_mappings[ip][0]
		for t in self.ip_mappings[ip]:
			# t is a (timestamp,hostname) tuple
			if t[0] < timestamp and t[0] > best_fit[0]:
				# t is a better fit if it happened BEFORE the input timestamp
				# and is LATER than the current best_fit
				best_fit = t
		return best_fit

	def add_mapping(self, ip, timestamp_hostname_tuple):
		self.ip_mappings[ip].append(timestamp_hostname_tuple)

	def print_mappings(self):
		count = 0
		print "### Mappings for MAC = ", self.mac, "###"
		for ip in self.ip_mappings:
			print "--- IP ", ip, " maps to: ---"
			for t in self.ip_mappings[ip]:
				print t[1], "at epoch time =", t[0]
				count += 1
		print "### Total of", count, "mappings for", self.mac, "###"

	# --------------------------------------------------------------------------
	# Define eq and hash such that instances of the class can be used as keys in dictionaries.
	# Equality is based on MAC as a MAC uniquely identifies the device.
	def __eq__(self, another):
		return hasattr(another, 'mac') and self.mac == another.mac
	def __hash__(self):
		return hash(self.data)
	# --------------------------------------------------------------------------


def parse_json_dns(file_path):
	# Our end output: dictionary of MAC addresses with DeviceDNSMaps as values.
	# Each DeviceDNSMap contains DNS lookups performed by the device with the corresponding MAC.
	result = defaultdict()
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
			# Get ethernet information for identifying the device performing the DNS lookup.
			eth = layers.get(JSON_KEY_ETH, None)
			if eth is None:
				print "[ WARNING: eth data not found ]"
				continue
			# As this is a response to a DNS query, the IoT device is the destination.
			# Get the device MAC of that device.
			device_mac = eth.get(JSON_KEY_ETH_DST, None)
			if device_mac is None:
				print "[ WARNING: eth.dst data not found ]"
				continue
			# Get the router's timestamp for this packet
			# so that we can mark when the DNS mapping occurred
			timestamp = Decimal(layers[JSON_KEY_FRAME][JSON_KEY_FRAME_TIME_EPOCH])
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
					# Create the tuple that indicates WHEN the ip to hostname mapping occurred
					timestamp_hostname_tuple = (timestamp,hostname)
					if device_mac in result:
						# If we already have DNS data for the device with this MAC:
						# Add the mapping to the DeviceDNSMap that is already present in the dict.
						result[device_mac].add_mapping(ip, timestamp_hostname_tuple)
					else:
						# No DNS data for this device yet:
						# Create a new DeviceDNSMap, add the mapping, and at it to the dict.
						ddm = DeviceDNSMap(device_mac)
						ddm.add_mapping(ip, timestamp_hostname_tuple)
						result[device_mac] = ddm
	return result

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

# ================================================================================================
# Notes/brainstorming how to do ip to host mappings.

# Maps IPs to hostnames. Uses a dictionary of dictionaries.
# IP lookup in the outer dictionary returns a dictionary that has hostnames as keys.
# Looking up a hostname in the inner dictionary returns a set of timestamps.
# Each timestamp indicate the time at which the IP<->hostname mapping was determined by a DNS query.
# Note that the keyset of the inner dictionary will be of size 1 in most cases.
# When this is the case, the value (the set of timestamps) can be ignored.
# The values are only relevant when one IP maps to more than 1 hostname.
# When this the case, the timestamps must be considered to find the most recent mapping.
# ip_host_mappings = defaultdict(defaultdict(set))

# ================================================================================================