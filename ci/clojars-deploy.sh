#!/usr/bin/env bash
#shellcheck disable=SC2039

set -eu

SRC_DIR="/app/src"

cd "${SRC_DIR}/GAE"

echo "Setting project version to $FLOW_GIT_VERSION"
mvn versions:set -DnewVersion="${FLOW_GIT_VERSION}"

mvn deploy:deploy-file -s "${SRC_DIR}/maven-ci-settings.xml" \
                       -Durl="https://clojars.org/repo" \
                       -DrepositoryId=clojars \
                       -Dfile=target/akvo-flow-classes.jar \
                       -DpomFile=pom.xml \
                       -Dpackaging=jar \
                       -Dclassifier=classes
