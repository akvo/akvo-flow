#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

if [[ "$#" -lt 3 ]]; then
    echo "Usage: ./scripts/deploy/run.sh <version> [flip|deploy] [all | <instance-id-1> <instance-id-2> ... <instance-id-n>]"
    exit 1
fi

function log {
   echo "$(date +"%T") - INFO - $*"
}

version="${1}"                # <version> as $1
export version

action="${2}"
export action

shift 2                       # $@ rest of instances

deploy_id="${version}-$(date +%s)"
tmp="/tmp/${deploy_id}"

mkdir -p "${tmp}"

# Move to tmp folder and work there
cd "${tmp}"

deploy_bucket_name="akvoflowsandbox-deployment"
export deploy_bucket_name

log Obtaining instances config folder ...

curl --silent --location \
     --header "Accept: application/json" \
     --header "Authorization: token ${FLOW_GH_TOKEN}" \
     --output afsc.tar.gz \
     "https://api.github.com/repos/akvo/${FLOW_CONFIG_REPO}/tarball/"

config="flow-config"
mkdir "${config}"
tar xfz afsc.tar.gz --strip-components=1 --directory="${config}"
export config

log Authenticating

gcloud auth activate-service-account --key-file="${config}/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

if [[ "${1}" == "all" ]]; then

    find "${config}" -name 'appengine-web.xml' | awk -F'/' '{print $2}' > instances.txt
    find "${config}" -name '.skip-deployment' | awk -F'/' '{print $2}' > skip.txt

    cat < skip.txt |  while IFS= read -r line
    do
	sed -i "/$line/d" instances.txt
    done
else
    printf "%s\n" "$@" > instances.txt
fi

deploy_instance() {
    instance_id="${1}"
    staging_dir="appengine-staging-${1}"

    echo "Copying staging dir to ${staging_dir}"
    cp -r appengine-staging "${staging_dir}"

    echo "Deploying ${instance_id} from ${staging_dir}"

    cp "${config}/${instance_id}/appengine-web.xml" "${staging_dir}/WEB-INF/appengine-web.xml"

    gcloud app deploy "${staging_dir}/app.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/queue.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/index.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/cron.yaml" \
	   --no-promote --quiet \
	   --version="${version}" \
	   --project="${instance_id}"
}
export -f deploy_instance

migrate_traffic() {
    gcloud app services set-traffic default \
	   --splits "${version}"=1 \
	   --project="${1}"
}
export -f migrate_traffic

if [[ "${action}" == "flip" ]]; then
    deploy_fn="migrate_traffic"
else
    deploy_fn="deploy_instance"

    log Obtaining version archive
    gsutil cp "gs://${deploy_bucket_name}/${version}.zip" "${version}.zip"
    unzip -q "${version}.zip"

    if [[ ! -d "appengine-staging" ]]; then
	log Staging folder is not present
	exit 1
    fi
fi

log "Deploying instances: $*"

parallel --results "${tmp}/parallel" \
	 --retries 3 \
	 --jobs 10 \
	 --joblog "${deploy_id}.log" \
	 "${deploy_fn}" :::: instances.txt

log Deploy results

cat "${tmp}/${deploy_id}.log"

log Uploading results

results_archive="${deploy_id}.zip"
zip -q -r "${results_archive}" "${tmp}/${deploy_id}.log" "${tmp}/parallel"
gsutil cp "${results_archive}" "gs://${deploy_bucket_name}/${results_archive}"

log Done
