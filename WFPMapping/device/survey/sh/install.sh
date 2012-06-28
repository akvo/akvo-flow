#!/bin/sh

# Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
#
# This file is part of Akvo FLOW.
#
# Akvo FLOW is free software: you can redistribute it and modify it under the terms of
# the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
# either version 3 of the License or any later version.
#
# Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Affero General Public License included below for more details.
#
# The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>


#set this to match the paths on your system
ANDROID_HOME=c:/android-sdk-windows
APK_HOME=C:/Users/Chris/development/gallatin/wfp/trunk/WFPMapping/device/survey/bin


rm /tmp/adbinstalllog.txt

for arg
do
	$ANDROID_HOME/tools/adb -s "$arg" install -r $APK_HOME/WFP_Map_Monitor.apk  >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install -r $APK_HOME/GPSStatus.apk  >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install -r $APK_HOME/Logcat.apk >> /tmp/adbinstalllog.txt
	$ANDROID_HOME/tools/adb -s "$arg" install -r $APK_HOME/FileManager-1.1.3.apk >> /tmp/adbinstalllog.txt 
done

loggg=$(cat /tmp/adbinstalllog.txt)
echo "ADB Install Done" --msgbox "$loggg" 800 800


rm /tmp/adbinstalllog.txt
