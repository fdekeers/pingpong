#!/bin/sh

# Drop packets with lengths 1299, 1300, and 640
#iptables -I INPUT -s 192.168.1.108 -p tcp -j DROP
#iptables -I INPUT -d 192.168.1.108 -p tcp -j DROP
#iptables -I OUTPUT -s 192.168.1.108 -p tcp -j DROP
#iptables -I OUTPUT -d 192.168.1.108 -p tcp -j DROP
#iptables -I FORWARD -s 192.168.1.108 -p tcp -j DROP
#iptables -I FORWARD -d 192.168.1.108 -p tcp -j DROP

# Ecobee
# HVAC
#iptables -I FORWARD -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1280:1290 -j DROP
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 1280:1290 -j DROP
# Fan
#iptables -I FORWARD -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1370:1380 -j DROP
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 1370:1380 -j DROP

# Nest
# Fan
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 875:885 -j DROP
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 840:860 -j DROP

# Amazon plug

# WeMo plug
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 240:250 -j DROP
#iptables -I FORWARD -d 192.168.1.59 -p tcp -m length --length 240:250 -j DROP                
# WeMo Insight plug
#iptables -I FORWARD -d 192.168.1.10 -p tcp -m length --length 240:250 -j DROP

# TP-Link plug
#iptables -I FORWARD -s 192.168.1.74 -p tcp -m length --length 570:590 -j DROP
# We need to block the entire traffic to the plug to make this work
#iptables -I FORWARD -d 192.168.1.74 -p tcp -j DROP

# D-Link plug
# Phone signature
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 1080:1120 -j DROP
# Device signature
iptables -I FORWARD -d 192.168.1.58 -p tcp -m length --length 70:100 -j DROP

# SmartThings plug
#iptables -I FORWARD -s 192.168.1.108 -p tcp -m length --length 680:700 -j DROP

#iptables -I FORWARD -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1300 -j DROP
#iptables -I FORWARD -d 192.168.1.108 -s 216.220.61.235 -p tcp -m length --length 640 -j DROP
#iptables -I FORWARD -s 216.220.61.235 -d 192.168.1.108 -p tcp -m length --length 620:630 -j DROP
#iptables -I INPUT -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1299 -j DROP
#iptables -I INPUT -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1300 -j DROP
#iptables -I INPUT -d 192.168.1.108 -s 216.220.61.235 -p tcp -m length --length 640 -j DROP
#iptables -I OUTPUT -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1299 -j DROP
#iptables -I OUTPUT -s 192.168.1.108 -d 216.220.61.235 -p tcp -m length --length 1300 -j DROP
#iptables -I OUTPUT -d 192.168.1.108 -s 216.220.61.235 -p tcp -m length --length 640 -j DROP

#iptables -I FORWARD -s 192.168.1.108 -d 216.220.61.235 -p tcp -j DROP
#iptables -I FORWARD -s 216.220.61.235 -d 192.168.1.108 -p tcp -j DROP
#iptables -I FORWARD -s 192.168.1.108 -p tcp -j DROP
#iptables -I FORWARD -d 192.168.1.108 -p tcp -j DROP
