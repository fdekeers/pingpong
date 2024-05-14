import argparse
import ipaddress
import socket
import unicodecsv as csv

from scapy.all import *


def find_matches(pcap_file, router_wan_ip, sig_duration):
    # Read all packets into memory (stored as a list).
    # This is slow and consumes lots of memory.
    # There are more efficient ways to read the pcap (which clear each packet from memory after it's been processed).
    # However, to simplify the detection implementation we stick with the quick-and-dirty approach.
    pkts = rdpcap(pcap_file)
    # The potential signature matches, with the request packet as the first item of a tuple, and all possible reply
    # packets as the second item.
    matches = []
    for idx, p in enumerate(pkts):
        # Only consider IP traffic (note: this does not account for IPv6).
        if not IP in p:
            continue
        # Note: IP addresses are apparently stored in string form (odd)
        src = p[IP].src
        dst = p[IP].dst
        if src != router_wan_ip:
            # We are trying to find matches for a simple [C->S, S->C] signature, so we want to first identify an
            # outbound (router-to-cloud) packet and then subsequently find all potential reply packets (cloud-to-router)
            # If this is a cloud-to-router packet, it is of no interest to us at this stage, so move on.
            continue
        # TODO should we exclude all multicasts+broadcasts? They wouldn't occur and/or be tunneled?
        if ipaddress.ip_address(dst).is_multicast:
            # Don't include multicast traffic originating from the router in the results.
            continue
        # Find the set of potential reply packets for this request.
        replies = find_reply_pkts(router_wan_ip, pkts, idx, sig_duration)
        # Store packet index alongside packet so that we can provide packet numbers for post analysis.
        matches.append(((p, idx), replies))
    return matches


def find_reply_pkts(router_wan_ip, pkts, request_pkt_idx, sig_duration):
    request_pkt = pkts[request_pkt_idx]
    idx = request_pkt_idx + 1
    reply_pkts = []
    while idx < len(pkts) and pkts[idx].time - request_pkt.time <= sig_duration:
        pkt = pkts[idx]
        if is_inbound_ip_pkt(pkt, router_wan_ip):
            # Only count IP packets with router WAN IP as destination as potential replies to the request.
            # Store packet index alongside packet so that we can provide packet numbers for post analysis.
            reply_pkts.append((pkt, idx))
        idx += 1
    return reply_pkts


def is_inbound_ip_pkt(pkt, router_wan_ip):
    return IP in pkt and pkt[IP].dst == router_wan_ip


def write_matches_to_csv(matches, csv_filename):
    key_req_pkt = 'request_pkt'
    key_reply_pkts = 'reply_pkts'
    key_reply_pkts_count = 'number_of_reply_pkts'
    columns = [key_req_pkt, key_reply_pkts, key_reply_pkts_count]
    with open (csv_filename, 'wb') as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=columns)
        writer.writeheader()
        for m in matches:
            request_pkt = m[0][0]
            request_pkt_idx = m[0][1]
            # Wireshark packet numbers start from 1 (are not 0-based)
            request_pkt_num = request_pkt_idx + 1
            reply_pkts_numbers = []
            for (reply_pkt, reply_pkt_idx) in m[1]:
                reply_pkt_num = reply_pkt_idx + 1
                reply_pkts_numbers.append(reply_pkt_num)
            row = { key_req_pkt: request_pkt_num,
                    key_reply_pkts: '; '.join(str(pkt_num) for pkt_num in reply_pkts_numbers),
                    key_reply_pkts_count: len(reply_pkts_numbers) }
            writer.writerow(row)


if __name__ == '__main__':
    desc = 'Perform detection on traffic in a VPN tunnel where traffic is padded; ' + \
           'i.e., the detection is entirely based on timing information and packet directions. ' + \
            'NOTE: THIS CODE IS SIMPLIFIED AND ONLY WORKS FOR SIMPLE [Client-to-Server, Server-to-Client] TWO ' + \
            'PACKET SIGNATURES.'
    parser = argparse.ArgumentParser(description=desc)
    parser.add_argument('pcap_file', help='Full path to the target pcap file (detection target trace).')
    parser.add_argument('router_wan_ip', help='IP of WAN interface of the home router (in decimal format).')
    h = 'Duration of the signature ' + \
        '(max time between request and reply packet for the two packets to be considered a match). ' + \
        'Unit: seconds (floating point number expected).'
    parser.add_argument('signature_duration',
                        help=h, type=float)
    parser.add_argument('output_csv', help='Filename of CSV file where results are to be written.')
    args = parser.parse_args()

    pcap_file = args.pcap_file
    router_wan_ip = args.router_wan_ip
    signature_duration = args.signature_duration
    output_csv = args.output_csv
    print('Parsed arguments:')
    print(f'pcap_file={pcap_file}')
    print(f'router_wan_ip={router_wan_ip}')
    print(f'signature_duration={signature_duration}')
    print(f'output_csv={output_csv}')

    events = find_matches(pcap_file, router_wan_ip, signature_duration)
    # for e in events:
    #     request_pkt = e[0][0]
    #     request_pkt_num = e[0][1] + 1
    #     for (reply_pkt, reply_pkt_num) in e[1]:
    #         print(f"MATCH: Packet number {request_pkt_num} (request) with packet number {reply_pkt_num + 1} (reply).")
    write_matches_to_csv(events, output_csv)
