#!/bin/sh

# USAGE: ./check-survey-strucure.sh akvoflowsandbox [FIX] [GC]
# FIX to correct survey pointers, GC to delete orphaned entites
APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/path/to/akvo-repositories/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     CheckSurveyStructure \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $2 $3 $4
