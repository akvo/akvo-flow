#!/usr/bin/env bash

set -euo pipefail

CLOUD_SDK_VERSION="${CLOUD_SDK_VERSION:=198.0.0}"

if [[ ! -d "tmp" ]]; then
    mkdir "tmp"
fi

docker run --rm \
       --interactive \
       --tty \
       --volume "$(pwd):/akvo-flow:delegated" \
       --volume "$(pwd)/tmp:/tmp:delegated" \
       --workdir "/akvo-flow" \
       --env GH_USER \
       --env GH_TOKEN \
       --env CRON_UPDATE \
       --env INDEX_UPDATE \
       --env QUEUE_UPDATE \
       "google/cloud-sdk:${CLOUD_SDK_VERSION}-alpine" \
       "/akvo-flow/scripts/deploy/bootstrap-deploy.sh" "$@"
