#!/usr/bin/python

"""
Script used to extract only the needed information from JSON packet traces generated by
tshark from PCAPNG format
"""

import os, sys
import json
import uuid

from collections import OrderedDict

json_key_source = "_source"
json_key_layers = "layers"

json_key_ip = "ip"
json_key_tcp = "tcp"

json_key_http = "http"
json_key_method = "method"
json_key_uri = "uri"
json_key_headers = "headers"
json_key_host = "host"

json_key_http_req = json_key_http + ".request."
json_key_http_req_method = json_key_http_req + json_key_method
json_key_http_req_uri = json_key_http_req + json_key_uri
json_key_http_req_line = json_key_http_req + "line"

json_key_pkt_comment = "pkt_comment"

json_key_frame = "frame"
json_key_frame_num = json_key_frame + ".number"
json_key_frame_comment = json_key_frame + ".comment"
json_key_frame_ts = json_key_frame + ".time_epoch"


def make_unique(key, dct):
    counter = 0
    unique_key = key

    while unique_key in dct:
        counter += 1
        unique_key = '{}_{}'.format(key, counter)
    return unique_key


def parse_object_pairs(pairs):
    dct = OrderedDict()
    for key, value in pairs:
        if key in dct:
            key = make_unique(key, dct)
        dct[key] = value

    return dct

def change_file(fpath):
    for fn in os.listdir(fpath):
        full_path = fpath + '/' + fn

        # Recursively go through all directories
        if os.path.isdir(full_path):
            change_file(full_path)
            continue

        print full_path
        with open(full_path, "r+") as jf:
            # Since certain json 'keys' appear multiple times in our data, we have to make them
            # unique first (we can't use regular json.load() or we lose some data points). From:
            # https://stackoverflow.com/questions/29321677/python-json-parser-allow-duplicate-keys
            decoder = json.JSONDecoder(object_pairs_hook=parse_object_pairs)
            pcap_data = decoder.decode(jf.read())

            # Prepare new data structure for re-formatted JSON storage
            data = {}
            for packet in pcap_data:
                layers = packet[json_key_source][json_key_layers]

                # All captured traffic should have a frame + frame number, but check anyway
                frame_num = " Frame: "
                if json_key_frame not in layers or json_key_frame_num not in layers[json_key_frame]:
                    print "WARNING: could not find frame number! Using -1..."
                    frame_num = frame_num + "-1"
                else:
                    # Save frame number for error-reporting
                    frame_num = frame_num + layers[json_key_frame][json_key_frame_num]

                # All captured traffic should be IP, but check anyway
                if not json_key_ip in layers:
                    print "WARNING: Non-IP traffic detected!" + frame_num
                    continue

                # For now, focus on HTTP only
                if json_key_tcp not in layers or json_key_http not in layers:
                    continue

                # Fill our new JSON packet with TCP/IP info
                new_packet = {}
                new_packet["dst_ip"] = layers[json_key_ip][json_key_ip + ".dst"]
                new_packet["dst_port"] = int(layers[json_key_tcp][json_key_tcp + ".dstport"])

                # Go through all HTTP fields and extract the ones that are needed
                http_data = layers[json_key_http]
                for http_key in http_data:
                    http_value = http_data[http_key]

                    if http_key.startswith(json_key_http_req_line):
                        header_line = http_value.split(":", 1)
                        if len(header_line) != 2:
                            print ("WARNING: could not parse header '" + str(header_line) + "'"
                                   + frame_num)
                            continue

                        # Prepare container for HTTP headers
                        if json_key_headers not in new_packet:
                            new_packet[json_key_headers] = {}

                        # Use lower case for header keys to stay consistent with our other data
                        header_key = header_line[0].lower()

                        # Remove the trailing carriage return
                        header_val = header_line[1].strip()

                        # Save the header key-value pair
                        new_packet[json_key_headers][header_key] = header_val

                        # If this is the host header, we also save it to the main object
                        if header_key == json_key_host:
                            new_packet[json_key_host] = header_val

                    if json_key_http_req_method in http_value:
                        new_packet[json_key_method] = http_value[json_key_http_req_method]
                    if json_key_http_req_uri in http_value:
                        new_packet[json_key_uri] = http_value[json_key_http_req_uri]

                # End of HTTP parsing

                # Check that we found the minimum needed HTTP headers
                if (json_key_uri not in new_packet or json_key_method not in new_packet or
                        json_key_host not in new_packet):
                    print "Missing some HTTP Headers!" + frame_num
                    continue

                # Extract timestamp
                if json_key_frame_ts not in layers[json_key_frame]:
                    print "WARNING: could not find timestamp!" + frame_num
                    continue

                new_packet["ts"] = layers[json_key_frame][json_key_frame_ts]

                # Now extract and parse the packet comment
                if (json_key_pkt_comment not in layers or
                            json_key_frame_comment not in layers[json_key_pkt_comment]):
                    print "WARNING: no packet comment found!" + frame_num
                    continue

                comment = layers[json_key_pkt_comment][json_key_frame_comment]
                comment_data = json.loads(comment)
                for key in comment_data:
                    new_packet[str(key)] = str(comment_data[key])

                # Create a unique key for each packet to keep consistent with ReCon
                # Also good in case packets end up in different files
                data[str(uuid.uuid4())] = new_packet

            # Write the new data
            #print json.dumps(data, sort_keys=True, indent=4)
            jf.seek(0)
            jf.write(json.dumps(data, sort_keys=True, indent=4))
            jf.truncate()

if __name__ == '__main__':
    # Needed to re-use some JSON keys
    change_file(sys.argv[1])