#!/usr/bin/env bash

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

cd /app/src/GAE

# Everything this script shells out to. These used to fail at the point of use, which is
# a poor place to find out: zip is on the last line, so a missing zip meant a full deploy
# to production had already happened before anything complained. A second of checking
# here turns that into an immediate, named failure.
require_commands() {
    local missing=()
    local c
    for c in "$@"; do
	command -v "${c}" >/dev/null 2>&1 || missing+=("${c}")
    done
    if [[ ${#missing[@]} -gt 0 ]]; then
	echo "Missing required command(s): ${missing[*]}" >&2
	echo "They have to be provided by the deploy image, ci/Dockerfile.gae-deploy" >&2
	exit 1
    fi
}

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

[[ -s "${descriptor}" ]] || { echo "Fetched appengine-web.xml is empty" >&2; exit 1; }
grep -q "<appengine-web-app" "${descriptor}" \
    || { echo "Fetched file is not an appengine-web.xml descriptor" >&2; exit 1; }

# The only thing tying this repository to akvo-flow-server-config is the URL above, and
# nothing notices when the two disagree. This build carries App Engine SDK 2.x and the
# DataNucleus fix for JVM 9 and later, so it can only run on a second-generation runtime,
# and java8 does not accept deployments at all any more. Merging the two repositories in
# the wrong order used to produce several minutes of work and then an opaque failure;
# now it says which repository is behind.
if grep -q "<runtime>java8</runtime>" "${descriptor}"; then
    echo "${PROJECT_ID} still declares <runtime>java8</runtime>." >&2
    echo "This build only supports the second generation runtime. Update" >&2
    echo "${PROJECT_ID}/appengine-web.xml on master of ${FLOW_CONFIG_REPO} first." >&2
    exit 1
fi

grep -q "<app-engine-apis>true</app-engine-apis>" "${descriptor}" \
    || { echo "${PROJECT_ID}/appengine-web.xml is missing <app-engine-apis>true</app-engine-apis>; Flow would deploy and then fail at its first datastore call" >&2; exit 1; }

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
