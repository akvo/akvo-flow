#!/usr/bin/env bash

set -euo pipefail

CLOUD_SDK_VERSION="${CLOUD_SDK_VERSION:=196.0.0}"

docker run --rm \
       --interactive \
       --tty \
       --volume "$(pwd):/akvo-flow" \
       --workdir "/akvo-flow" \
       "google/cloud-sdk:${CLOUD_SDK_VERSION}" \
       "/akvo-flow/scripts/deploy/deploy.sh" "$@"
