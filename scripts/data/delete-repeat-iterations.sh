#!/bin/sh

# USAGE: ./delete-repeat-iterations.sh akvoflow-uat1 [surveyInstanceId] [group name] [1,2,4] [d | u]
# d to delete the iterations
# u to update the itertion number on remaining iterations
APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     DeleteRepeatQuestionGroupIteration \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     "$2" "$3" "$4" "$5"
