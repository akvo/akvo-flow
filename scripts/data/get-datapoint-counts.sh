#!/bin/sh

# USAGE: ./get-datapoint-counts.sh akvoflowsandbox

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"
APP_ENGINE_FILE="$REPOS_HOME/akvo-flow-server-config/$1/appengine-web.xml"
INSTANCE_NAME=$(grep "alias" $APP_ENGINE_FILE \
    | sed 's/.*value="\([^"]*\).*/\1/' \
    | cut -d '.' -f 1)


java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     GetDatapointCounts \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $INSTANCE_NAME
