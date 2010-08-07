#!/bin/sh
#

rm /tmp/adbinstalllog.txt

for arg
do
	/Users/dru/devenv/tools/android-sdk-mac_86-1/tools/adb -s "$arg" install ../bin/WFP_Map_Monitor.apk  >> /tmp/adbinstalllog.txt
	/Users/dru/devenv/tools/android-sdk-mac_86-1/tools/adb -s "$arg" install ../bin/GPSStatus.apk  >> /tmp/adbinstalllog.txt
	/Users/dru/devenv/tools/android-sdk-mac_86-1/tools/adb -s "$arg" install ../bin/Logcat.apk >> /tmp/adbinstalllog.txt
	/Users/dru/devenv/tools/android-sdk-mac_86-1/tools/adb -s "$arg" install ../bin/FileManager-1.1.3.apk >> /tmp/adbinstalllog.txt 
done

loggg=$(cat /tmp/adbinstalllog.txt)
echo "ADB Install Done" --msgbox "$loggg" 800 800


rm /tmp/adbinstalllog.txt
