#!/bin/sh
#


#set this to match the paths on your system
ANDROID_HOME=c:/android-sdk-windows
APK_HOME=C:/Users/Chris/development/gallatin/wfp/trunk/WFPMapping/device/survey/bin


rm /tmp/adbinstalllog.txt

for arg
do
	$ANDROID_HOME/tools/adb -s "$arg" install $APK_HOME/WFP_Map_Monitor.apk  >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install $APK_HOME/GPSStatus.apk  >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install $APK_HOME/Logcat.apk >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install $APK_HOME/FileManager-1.1.3.apk >> /tmp/adbinstalllog.txt 
done

loggg=$(cat /tmp/adbinstalllog.txt)
echo "ADB Install Done" --msgbox "$loggg" 800 800


rm /tmp/adbinstalllog.txt
