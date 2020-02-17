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

curl --location --silent --output ./target/akvo-flow/WEB-INF/appengine-web.xml \
     "https://${GH_USER}:${GH_TOKEN}@raw.githubusercontent.com/akvo/${CONFIG_REPO}/master/${PROJECT_ID}/appengine-web.xml"

log Staging app

mvn appengine:stage

log Deploying version 1

(
    cd "./target/appengine-staging"
    gcloud app deploy app.yaml \
	   WEB-INF/appengine-generated/queue.yaml \
	   WEB-INF/appengine-generated/index.yaml \
	   WEB-INF/appengine-generated/cron.yaml \
	   --promote --quiet --version=1 --project="${PROJECT_ID}"
)

version=$(git describe)
archive_name="${version}.zip"
(
    cd target
    rm -rf appengine-staging/WEB-INF/appengine-web.xml
    zip "${archive_name}" -r appengine-staging/*
)

gsutil cp "target/${archive_name}" "gs://akvoflowsandbox-deployment/${archive_name}"
