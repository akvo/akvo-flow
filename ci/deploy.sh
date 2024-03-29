#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
FLOW_VERSION=${FLOW_VERSION:-${CI_COMMIT}}

project_id="${develop_project_id}"

curl --location --silent --output ci/akvoflow-uat1.json \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

[[ ! -f "ci/akvoflow-uat1.json" ]] && { echo "Credentials file [ci/akvoflow-uat1.json] doesn't exist"; exit 1;}

docker run \
    --rm \
    --volume "${HOME}/.m2:/root/.m2:delegated" \
    --volume "${HOME}/.m2:/home/akvo/.m2:delegated" \
    --volume "${HOME}/.cache:/home/root/.cache:delegated"\
    --volume "${HOME}/.cache:/home/akvo/.cache:delegated" \
    --volume "$(pwd):/app/src:delegated" \
    --env FLOW_GH_TOKEN \
    --env FLOW_CONFIG_REPO \
    --env "PROJECT_ID=${project_id}" \
    --entrypoint /app/src/ci/run-as-user.sh \
    akvo/akvo-flow-builder:20210108.190049.80f674e /app/src/ci/mvn-deploy.sh "$FLOW_VERSION"
