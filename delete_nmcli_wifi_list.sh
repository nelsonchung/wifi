
source wifi_setting.sh

#list now wifi connection list
nmcli c list

sudo nmcli con delete id $SSID_NAME_1 
sudo nmcli con delete id $SSID_NAME_2 
for i in $(seq 1 10)
do
#sample command
#sudo nmcli con delete id "00Nelson_24G_Private $i"
sudo nmcli con delete id "$SSID_NAME_1 $i"
sudo nmcli con delete id "$SSID_NAME_2 $i"
done

