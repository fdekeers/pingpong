import argparse
import ipaddress
import socket
import unicodecsv as csv

from scapy.all import *


def full_duplex(p):
    """
    For reassembling bidirectional sessions (streams). By default, Scapy only groups packets in one direction. That is,
    bidirectional sessions are split into two sessions, one with client-to-server packets, and one with server-to-client
    packets.

    Note that this is simplified session reassembly as it does not consider TCP FIN/RST packets --- packets are mapped
    to their respective session based solely on the (src_ip, src_port, dst_ip, dst_port) four-tuple. If the client (or
    server) closes a TCP stream and the client by chance selects the same ephemeral port number when contacting the same
    server again, the two DIFFERENT TCP streams will be identified as a single stream.

    Code courtesy of: https://pen-testing.sans.org/blog/2017/10/13/scapy-full-duplex-stream-reassembly

    Also note that this assumes Ethernet as layer-2 wrapper for everything. This assumption holds for our TP-Link trace,
    but will not hold in general. See discussion at:
    https://gist.github.com/MarkBaggett/d8933453f431c111169158ce7f4e2222#file-scapy_helper-py

    :param p: A Scapy packet object.
    :return: Session identifier for the packet.
    """
    sess = "Other"
    if 'Ether' in p:
        if 'IP' in p:
            if 'TCP' in p:
                sess = str(sorted(["TCP", p[IP].src, p[TCP].sport, p[IP].dst, p[TCP].dport],key=str))
            elif 'UDP' in p:
                sess = str(sorted(["UDP", p[IP].src, p[UDP].sport, p[IP].dst, p[UDP].dport] ,key=str))
            elif 'ICMP' in p:
                sess = str(sorted(["ICMP", p[IP].src, p[IP].dst, p[ICMP].code, p[ICMP].type, p[ICMP].id] ,key=str))
            else:
                sess = str(sorted(["IP", p[IP].src, p[IP].dst, p[IP].proto] ,key=str))
        elif 'ARP' in p:
            sess = str(sorted(["ARP", p[ARP].psrc, p[ARP].pdst],key=str))
        else:
            sess = p.sprintf("Ethernet type=%04xr,Ether.type%")
    return sess


def get_tls_app_data_pkts(session):
    """
    Extract the TLS Application Data packets from a (TCP) stream.
    :param tcp_session: The (TCP) stream.
    :return: The (ordered) list of TLS application data packets in session.
    """
    return session.filter(lambda pkt: TLS in pkt and pkt[TLS].type == 23)


def find_matches(pcap_file, device_ip, sig_duration):
    """
    Find all matches of [C-->S, S-->C] signatures in TLS conversations involving the device with IP=device_ip. Packet
    lengths are not considered, only directions and timing (packet lengths are assumed unavaiable due to TLS padding).
    :param pcap_file: The pcap file that is the target of the signature matching.
    :param device_ip: IP of the device whose TLS sessions are to be examined for matches.
    :param sig_duration: Maximum duration between request and response packets.
    :return: A list of (request_packet, reply_packets) tuples, where reply_packets is a list of reply packets that
             satisfy the signature match conditions (i.e., that they are within sig_duration after the request packet
             and that no other request packet interleaves the request_packet and the reply packet).
    """
    # Read all packets into memory (stored as a list).
    # This is slow and consumes lots of memory.
    # There are more efficient ways to read the pcap (which clear each packet from memory after it's been processed).
    # However, to simplify the detection implementation we stick with the quick-and-dirty approach.
    pkts = rdpcap(pcap_file)
    matches = []
    # Group packets into sessions (streams)
    sessions_dict = pkts.sessions(full_duplex)
    for sess_key in sessions_dict:
        session = sessions_dict[sess_key]
        tls_app_data_pkts = get_tls_app_data_pkts(session)
        if len(tls_app_data_pkts) == 0:
            # Session w/o any TLS traffic, not relevant.
            continue
        first_pkt = tls_app_data_pkts[0]
        if IP not in first_pkt:
            # Only consider IPv4 traffic.
            continue
        if first_pkt[IP].src != device_ip and first_pkt[IP].dst != device_ip:
            # Traffic from some other device; ignore -- not relevant to us.
            continue
        if ipaddress.ip_address(first_pkt[IP].src).is_multicast or ipaddress.ip_address(first_pkt[IP].dst).is_multicast:
            # Don't include multicast traffic in the results.
            # (Should never occur as TLS is not used for multicast?)
            continue
        # Now let's find all the potential matches for the current TLS session.
        for i, request_pkt in enumerate(tls_app_data_pkts):
            if request_pkt[IP].src != device_ip:
                # We are trying to find matches for a simple [C->S, S->C] signature, so we want to first identify an
                # outbound (device-to-cloud) packet and then subsequently find all potential reply packets
                # (cloud-to-device). If this is a cloud-to-device packet, it is of no interest to us at this stage, so
                # move on.
                continue
            # All subsequent cloud-to-device packets (replies) in this TLS session that lie within the signature
            # duration after this packet AND that are not preceded by a device-to-cloud packet that is later than the
            # current packet can be paired with the current packet to constitute a potential signature match.
            idx = i+1
            replies = []
            while idx < len(tls_app_data_pkts) and tls_app_data_pkts[idx][IP].dst == device_ip:
                reply_pkt = tls_app_data_pkts[idx]
                if reply_pkt.time - request_pkt.time <= sig_duration:
                    # Could have this check in the loop condition as well. But some times packet order != timestamp
                    # order.
                    replies.append(reply_pkt)
                idx += 1
            matches.append((request_pkt, replies))
    return matches


