#!/usr/bin/env bash

set -eu

function log {
   echo "$(date +"%T") - INFO - $*"
}

#if [[ "${TRAVIS_BRANCH}" != "develop" ]]; then
#    exit 0
#fi

#if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
#    exit 0
#fi

openssl aes-256-cbc -K $encrypted_ac356ff71e5e_key -iv $encrypted_ac356ff71e5e_iv \
	-in ci/akvoflow-uat1.p12.enc -out ci/akvoflow-uat1.p12 -d

log Authentication with gcloud
docker run -ti google/cloud-sdk:latest gcloud version

#openssl aes-256-cbc -K $encrypted_13abf95e958f_key -iv $encrypted_13abf95e958f_iv \
#	-in ci/gcloud-service-account.json.enc -out ci/gcloud-service-account.json -d

#gcloud auth activate-service-account --key-file ci/gcloud-service-account.json
#gcloud config set project akvoflow-uat1
#gcloud config set container/cluster europe-west1-d
#gcloud config set compute/zone europe-west1-d
