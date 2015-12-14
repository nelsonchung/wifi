#!/bin/bash
source wifi_setting.sh
./delete_nmcli_wifi_list.sh

while [ 1 ]
do
sudo python wifi_conn.py $SSID_NAME_1 "12345678"
echo "Connect to "$SSID_NAME_1" successfully"
echo "Sleep 5 seconds"
sleep 5 
echo "Disonnecting to "$SSID_NAME_1
sudo nmcli dev disconnect iface wlan0
echo "Sleep 5 seconds to connect another AP"
sleep 5 

#sudo python wifi_conn.py "00Nelson_24G_Public" ""
sudo python wifi_conn.py $SSID_NAME_2 ""
echo "Connect to "$SSID_NAME_2" successfully"
echo "Sleep 5 seconds"
sleep 5 
echo "Disonnecting to "$SSID_NAME_2
sudo nmcli dev disconnect iface wlan0
echo "Sleep 5 seconds to connect another AP"
sleep 5 
#clear the configure file
sudo nmcli con delete id $SSID_NAME_1
sudo nmcli con delete id $SSID_NAME_2 
done
