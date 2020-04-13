#!/usr/bin/env bash

set -euo pipefail

CI_BRANCH="${SEMAPHORE_GIT_BRANCH:=}"
CI_TAG="${SEMAPHORE_GIT_TAG_NAME:=}"
CI_PULL_REQUEST="${SEMAPHORE_GIT_PR_NAME:=}"

function log {
   echo "$(date +"%T") - INFO - $*"
}

if [[ "${CI_BRANCH}" != "master" ]] && [[ -z "${CI_TAG}" ]]; then
   exit 0
fi

if [[ -n "${CI_PULL_REQUEST}" ]]; then
    exit 0
fi

if [[ "${CI_TAG:0:8}" == "promote-" ]]; then
    echo "Skipping deployment"
    exit 0
fi

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
release_project_id="${RELEASE_PROJECT_ID:=akvoflow-uat1}"

project_id="${develop_project_id}"

if [[ -n "${CI_TAG}" ]]; then
    project_id="${release_project_id}"
fi

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
    akvo/akvo-flow-builder:20200115.154607.012ea0b /app/src/ci/mvn-deploy.sh
