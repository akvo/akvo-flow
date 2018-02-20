#!/bin/sh

# USAGE: ./check_options.sh akvoflowsandbox

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/c/_Dev/repos/akvo_server-config/$1/$1.p12"

echo "$1 "
java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     AddUsersCheckOptions \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     "$2"
