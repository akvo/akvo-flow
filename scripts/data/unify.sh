#!/bin/sh

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="$2/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     UnifyDataPointAssignment \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH

