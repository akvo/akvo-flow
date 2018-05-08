#!/usr/bin/env bash

#
#  Copyright (C) 2017-2018 Stichting Akvo (Akvo Foundation)
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

if [[ ! -d "$MAVEN_REPO" ]]; then
    mkdir "$MAVEN_REPO"
fi

docker run \
       --rm \
       --env "HOST_GID=$(id -g)" \
       --env "HOST_UID=$(id -u)" \
       --env "HOST_USER=${USER}" \
       --volume "${MAVEN_REPO}:/home/$USER/.m2" \
       --volume "$(pwd):/app/src" \
       --entrypoint /app/src/ci/startup.sh \
       --interactive --tty \
       akvo/flow-maven-build "$@"
