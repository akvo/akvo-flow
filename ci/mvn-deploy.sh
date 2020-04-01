#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

cd /app/src/GAE

gcloud auth activate-service-account --key-file=/app/src/ci/akvoflow-uat1-.json
gcloud config set project "${PROJECT_ID}"
gcloud config set compute/zone europe-west1-d

log Requesting "${PROJECT_ID}" config

curl --location --silent --output ./target/akvo-flow/WEB-INF/appengine-web.xml \
     -h "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/${PROJECT_ID}/appengine-web.xml"

log Staging app

mvn appengine:stage

version=$(git describe)
log Deploying version "${version}"

(
    cd "./target/appengine-staging"
    gcloud app deploy app.yaml \
	   WEB-INF/appengine-generated/queue.yaml \
	   WEB-INF/appengine-generated/index.yaml \
	   WEB-INF/appengine-generated/cron.yaml \
	   --promote --quiet --version="${version}" \
	   --project="${PROJECT_ID}"
)

archive_name="${version}.zip"
(
    cd target
    rm -rf appengine-staging/WEB-INF/appengine-web.xml
    zip "${archive_name}" -q -r appengine-staging/*
)

gsutil cp "target/${archive_name}" "gs://akvoflowsandbox-deployment/${archive_name}"
