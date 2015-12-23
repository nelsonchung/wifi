#!/bin/bash
source wifi_setting.sh
./delete_nmcli_wifi_list.sh


while [ 1 ]
do
# with wpa-psk security authentication
#move to wifi_setting.sh
#SSID_NAME_1="00Nelson_24G_Private"
SSID_NAME=$SSID_NAME_1
echo "Connecting to "$SSID_NAME
#iwlist wlan0 scan | grep $SSID_NAME 
nmcli -p dev wifi list | grep $SSID_NAME
if [ "$?" == 0 ]; then
    if [ "$UBUNTU_VER" == "15.04" ] || [ "$UBUNTU_VER" == "16.04" ]; then
        #nmcli device wifi connect $SSID_NAME password "12345678" ifname wlan0 
        nmcli device wifi connect $SSID_NAME password "12345678" ifname wlan0 > /dev/null 2>&1 
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
                count=`expr $CONNECTION_TIMEOUT + 1`
            fi
        done
    else
        sudo nmcli dev wifi con $SSID_NAME password "12345678"
        result=$?
        if [ $result -eq 0 ]; then
            echo "Connect to "$SSID_NAME" successfully"
            echo "Sleep 5 seconds"
            ifconfig wlan0
            sleep 5
        fi

    fi
else
    echo "Not found AP named $SSID_NAME"
fi
#endif

#Not do wifi disconnect to make wifi connection good or system will get wifi connection problem after test in the weekend.
#echo "Disonnecting to "$SSID_NAME
#sudo nmcli dev disconnect iface wlan0
#res_1=$?
#echo "Disonnecting to "$SSID_NAME" successfully"
## We can use the command - "nmcli c list" to check id
#echo "Sleep 5 seconds"
#sleep 5

#move to wifi_setting.sh
#SSID_NAME_2="00Nelson_24G_Public"
SSID_NAME=$SSID_NAME_2
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


#Not do wifi disconnect to make wifi connection good or system will get wifi connection problem after test in the weekend.
#echo "Disonnecting to "$SSID_NAME_2
#sudo nmcli dev disconnect iface wlan0
#res_2=$?
#echo "Disonnecting to "$SSID_NAME_2" successfully"
#echo "Sleep 5 seconds"
#sleep 5

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

SSID_NAME=$SSID_NAME_3
echo "Connecting to "$SSID_NAME
#iwlist wlan0 scan | grep $SSID_NAME 
nmcli -p dev wifi list | grep $SSID_NAME
if [ "$?" == 0 ]; then
    if [ "$UBUNTU_VER" == "15.04" ] || [ "$UBUNTU_VER" == "16.04" ]; then
        #nmcli device wifi connect $SSID_NAME password "12345678" ifname wlan0 
        nmcli device wifi connect $SSID_NAME password "12345678" ifname wlan0 > /dev/null 2>&1 
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
                count=`expr $CONNECTION_TIMEOUT + 1`
            fi
        done
    else
        sudo nmcli dev wifi con $SSID_NAME password "12345678"
        result=$?
        if [ $result -eq 0 ]; then
            echo "Connect to "$SSID_NAME" successfully"
            echo "Sleep 5 seconds"
            ifconfig wlan0
            sleep 5
        fi

    fi
else
    echo "Not found AP named $SSID_NAME"
fi

sudo nmcli con delete id $SSID_NAME
sleep 1  
done
