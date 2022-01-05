#!/bin/sh

# USAGE: ./add_users.sh akvoflowsandbox userslist.csv

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"

echo "$1 "
java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     AddUsers \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     "$2"
