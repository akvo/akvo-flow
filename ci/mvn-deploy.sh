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

curl -s -o ./target/akvo-flow/WEB-INF/appengine-web.xml \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/${PROJECT_ID}/appengine-web.xml"

log Update __VERSION__

version=$(git describe)

sed -i "s/__VERSION__/${version}/" ./target/akvo-flow/admin/js/app.js

log Updating version 1

mvn appengine:deploy -Dapp.deploy.project="${PROJECT_ID}" -Dapp.deploy.version=1

log Updating cron, index, queue

gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/cron.yaml --quiet
gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/index.yaml --quiet
gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/queue.yaml --quiet

log Uploading artifacts

archive_name="${version}.zip"
(
  cd target
  zip "${archive_name}" -r appengine-staging/*
)

gsutil cp "target/${archive_name}" "gs://akvoflowsandbox-deployment/${archive_name}"

log Updating dataprocessor

mvn appengine:deploy -Dapp.deploy.project="${PROJECT_ID}" -Dapp.deploy.version=dataprocessor -Dapp.deploy.promote=false
