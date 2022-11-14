#! /bin/sh

# USAGE: ./${0}.sh akvoflowsandbox <question_id> <instance_id> <image_url>
# EXAMPLE: ./replace-caddisfly-image.sh akvoflow-62 51970912 56440924 467560ab-317b-4148-9551-c8c1fe17e43c.jpg

APP_ID=$1
SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
REPOS_HOME="$(cd $(dirname "$THIS_SCRIPT")/../../.. && pwd)"
P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$1/$1.p12"
QUESTION_ID=$2
INSTANCE_ID=$3
NEW_IMAGE=$4

java -cp bin:"lib/*" \
     org.akvo.gae.remoteapi.RemoteAPI \
     ReplaceCaddisflyImage \
     $APP_ID \
     $SERVICE_ACCOUNT \
     $P12_FILE_PATH \
     $QUESTION_ID \
     $INSTANCE_ID \
     $NEW_IMAGE
