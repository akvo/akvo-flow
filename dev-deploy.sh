#!/usr/bin/env bash

# see README at root of the project
# usage: build with `docker-compose up --build -d && docker-compose logs -f
# make sure FLOW_GH_TOKEN is setup
# run with `docker-compose exec -u akvo akvo-flow ./dev-deploy.sh akvoflowsandbox $FLOW_GH_TOKEN`

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

FLOW_VERSION=$(git describe)
FLOW_CONFIG_REPO=akvo-flow-server-config
PROJECT_ID=${1}
FLOW_GH_TOKEN=${2}

log Will deploy version "${FLOW_VERSION}" to "${PROJECT_ID}"

curl --location --silent --output ci/akvoflow-uat1.json \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

gcloud auth activate-service-account --key-file=ci/akvoflow-uat1.json

cd GAE

mvn appengine:stage

[[ ! -f "./target/akvo-flow/WEB-INF/appengine-web.xml" ]] && { echo "Required appengine-web.xml not found"; exit 1; }

(
    cd "./target/appengine-staging"
    gcloud app deploy app.yaml \
	   WEB-INF/appengine-generated/queue.yaml \
	   WEB-INF/appengine-generated/index.yaml \
	   WEB-INF/appengine-generated/cron.yaml \
	   --promote --quiet --version="${FLOW_VERSION}" \
	   --project="${PROJECT_ID}"
)
