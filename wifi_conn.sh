#!/bin/bash

while [ 1 ]
do
sudo python wifi_conn.py "00Nelson_24G_Private" "12345678"
echo "Sleep 5 seconds to connect another AP"
sleep 5
sudo python wifi_conn.py "00Nelson_24G_Public" ""
sleep 5
echo "Sleep 5 seconds to connect another AP"
done
