#!/bin/sh

# USAGE: ./remove_orphaned_survey_instances.sh akvoflow-uat1 [--doit]

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="$FLOW_SERVER_CONFIG/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     FixOrphanedSubmissions \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $1
     $2
