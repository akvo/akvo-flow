#!/bin/sh

# USAGE: ./public_private_instance_count.sh akvoflowsandbox


APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
P12_FILE_PATH="/home/stellan/dev/akvo-flow-server-config/$1/$1.p12"
CHUNK_SIZE="300"

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     PublicPrivateInstanceCount \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH $CHUNK_SIZE $APP_ID
