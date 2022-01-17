#! /bin/sh

# USAGE: ./${0}.sh akvoflowsandbox <question_id> [--delete]

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"
QUESTION_ID=$2
CONFIRM_DELETE=$3

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     FindQuestionResponses \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $QUESTION_ID \
     $CONFIRM_DELETE
