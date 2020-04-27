#!/usr/bin/env bash

#
#  Copyright (C) 2017-2020 Stichting Akvo (Akvo Foundation)
#
#  This file is part of Akvo FLOW.
#
#  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
#  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#  either version 3 of the License or any later version.
#
#  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License included below for more details.
#
#  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
#

set -eu

MAVEN_REPO="$HOME/.m2"
NPM_CACHE="$HOME/.npm"

if [[ ! -d "$MAVEN_REPO" ]]; then
    mkdir "$MAVEN_REPO"
fi

if [[ ! -d "$NPM_CACHE" ]]; then
    mkdir "$NPM_CACHE"
fi

FLOW_GIT_VERSION=$(git describe)
CI_BRANCH="${SEMAPHORE_GIT_BRANCH:=}"
CI_TAG="${SEMAPHORE_GIT_TAG_NAME:=}"

docker run \
       --rm \
       -e CLOJARS_PASSWORD="${CLOJARS_PASSWORD:=}" \
       -e FLOW_GIT_VERSION="${FLOW_GIT_VERSION}" \
       -e CI_BRANCH="${CI_BRANCH}" \
       -e CI_TAG="${CI_TAG}" \
       --volume "${MAVEN_REPO}:/root/.m2:delegated" \
       --volume "${MAVEN_REPO}:/home/akvo/.m2:delegated" \
       --volume "${NPM_CACHE}:/root/.npm:delegated" \
       --volume "${NPM_CACHE}:/home/akvo/.npm:delegated" \
       --volume "$(pwd):/app/src:delegated" \
       --entrypoint /app/src/ci/run-as-user.sh \
       akvo/akvo-flow-builder:20200427.052642.da8c2ee "$@"
