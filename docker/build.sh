#!/usr/bin/env bash

#
#  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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
APP_ENGINE_SDK_VERSION="1.9.54"
APP_ENGINE_SDK_FILE="appengine-java-sdk-1.9.54.zip"

if [[ ! -d "$HOME/.cache/appengine-java-sdk-$APP_ENGINE_SDK_VERSION" ]]; then
    unzip "$HOME/.cache/$APP_ENGINE_SDK_FILE" -d "$HOME/.cache"
fi

cd "$SRC_DIR/Dashboard"

RAKEP_MODE=production bundle exec rake build --trace

cd "$SRC_DIR/Dashboard/app/cljs"

lein build

cd "$SRC_DIR/GAE"

cp -f build.properties.template build.properties

sed -i "s|^sdk\.dir=.*|sdk\.dir=$HOME/.cache/appengine-java-sdk-$APP_ENGINE_SDK_VERSION|" build.properties


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