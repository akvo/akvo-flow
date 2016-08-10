#!/bin/sh

# USAGE: ./fix_imagecheck_queue.sh akvoflowsandbox


APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/path/to/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     FixImageCheckFilename \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH
