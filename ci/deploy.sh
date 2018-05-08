#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

#if [[ "${TRAVIS_BRANCH}" != "develop" ]] && [[ "${TRAVIS_BRANCH:0:8}" != "release/" ]]; then
#  exit 0
#fi

if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    exit 0
fi

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
release_project_id="${RELEASE_PROJECT_ID:=akvoflow-uat1}"

project_id="${develop_project_id}"

if [[ "${TRAVIS_BRANCH:0:8}" == "release/" ]]; then
    project_id="${release_project_id}"
fi

# shellcheck disable=SC2154
openssl aes-256-cbc -K "$encrypted_ac356ff71e5e_key" -iv "$encrypted_ac356ff71e5e_iv" \
	-in ci/akvoflow-uat1.p12.enc -out ci/akvoflow-uat1.p12 -d

docker run \
    --rm \
    --volume "${HOME}/.m2:/home/akvo/.m2:delegated" \
    --volume "$(pwd):/app/src:delegated" \
    --env GH_USER \
    --env GH_TOKEN \
    --env CONFIG_REPO \
    --env SERVICE_ACCOUNT_ID \
    --env "PROJECT_ID=${project_id}" \
    --entrypoint /app/src/ci/run-as-user.sh \
    akvo/flow-maven-build /app/src/ci/mvn-deploy.sh