def get_pkt_key(pkt):
    """
    Get a string representation of a packet that can be used as a key in a dictionary.
    :param pkt: A Scapy packet.
    :return: A string representation of a packet that can be used as a key in a dictionary.
    """
    return f'src={pkt.src} dst={pkt.dst} timestamp={pkt.time}'


def build_pkt_number_dict(pcap_file):
    """
    Create a dictionary mapping packets to their packet number in pcap_file.
    The keys are generated by passing each packet to get_pkt_key(pkt).
    :param pcap_file: The pcap file for which a packet number dictionary is desired.
    :return: A dictionary mapping packet keys (obtainable from get_pkt_key(pkt)) to the packets packet number.
    """
    pkts = rdpcap(pcap_file)
    map = {}
    for i, pkt in enumerate(pkts):
        pkt_num = i + 1
        key = get_pkt_key(pkt)
        assert(key not in map)
        map[key] = pkt_num
    assert(len(map) == len(pkts))
    # Double check that numbers come out right. Can be removed in final version.
    pkts = rdpcap(pcap_file)
    for i, pkt in enumerate(pkts):
        pkt_key = get_pkt_key(pkt)
        assert(pkt_key in map and map[pkt_key] == i+1)
    return map


def add_pkt_numbers_to_matches(pcap_file, matches):
    """
    Hacky way to augment the matches with packet numbers. Assumes the same device does not send or receive more than
    one packet at a given timestamp.
    :param pcap_file: The pcap file where the matches were found in.
    :param matches: The matches.
    :return: matches augmented with packet numbers; each packet is converted to a (pkt, pkt_number) tuple.
    """
    pkt_nums_dict = build_pkt_number_dict(pcap_file)
    result = []
    for req_pkt, replies in matches:
        req_pkt_num = pkt_nums_dict[get_pkt_key(req_pkt)] #find_pkt_number(req_pkt, pcap_file)
        numbered_req_pkt = (req_pkt, req_pkt_num)
        numbered_reply_pkts = []
        for reply_pkt in replies:
            reply_pkt_num = pkt_nums_dict[get_pkt_key(reply_pkt)] #find_pkt_number(reply_pkt, pcap_file)
            numbered_reply_pkts.append((reply_pkt, reply_pkt_num))
        result.append((numbered_req_pkt, numbered_reply_pkts))
    return result


def write_matches_to_csv(matches, csv_filename):
    """
    Output matches to a .csv file.
    matches argument is expected to be in the format returned by add_pkt_numbers_to_matches(pcap_file, matches).
    :param matches: A list of matches w/ packet numbers, as returned by add_pkt_numbers_to_matches(pcap_file, matches).
    :param csv_filename: Path to the .csv file where the output is to be written.
    :return: None.
    """
    key_req_pkt = 'request_pkt'
    key_reply_pkts = 'reply_pkts'
    key_reply_pkts_count = 'number_of_reply_pkts'
    key_conversation_info = 'tls_conversation_between'
    columns = [key_req_pkt, key_reply_pkts, key_reply_pkts_count, key_conversation_info]
    with open (csv_filename, 'wb') as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=columns)
        writer.writeheader()
        for m in matches:
            request_pkt = m[0][0]
            request_pkt_num = m[0][1]
            reply_pkts_numbers = []
            for (reply_pkt, reply_pkt_num) in m[1]:
                reply_pkts_numbers.append(reply_pkt_num)
            info = f'{request_pkt[IP].src+":"+str(request_pkt[TCP].sport)} and ' + \
                   f'{request_pkt[IP].dst+":"+str(request_pkt[TCP].dport)}'
            row = { key_req_pkt: request_pkt_num,
                    key_reply_pkts: '; '.join(str(pkt_num) for pkt_num in reply_pkts_numbers),
                    key_reply_pkts_count: len(reply_pkts_numbers),
                    key_conversation_info: info}
            writer.writerow(row)


if __name__ == '__main__':
    desc = 'Perform detection on padded TLS traffic; ' + \
           'i.e., the detection is entirely based on timing information and packet directions. ' + \
           'NOTE: THIS CODE IS SIMPLIFIED AND ONLY WORKS FOR SIMPLE [Client-to-Server, Server-to-Client] TWO ' + \
           'PACKET SIGNATURES.'
    parser = argparse.ArgumentParser(description=desc)
    parser.add_argument('pcap_file', help='Full path to the target pcap file (detection target trace).')
    parser.add_argument('device_ip', help='Perform detection on TLS flows from this device (identified by IP) only.')
    h = 'Duration of the signature ' + \
        '(max time between request and reply packet for the two packets to be considered a match). ' + \
        'Unit: seconds (floating point number expected).'
    parser.add_argument('signature_duration',
                        help=h, type=float)
    parser.add_argument('output_csv', help='Filename of CSV file where results are to be written.')
    args = parser.parse_args()

    pcap_file = args.pcap_file
    device_ip = args.device_ip
    signature_duration = args.signature_duration
    output_csv = args.output_csv

    load_layer('tls')

    matches = find_matches(pcap_file, device_ip, signature_duration)
    matches = add_pkt_numbers_to_matches(pcap_file, matches)
    write_matches_to_csv(matches, output_csv)

