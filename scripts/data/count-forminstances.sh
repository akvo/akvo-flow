#!/bin/sh

# USAGE: ./count-forminstances.sh akvoflowsandbox

getforminstancecount() {
    SERVICE_ACCOUNT="sa-$APP_ID@$APP_ID.iam.gserviceaccount.com"
    DIR_NAME="$(dirname "${THIS_SCRIPT}")/../../.."
    REPOS_HOME="$(cd "${DIR_NAME}" && pwd)"
    P12_FILE_PATH="$REPOS_HOME/akvo-flow-server-config/$APP_ID/$APP_ID.p12"
    APP_ENGINE_FILE="$REPOS_HOME/akvo-flow-server-config/$APP_ID/appengine-web.xml"
    INSTANCE_NAME=$(grep "alias" "${APP_ENGINE_FILE}" \
        | sed 's/.*value="\([^"]*\).*/\1/' \
        | cut -d '.' -f 1)
    CSV_FILE="/tmp/form-instance-counts_$INSTANCE_NAME.csv"
    [ -e "${CSV_FILE}" ] && rm "${CSV_FILE}"
    java -cp bin:"lib/*" \
         org.akvo.gae.remoteapi.RemoteAPI \
         CountFormInstances \
         "${APP_ID}" \
         "${SERVICE_ACCOUNT}" \
         "${P12_FILE_PATH}" \
         "${INSTANCE_NAME}"
}


if [ "$#" -ne 1 ]
then
  echo "USAGE:"
  echo "- Single instance : ./count-forminstances.sh <appengine-folder>"
  echo "- All instances   : ./count-forminstances.sh --all"
  exit 1
fi

if [ "$1" == "--all" ]; then
    read -p "Are you sure want to fetch all the data ? [yes/no] " answer
    if [ "$answer" == "yes" ]; then
        list=$(find ../../../akvo-flow-server-config -type f -name appengine-web.xml \
        -maxdepth 2 -mindepth 2 \
        -exec echo {} \; 2>/dev/null)
        for APP_LINE in ${list};
        do
        APP_ID=$(grep "<application>" "${APP_LINE}"\
            | sed 's/<.*>\(.*\)<.*>/\1/' \
            | sed 's/\ //g')
        getforminstancecount
        done;
        MERGED_CSV="/tmp/all-form-instance-counts.csv"
        [ -e $MERGED_CSV ] && rm $MERGED_CSV
        echo "Instance Name, Form ID, Form Name, Survey Name, Total Form Instances, Last Submission Date, Path" > /tmp/all-form-instance-counts.csv
        awk 'FNR > 1' /tmp/form-instance-counts_2scale.csv >> /tmp/all-form-instance-counts.csv
    fi
    exit 1
else
    APP_ID=$1
    getforminstancecount
fi

