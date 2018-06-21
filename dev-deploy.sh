#!/usr/bin/env bash

set -eu

cd GAE

mvn appengine:stage

java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
     com.google.appengine.tools.admin.AppCfg \
     update ./target/appengine-staging

java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
     com.google.appengine.tools.admin.AppCfg \
     backends update ./target/appengine-staging
