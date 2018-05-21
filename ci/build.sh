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

set -e

SRC_DIR="/app/src"
BUNDLE_GEMFILE="${SRC_DIR}/Dashboard/Gemfile"
RAKEP_MODE=production

export BUNDLE_GEMFILE
export RAKEP_MODE

cd "${SRC_DIR}/Dashboard"

bundle exec rake build --trace

cd "${SRC_DIR}/Dashboard/app/cljs"

lein build

cd "${SRC_DIR}/GAE"

mvn package

if ! [[ -z "$TRAVIS_TAG" ]]; then

    gpg --batch --passphrase ${CLOJARS_GPG_PASSWORD} --import "$SRC_DIR/devops.asc"

    echo "Setting project version to $FLOW_GIT_VERSION"
    mvn versions:set -DnewVersion=${FLOW_GIT_VERSION}

    mvn deploy:deploy-file -s "$SRC_DIR/maven-ci-settings.xml" \
                           -Dgpg.passphrase=${CLOJARS_GPG_PASSWORD} \
                           -Durl="https://clojars.org/repo" \
                           -DrepositoryId=clojars \
                           -Dfile=target/akvo-flow-classes.jar \
                           -DpomFile=pom.xml \
                           -Dpackaging=jar \
                           -Dclassifier=classes

fi