#!/bin/bash
export CONNECTION_TIMEOUT=60
export SSID_NAME_1="00Nelson_24G_Private"
export SSID_NAME_2="TELENETHOMESPOT"
export SSID_NAME_2_TIMEOUT=600
export SSID_NAME_3="00Nelson_5G_Private"

#export SSID_NAME_1="1-TELENET"
#export SSID_NAME_2="1-TELENETHOMESPOT"
#export SSID_NAME_2_TIMEOUT=600


#No LSB modules are available.
#Distributor ID:	Ubuntu
#Description:	Ubuntu 15.04
#Release:	15.04
#Codename:	vivid
Ubuntu_Ver=`lsb_release -a | grep "Release:" | awk -F " " '{print $2}'`
export UBUNTU_VER=$Ubuntu_Ver
