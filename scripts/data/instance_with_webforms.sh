#!/bin/sh

# USAGE: ./instance_with_webforms.sh akvoflow-uat1 2021-05-01
APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"
DATE=$2

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     InstanceWithWebFormSubmissions \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
	 $DATE
