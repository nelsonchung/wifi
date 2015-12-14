
source wifi_setting.sh

#list now wifi connection list
if [ "$UBUNTU_VER" == "15.04" ] || [ "$UBUNTU_VER" == "16.04" ]; then
    nmcli connection show
else
    nmcli c list
fi

filename='tmp.txt'
nmcli c list | awk -F ' ' '{print $1 " " $2}' > $filename
exec < $filename

while read line
do
    if echo "$line" | grep -q '[0-9]'; then
    #it means $line is number
        echo "Run command; sudo nmcli con delete id \"$line\""
        sudo nmcli con delete id "$line"
        sleep 3
    fi
done
rm $filename

