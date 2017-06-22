#!/bin/sh

# USAGE: ./fix-survey-instance-name.sh akvoflowsandbox
APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
#P12_FILE_PATH="/path/to/akvo-repositories/akvo-flow-server-config/$1/$1.p12"
P12_FILE_PATH="/home/stellan/dev/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     FixSurveyInstanceName \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $2 $3 $4
