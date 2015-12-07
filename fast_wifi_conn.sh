#!/bin/bash
source wifi_setting.sh
./delete_nmcli_wifi_list.sh

while [ 1 ]
do
# with wpa-psk security authentication
#move to wifi_setting.sh
#SSID_NAME_1="00Nelson_24G_Private"
echo "Connecting to "$SSID_NAME_1
sudo nmcli dev wifi con $SSID_NAME_1 password "12345678"
echo "Connect to "$SSID_NAME_1" successfully"
echo "Sleep 5 seconds"
sleep 5 
echo "Disonnecting to "$SSID_NAME_1
sudo nmcli dev disconnect iface wlan0
res_1=$?
echo "Disonnecting to "$SSID_NAME_1" successfully"
# We can use the command - "nmcli c list" to check id
echo "Sleep 5 seconds"
sleep 5 

#move to wifi_setting.sh
#SSID_NAME_2="00Nelson_24G_Public"
echo "Connecting to "$SSID_NAME_2
# with no security authentication
#sudo nmcli dev wifi con $SSID_NAME_2
# test timeout command
sudo nmcli dev wifi con $SSID_NAME_2 --timeout $SSID_NAME_2_TIMEOUT 
echo "Connect to "$SSID_NAME_2" successfully"
echo "Sleep 5 seconds"
sleep 5 
echo "Disonnecting to "$SSID_NAME_2
sudo nmcli dev disconnect iface wlan0
res_2=$?
echo "Disonnecting to "$SSID_NAME_2" successfully"
echo "Sleep 2 seconds"
sleep 2  

#clear the configure file
#configure file still exist when connection fail
#if [ "$res_1" == "0" ]; then
#echo "Delete configure file - "$SSID_NAME_1
sudo nmcli con delete id $SSID_NAME_1
#fi

#if [ "$res_2" == "0" ]; then
#echo "Delete configure file - "$SSID_NAME_2
sudo nmcli con delete id $SSID_NAME_2
#fi

sleep 1  
done
