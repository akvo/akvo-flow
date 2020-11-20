#!/bin/sh

# need FLOW_SERVER_CONFIG set up to akvo-flow-server-config repo path
# use `ant` to compile the java project
# USAGE: ./fix-old-translations.sh akvoflowsandbox

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="$FLOW_SERVER_CONFIG/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     FixOldTranslations \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH
