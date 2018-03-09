#!/bin/sh

# USAGE: ./check-datapoint-location.sh akvoflow-uat1 [FIX]
# FIX to actually fix the datapoint location
APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     CheckDataPointLocation \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $2
