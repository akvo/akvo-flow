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

log Making sure gcloud and app-engine-java are installed and up to date

gcloud components install app-engine-java
gcloud components update
gcloud version

log Authentication with gcloud

openssl aes-256-cbc -K $encrypted_ac356ff71e5e_key -iv $encrypted_ac356ff71e5e_iv \
	-in ci/akvoflow-uat1.p12.enc -out ci/akvoflow-uat1.p12 -d

gcloud auth activate-service-account --key-file=ci/akvoflow-uat1.p12
gcloud config set project akvoflow-uat1
gcloud config set compute/zone europe-west1-d
