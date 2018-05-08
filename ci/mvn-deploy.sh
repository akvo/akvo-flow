#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

cd /app/src/GAE

gcloud auth activate-service-account "${SERVICE_ACCOUNT_ID}" --key-file=/app/src/ci/akvoflow-uat1.p12
gcloud config set project "${PROJECT_ID}"
gcloud config set compute/zone europe-west1-d

log Requesting "${PROJECT_ID}" config

curl -s -o GAE/target/akvo-flow/WEB-INF/appengine-web.xml \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/${PROJECT_ID}/appengine-web.xml"

log Update __VERSION__

version=$(git describe)

sed -i "s/__VERSION__/${version}/" target/akvo-flow/admin/js/app.js

log Updating version 1

mvn appengine:deploy -Dapp.deploy.project="${PROJECT_ID}" -Dapp.deploy.version=1

log Version 1 updated

#gsutil cp GAE/target/akvo-flow.war "gs://akvoflowsandbox-deployment/${version}.war"

#log Updating default service version 1

#gcloud app deploy GAE/target/akvo-flow/WEB-INF/appengine-web.xml --promote --version=1

#log Updating default service version dataprocessor

#gcloud app deploy GAE/target/akvo-flow/WEB-INF/appengine-web.xml --no-promote --version=dataprocessor
