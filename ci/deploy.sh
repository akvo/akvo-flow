#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

if [[ "${TRAVIS_BRANCH}" != "develop" ]] && [[ "${TRAVIS_BRANCH:0:8}" != "release/" ]] && [[ -z "${TRAVIS_TAG}" ]]; then
  exit 0
fi

if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    exit 0
fi

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
release_project_id="${RELEASE_PROJECT_ID:=akvoflow-uat1}"

project_id="${develop_project_id}"

if [[ "${TRAVIS_BRANCH:0:8}" == "release/" ]] || [[ ! -z "${TRAVIS_TAG}" ]]; then
    project_id="${release_project_id}"
fi

curl -s -o ./ci/akvoflow-uat1.p12 \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/akvoflow-uat1/akvoflow-uat1.p12"

curl -s -o ./ci/akvoflow-uat1.json \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

docker run \
    --rm \
    --volume "${HOME}/.m2:/root/.m2:delegated" \
    --volume "${HOME}/.m2:/home/akvo/.m2:delegated" \
    --volume "$(pwd):/app/src:delegated" \
    --env GH_USER \
    --env GH_TOKEN \
    --env CONFIG_REPO \
    --env SERVICE_ACCOUNT_ID \
    --env "PROJECT_ID=${project_id}" \
    --entrypoint /app/src/ci/run-as-user.sh \
    akvo/flow-builder /app/src/ci/mvn-deploy.sh
