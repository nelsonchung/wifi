#此腳本能夠連線2.4G/5G AP, 總共四個AP的連線。一個連線成功後, 斷線再接著連線下一個AP
#針對不加密的AP
#wpapsk的加密AP,可以參考fast_wifi_conn.sh

#!/bin/bash
export CONNECTION_TIMEOUT=60
export SSID_NAME_1="Nelson_24G_Private"
export SSID_NAME_2="Nelson_24G_Public"
export SSID_NAME_3="Nelson_5G_Private"
export SSID_NAME_4="Nelson_5G_Public"
export SSID_NAME_2_TIMEOUT=600


#No LSB modules are available.
#Distributor ID:	Ubuntu
#Description:	Ubuntu 15.04
#Release:	15.04
#Codename:	vivid
Ubuntu_Ver=`lsb_release -a | grep "Release:" | awk -F " " '{print $2}'`
export UBUNTU_VER=$Ubuntu_Ver

index_cnt=1

while [ 1 ]
do

index_cnt=`expr $index_cnt % 4`
index_cnt=`expr $index_cnt + 1`
if [ $index_cnt -eq 1 ]; then
    SSID_NAME=$SSID_NAME_1
elif [ $index_cnt -eq 2 ]; then
    SSID_NAME=$SSID_NAME_2
elif [ $index_cnt -eq 3 ]; then
    SSID_NAME=$SSID_NAM_3
elif [ $index_cnt -eq 4 ]; then
    SSID_NAME=$SSID_NAME_4
fi
nmcli -p dev wifi list | grep $SSID_NAME
echo "Connecting to "$SSID_NAME
# with no security authentication
nmcli -p dev wifi list | grep $SSID_NAME
if [ "$?" == 0 ]; then
    if [ "$UBUNTU_VER" == "15.04" ] || [ "$UBUNTU_VER" == "16.04" ]; then
        nmcli device wifi connect $SSID_NAME ifname wlan0 > /dev/null 2>&1
        count=1
        while [ $count -lt $CONNECTION_TIMEOUT ]
        do
            #echo "Check the connection of $SSID_NAME"
            #nmcli device status | grep $SSID_NAME | grep "已連線" 
            nmcli con status | grep $SSID_NAME
            if [ "$?" != 0 ]; then 
                #echo "Not connect yet."
                count=`expr $count + 1`
                sleep 1
            else
                echo "Connect to "$SSID_NAME" successfully"
                echo "Sleep 5 seconds"
                ifconfig wlan0
                sleep 5
                count=`expr $CONNECTION_TIMEOUT + 2`
            fi
        done
        NOT_CONNECTION_YET=`expr $CONNECTION_TIMEOUT + 1`
        if [ $count -eq $NOT_CONNECTION_YET ]; then
            nmcli radio wifi off
            nmcli radio wifi on
        fi
    else
        sudo nmcli dev wifi con $SSID_NAME --timeout $SSID_NAME_2_TIMEOUT 
        result=$?
        if [ $result -eq 0 ]; then
            echo "Connect to "$SSID_NAME" successfully"
            echo "Sleep 5 seconds"
            ifconfig wlan0
            sleep 5
        else
            echo "Connect to "$SSID_NAME" fail. You need to check why fail"
            nmcli nm wifi off
            nmcli nm wifi on
        fi
    fi
else
    echo "Not found AP named $SSID_NAME"
fi


sleep 5
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

sudo nmcli con delete id $SSID_NAME_3
sudo nmcli con delete id $SSID_NAME_4

#SSID_NAME=$SSID_NAME_3
#sudo nmcli con delete id $SSID_NAME
sleep 1  
done
