#!/bin/sh

# Run the docker-compose env, so that the GAE classes are compiled
# docker run --rm -it -v `pwd`:/root/flow -v $(pwd)/../../akvo-flow-server-config/:/p12  frekele/ant:1.10-jdk8 bash
# cd /root/flow/scripts/data; sh export_auth_data_to_event_log.sh akvoflowsandbox

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/p12/$1/$1.p12"

ant
ant -Dargs="ExportAuthDataToEventLog $APP_ID $SERVICE_ACCOUNT $P12_FILE_PATH $APP_ID" remoteAPI