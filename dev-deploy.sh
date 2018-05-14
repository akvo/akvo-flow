#!/usr/bin/env bash

set -eu

gcloud auth login --force

cd GAE

instance_id=$(sed -n 's/\(.*\)<application>\(.*\)<\/application>\(.*\)/\2/p' target/akvo-flow/WEB-INF/appengine-web.xml)

mvn appengine:deploy -Dapp.deploy.project="${instance_id}" -Dapp.deploy.version=1
mvn appengine:deploy -Dapp.deploy.project="${instance_id}" -Dapp.deploy.version=dataprocessor -Dapp.deploy.promote=false

gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/cron.yaml --project="${instance_id}" --quiet
gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/index.yaml --project="${instance_id}" --quiet
gcloud app deploy target/appengine-staging/WEB-INF/appengine-generated/queue.yaml --project="${instance_id}" --quiet
