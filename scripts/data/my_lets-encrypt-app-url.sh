#!/bin/sh

# USAGE: ./lets-encrypt-app-url.sh akvoflowsandbox [--doit]

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/home/stellan/dev/repos/akvo-flow-server-config/$1/$1.p12"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     LetsEncryptAppUrl \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $2 $3
