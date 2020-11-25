#!/usr/bin/env bash

set -eu
# USAGE: ./remove_user.sh akvoflowsandbox userEmail

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="$FLOW_SERVER_CONFIG/$1/$1.p12"

echo "$1 "
java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     RemoveUsers \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     "$2"
