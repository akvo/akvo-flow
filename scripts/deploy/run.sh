#!/usr/bin/env bash

set -euo pipefail

CLOUD_SDK_VERSION="${CLOUD_SDK_VERSION:=272.0.0}"

if [[ "$#" -lt 2 ]]; then
    echo "Usage: ./scripts/deploy/run.sh <version> [all | <instance-id-1> <instance-id-2> ... <instance-id-n>]"
    exit 1
fi

if [[ ! -d "tmp" ]]; then
    mkdir "tmp"
fi

docker run --rm \
       --volume "$(pwd):/akvo-flow:delegated" \
       --volume "$(pwd)/tmp:/tmp:delegated" \
       --workdir "/akvo-flow" \
       --env FLOW_GH_TOKEN \
       --env FLOW_CONFIG_REPO \
       "google/cloud-sdk:${CLOUD_SDK_VERSION}-alpine" \
       "/akvo-flow/scripts/deploy/bootstrap-deploy.sh" "$@"
