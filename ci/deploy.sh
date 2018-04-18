#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

if [[ "${TRAVIS_BRANCH}" != "develop" ]] && [[ "${TRAVIS_BRANCH:0:8}" != "release/" ]]; then
  exit 0
fi

if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    exit 0
fi

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
release_project_id="${RELEASE_PROJECT_ID:=akvoflow-uat1}"

project_id="${develop_project_id}"

if [[ "${TRAVIS_BRANCH:0:8}" == "release/" ]]; then
    project_id="${release_project_id}"
fi

log Making sure gcloud and app-engine-java are installed and up to date

gcloud components install app-engine-java
gcloud components update
gcloud version
which gcloud

log Authentication with gcloud

# shellcheck disable=SC2154
openssl aes-256-cbc -K "$encrypted_ac356ff71e5e_key" -iv "$encrypted_ac356ff71e5e_iv" \
	-in ci/akvoflow-uat1.p12.enc -out ci/akvoflow-uat1.p12 -d


gcloud auth activate-service-account "${SERVICE_ACCOUNT_ID}" --key-file=ci/akvoflow-uat1.p12
gcloud config set project "${project_id}"
gcloud config set compute/zone europe-west1-d

log Requesting "${project_id}" config

curl -s -o GAE/target/akvo-flow/WEB-INF/appengine-web.xml \
     "https://$GH_USER:$GH_TOKEN@raw.githubusercontent.com/akvo/$CONFIG_REPO/master/${project_id}/appengine-web.xml"

log Update __VERSION__

version=$(git describe)
sed -i "s/__VERSION__/${version}/" GAE/target/akvo-flow/admin/js/app.js

log Updating default service version 1

gcloud app deploy GAE/target/akvo-flow/WEB-INF/appengine-web.xml --promote --version=1

log Updating default service version dataprocessor

gcloud app deploy GAE/target/akvo-flow/WEB-INF/appengine-web.xml --no-promote --version=dataprocessor
