#!/usr/bin/env bash
#
# Deploy the current working tree to a tenant.
#
#   ./switch_tenant.sh akvoflowsandbox      # pick which tenant's config to deploy with
#   ./dev-deploy.sh akvoflowsandbox "$FLOW_GH_TOKEN"
#
# Run this from the host, not from inside the dev container. It used to be the other way
# around, and it cannot be any more: the dev container is akvo/akvo-flow-builder, whose
# Cloud SDK dates from 2020 and stages through the legacy AppCfg tool. That tool requires
# a <threadsafe> element, which the second generation runtime rejects, so it can no longer
# stage this application at all. Staging now happens in ci/Dockerfile.gae-deploy, the same
# image CI uses, which this script builds and runs for you.
#
# The dev container is otherwise unchanged and still the place the application runs while
# you work on it.

set -euo pipefail

function log {
   echo "$(date +"%T") - INFO - $*"
}

# The script runs twice: once on the host to set the container up, and once inside it to
# do the work. This flag is how the second invocation knows which half it is.
if [[ -z "${FLOW_DEPLOY_INNER:-}" ]]; then

    if [[ "$#" -lt 2 ]]; then
	echo "Usage: ./dev-deploy.sh <tenant-project-id> <github-token>" >&2
	echo "Run ./switch_tenant.sh <tenant> first to choose the config to deploy with." >&2
	exit 1
    fi

    PROJECT_ID="${1}"
    FLOW_GH_TOKEN="${2}"
    DEPLOY_IMAGE="akvo-flow-gae-deploy:local"

    # Read on the host, where the repository and its git metadata actually are, so the
    # container needs neither git nor an opinion about repository ownership.
    FLOW_VERSION="$(git describe)"

    log Will deploy version "${FLOW_VERSION}" to "${PROJECT_ID}"

    log Building the deploy image
    docker build --quiet --tag "${DEPLOY_IMAGE}" - < ci/Dockerfile.gae-deploy

    docker run --rm \
	   --volume "${HOME}/.m2:/root/.m2:delegated" \
	   --volume "$(pwd):/app/src:delegated" \
	   --env FLOW_DEPLOY_INNER=1 \
	   --env "PROJECT_ID=${PROJECT_ID}" \
	   --env "FLOW_GH_TOKEN=${FLOW_GH_TOKEN}" \
	   --env "FLOW_VERSION=${FLOW_VERSION}" \
	   --env "HOST_UID=$(id -u)" \
	   --env "HOST_GID=$(id -g)" \
	   "${DEPLOY_IMAGE}" /app/src/dev-deploy.sh

    exit 0
fi

# ---------------------------------------------------------------------------
# Everything below runs inside the deploy image.
# ---------------------------------------------------------------------------

cd /app/src

# shellcheck source=ci/deploy-guards.sh
. /app/src/ci/deploy-guards.sh

# No zip or gsutil here: unlike CI, a developer deploy does not publish the archive that
# the other instances are deployed from.
require_commands curl gcloud mvn

# The container runs as root, so hand the build output back on the way out rather than
# leaving root-owned files in someone's working tree.
if [[ -n "${HOST_UID:-}" && -n "${HOST_GID:-}" ]]; then
    trap 'chown -R "${HOST_UID}:${HOST_GID}" /app/src/GAE/target 2>/dev/null || true' EXIT
fi

# switch_tenant.sh put this here. It is the cheapest thing that can be wrong and needs no
# network, so it goes first: choosing a tenant that has not been migrated should fail
# immediately and by name, not after fetching credentials and staging the application.
assert_gen2_descriptor "GAE/target/akvo-flow/WEB-INF/appengine-web.xml" "${PROJECT_ID}"

FLOW_CONFIG_REPO="${FLOW_CONFIG_REPO:-akvo-flow-server-config}"

curl --location --silent --fail --output ci/akvoflow-uat1.json \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     "https://raw.githubusercontent.com/akvo/${FLOW_CONFIG_REPO}/master/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json" \
    || { echo "Could not fetch the deploy credentials; is FLOW_GH_TOKEN valid?" >&2; exit 1; }

gcloud auth activate-service-account --key-file=ci/akvoflow-uat1.json

cd GAE

log Staging app

mvn appengine:stage

log Deploying version "${FLOW_VERSION}"

(
    cd "./target/appengine-staging"
    gcloud app deploy app.yaml \
	   WEB-INF/appengine-generated/queue.yaml \
	   WEB-INF/appengine-generated/index.yaml \
	   WEB-INF/appengine-generated/cron.yaml \
	   --promote --quiet --version="${FLOW_VERSION}" \
	   --project="${PROJECT_ID}"
)
