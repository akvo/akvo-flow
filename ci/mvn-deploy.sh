#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

cd /app/src/GAE

gcloud auth activate-service-account "${SERVICE_ACCOUNT_ID}" --key-file=/app/src/ci/akvoflow-uat1.p12
gcloud config set project "${PROJECT_ID}"
gcloud config set compute/zone europe-west1-d

version=$(git describe)

log Staging app

mvn appengine:stage

log Requesting "${PROJECT_ID}" config

curl -s -o ./target/appengine-staging/WEB-INF/appengine-web.xml \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/${PROJECT_ID}/appengine-web.xml"

sed -i "s/__VERSION__/${version}/" ./target/appengine-staging/admin/js/app.js

log Deploying version 1

java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
     com.google.appengine.tools.admin.AppCfg \
     --service_account_json_key_file=/app/src/ci/akvoflow-uat1.json \
     --application="${PROJECT_ID}" \
     update ./target/appengine-staging

log Deploying backend dataprocessor

java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
     com.google.appengine.tools.admin.AppCfg \
     --service_account_json_key_file=/app/src/ci/akvoflow-uat1.json \
     --application="${PROJECT_ID}" \
     backends update ./target/appengine-staging

archive_name="${version}.zip"
(
    cd target
    rm -rf appengine-staging/WEB-INF/appengine-web.xml
    zip "${archive_name}" -r appengine-staging/*
)

gsutil cp "target/${archive_name}" "gs://akvoflowsandbox-deployment/${archive_name}"
