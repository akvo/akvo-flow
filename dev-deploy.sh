#!/usr/bin/env bash

set -eu

cd GAE

mvn appengine:stage

staging_lib="./target/appengine-staging/WEB-INF/lib"
if [[ ! -f "${staging_lib}/appengine-api-1.0-sdk-1.9.63.jar" ]]; then
    cp -v ./target/akvo-flow/WEB-INF/lib/appengine-api-1.0-sdk-1.9.63.jar ${staging_lib}
fi

java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
     com.google.appengine.tools.admin.AppCfg \
     update ./target/appengine-staging
