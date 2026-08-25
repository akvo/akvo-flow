#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

develop_project_id="${DEVELOP_PROJECT_ID:=akvoflow-uat2}"
FLOW_VERSION=${FLOW_VERSION:-${CI_COMMIT}}

project_id="${develop_project_id}"

DEPLOY_IMAGE="akvo-flow-gae-deploy:local"

curl --location --silent --output ci/akvoflow-uat1.json \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

[[ ! -f "ci/akvoflow-uat1.json" ]] && { echo "Credentials file [ci/akvoflow-uat1.json] doesn't exist"; exit 1;}

# Staging and deploying needs a Cloud SDK that understands the gen2 runtimes, which the
# 2021 builder image predates. The image is built here rather than pulled so that nothing
# has to be published to Docker Hub; it has no build context and its layers cache, so a
# rebuild is cheap after the first run.
log Building the deploy image
docker build --tag "${DEPLOY_IMAGE}" --file ci/Dockerfile.gae-deploy - < ci/Dockerfile.gae-deploy

log Staging and deploying

# Runs as root, unlike the Alpine builder image, whose run-as-user.sh relies on su-exec.
# mvn-deploy.sh hands ownership of anything it wrote back afterwards.
docker run \
    --rm \
    --volume "${HOME}/.m2:/root/.m2:delegated" \
    --volume "${HOME}/.cache:/root/.cache:delegated" \
    --volume "$(pwd):/app/src:delegated" \
    --env FLOW_GH_TOKEN \
    --env FLOW_CONFIG_REPO \
    --env "PROJECT_ID=${project_id}" \
    --env "HOST_UID=$(id -u)" \
    --env "HOST_GID=$(id -g)" \
    "${DEPLOY_IMAGE}" /app/src/ci/mvn-deploy.sh "$FLOW_VERSION"
