#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

# Resolved before the cd to the temporary working directory further down.
script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=scripts/deploy/platform-settings.sh
source "${script_dir}/platform-settings.sh"

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

    # Whole-line comparison. `sed -i "/$line/d"` deleted on a substring, so a
    # marker for akvoflow-37 also removed akvoflow-370 -- and removal here is
    # exactly the silent kind: the instance simply never appears in the fan-out,
    # and nothing downstream can tell it was meant to.
    #
    # Harmless while the fleet stops at 231, but the markers now include
    # two-digit instances and the numbering only climbs.
    # Guarded because an empty skip.txt would otherwise empty the fan-out: with
    # no records read from the first file, NR==FNR is still true for the first
    # line of the second, and awk starts filing instances as skips.
    if [[ -s skip.txt ]]; then
        awk 'NR==FNR { skip[$0]; next } !($0 in skip)' skip.txt instances.txt > instances.kept
        mv instances.kept instances.txt
    fi
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

    # The copy above only reaches the Java runtime. gcloud reads scaling from
    # app.yaml, which was generated from UAT2's descriptor, so it needs the
    # instance's own values written into it. Explicitly guarded: this function
    # runs under parallel, which does not inherit set -e.
    apply_platform_settings "${instance_id}" \
	   "${config}/${instance_id}/appengine-web.xml" \
	   "${staging_dir}/app.yaml" || return 1

    gcloud app deploy "${staging_dir}/app.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/queue.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/index.yaml" \
	   "${staging_dir}/WEB-INF/appengine-generated/cron.yaml" \
	   --no-promote --quiet \
	   --version="${version}" \
	   --project="${instance_id}"
}
export -f deploy_instance
export -f apply_platform_settings

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
