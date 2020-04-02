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

SRC_DIR="/app/src"

cd "${SRC_DIR}/Dashboard"

npm ci
npm rebuild node-sass
npm run lint:quiet
npm run build:prod

cd "${SRC_DIR}/Dashboard/app/cljs"

lein build

cd "${SRC_DIR}/GAE"

mvn package

# Temporary disable publishing to clojars
#if [[ "${CI_BRANCH}" != "master" ]] && [[ -z "$CI_TAG" ]]; then
if [[ -z "$CI_TAG" ]]; then
  exit 0
fi

echo "Setting project version to $FLOW_GIT_VERSION"
mvn versions:set -DnewVersion="${FLOW_GIT_VERSION}"

mvn deploy:deploy-file -s "${SRC_DIR}/maven-ci-settings.xml" \
                       -Durl="https://clojars.org/repo" \
                       -DrepositoryId=clojars \
                       -Dfile=target/akvo-flow-classes.jar \
                       -DpomFile=pom.xml \
                       -Dpackaging=jar \
                       -Dclassifier=classes
