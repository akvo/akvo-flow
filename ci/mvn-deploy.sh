#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

cd /app/src/GAE

# shellcheck source=ci/deploy-guards.sh
. /app/src/ci/deploy-guards.sh

require_commands curl gcloud gsutil mvn zip

# This container runs as root, so everything Maven and gcloud write into the bind-mounted
# working tree would otherwise be left root-owned for whoever runs next -- the developer
# on a local deploy, or a later CI step. Hand it back on the way out however we exit.
if [[ -n "${HOST_UID:-}" && -n "${HOST_GID:-}" ]]; then
    trap 'chown -R "${HOST_UID}:${HOST_GID}" /app/src/GAE/target 2>/dev/null || true' EXIT
fi

gcloud auth activate-service-account --key-file=/app/src/ci/akvoflow-uat1.json
gcloud config set project "${PROJECT_ID}"
gcloud config set compute/zone europe-west1-d

log Requesting "${PROJECT_ID}" config

descriptor="./target/akvo-flow/WEB-INF/appengine-web.xml"

# --fail earns its place here. Without it curl writes GitHub's own "404: Not Found" body
# into the descriptor and exits successfully, so the file exists, the check below passes,
# and the mistake only surfaces much later as a baffling staging error.
curl --location --silent --fail --output "${descriptor}" \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/${PROJECT_ID}/appengine-web.xml" \
    || { echo "Could not fetch appengine-web.xml for ${PROJECT_ID} from ${FLOW_CONFIG_REPO}" >&2; exit 1; }

assert_gen2_descriptor "${descriptor}" "${PROJECT_ID}"

log Staging app

mvn appengine:stage

version=$1
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
