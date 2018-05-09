#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

yellow='\033[1;33m'
green='\033[0;32m'
nc='\033[0m'

if [[ "$#" -lt 2 ]]; then
    echo "Usage: ./scripts/deploy/run.sh <version> <instance-id-1> <instance-id-2> ... <instance-id-n>"
    exit 1
fi

export version="${1}"         # <version>       as $1
export source_instance="${2}" # <instance-id-1> as $2
shift 2                       # $@ rest of instances


export config_repo="${CONFIG_REPO:=/akvo-flow-server-config}"
export deploy_bucket_name="${deploy_bucket_name:=akvoflowsandbox-deployment}"
export api_root="https://appengine.googleapis.com/v1"

deploy_id="$(date +%s)"
tmp="/tmp/${deploy_id}"
target_dir="${tmp}/akvo-flow"
gh_user="${GH_USER:=''}"
gh_token="${GH_TOKEN:=''}"

CRON_UPDATE="${CRON_UPDATE:=true}"
INDEX_UPDATE="${INDEX_UPDATE:=true}"
QUEUE_UPDATE="${QUEUE_UPDATE:=true}"
export CRON_UPDATE
export INDEX_UPDATE
export QUEUE_UPDATE

# Force login
gcloud auth login --brief --activate --force

akvo_org_account=$( (gcloud auth list --filter="active" 2>&1 | grep -E '^\*.*akvo\.org$') || echo "")

if [[ -z "${akvo_org_account}" ]]; then
    echo >&2 "Unable to detect an akvo.org account for gcloud"
    exit 1
fi

echo "Cloning akvo-flow-server-config..."

if [[ "${gh_user}" != "unknown" ]] && [[ "${gh_token}" != "unknown" ]]; then
    git clone --depth=50 --branch=master \
	"https://${gh_user}:${gh_token}@github.com/akvo/akvo-flow-server-config.git" "${config_repo}" > /dev/null
else
    echo -e "${yellow}WARNING:${nc} If you have 2FA enabled in GitHub, make sure you have a
             ${green}personal access token${nc} available and use it as password"
    echo "Visit for more info: https://blog.github.com/2013-09-03-two-factor-authentication/#how-does-it-work-for-command-line-git"
    git clone --depth=50 --branch=master \
	"https://github.com/akvo/akvo-flow-server-config.git" "${config_repo}" > /dev/null
fi

echo "Deploying to ${source_instance} using gcloud..."

mkdir -p "${tmp}"

# Move to tmp folder and work there
cd "${tmp}"

gsutil cp "gs://${deploy_bucket_name}/${version}.zip" "${version}.zip"
unzip "${version}.zip"

if [[ ! -d "appengine-staging" ]]; then
    echo "Staging folder is not present"
    exit 1
fi

cp -v "${config_repo}/${source_instance}/appengine-web.xml" appengine-staging/WEB-INF/
sed -i "s/__VERSION__/${version}/" appengine-staging/admin/js/app.js

gcloud app deploy appengine-staging/app.yaml \
       --project="${source_instance}" \
       --bucket="gs://${deploy_bucket_name}" \
       --version=1 \
       --promote

if [[ "${CRON_UPDATE}" != "false" ]]; then
    gcloud app deploy appengine-staging/WEB-INF/appengine-generated/cron.yaml \
	   --project="${source_instance}" --quiet
fi

if [[ "${INDEX_UPDATE}" != "false" ]]; then
    gcloud app deploy appengine-staging/WEB-INF/appengine-generated/index.yaml \
	   --project="${source_instance}" --quiet
fi

if [[ "${QUEUE_UPDATE}" != "false" ]]; then
    gcloud app deploy appengine-staging/WEB-INF/appengine-generated/queue.yaml \
	   --project="${source_instance}" --quiet
fi

gcloud app deploy appengine-staging/app.yaml \
       --project="${source_instance}" \
       --bucket="gs://${deploy_bucket_name}" \
       --version=dataprocessor \
       --no-promote --quiet

if [[ "$#" -eq 0 ]]; then
    echo "Done"
    exit 0
fi

echo "Retrieving version definitions..."

access_token=$(gcloud auth print-access-token)
export access_token

curl -s -H "Authorization: Bearer ${access_token}" \
     "${api_root}/apps/${source_instance}/services/default/versions/1?view=FULL" \
     > "${tmp}/1.json"

find "${config_repo}" -name 'appengine-web.xml' -exec sha1sum {} + > "${tmp}/sha1sum.txt"

function deploy_instance {
    instance_id="${1}"
    instance_file="${instance_id}.json"
    backend_file="${instance_id}_dataprocessor.json"

    # select required keys
    jq -M ". | {id, inboundServices, instanceClass, runtime, env, threadsafe, handlers, deployment}" \
       1.json > "${instance_file}.tmp"

    # remove __static__ entries
    jq '.deployment.files |= with_entries(select (.key | test("^__static__") | not))' "${instance_file}.tmp" \
       > "${instance_file}"
    rm -rf "${instance_file}.tmp"

    sed -i "s|apps/${source_instance}/|apps/${instance_id}/|g" "${instance_file}"

    sandbox_sha1_sum=$(awk '$2 ~ "/${source_instance}/appengine-web.xml$" {print $1}' sha1sum.txt)
    instance_sha1_sum=$(awk -v instance="${instance_id}" '$2 ~ "/"instance"/appengine-web.xml$" {print $1}' sha1sum.txt)

    sed -i "s|${sandbox_sha1_sum}|${instance_sha1_sum}|g" "${instance_file}"

    jq ". + {id: \"dataprocessor\", manualScaling: {instances: 1}, instanceClass: \"B2\"}" "${instance_file}" > "${backend_file}"

    gsutil cp -J "${config_repo}/${instance_id}/appengine-web.xml" "gs://${deploy_bucket_name}/${instance_sha1_sum}"

    echo "Deploying ${instance_id} using GAE Admin API..."

    curl -s -X POST -T "${instance_file}" -H "Content-Type: application/json" \
	 -H "Authorization: Bearer ${access_token}" \
	 "${api_root}/apps/${instance_id}/services/default/versions" > \
	 "${instance_id}_operation.json"

    instance_operation_path=$(jq -r .name "${instance_id}_operation.json")

    if [ "${instance_operation_path}" == "null" ]; then
        echo "Deployment to ${instance_id} failed"
        exit 1
    fi

    curl -s -X POST -T "${backend_file}" -H "Content-Type: application/json" \
	 -H "Authorization: Bearer ${access_token}" \
	 "${api_root}/apps/${instance_id}/services/default/versions" > \
	 "${instance_id}_dataprocessor_operation.json"

    if [[ "$(jq -r .name "${instance_id}_dataprocessor_operation.json")" == "null" ]]; then
        echo "Deployment to dataprocessor of ${instance_id} failed"
        exit 1
    fi

    # We only check for liveness of version 1
    for i in {1..20}
    do
	sleep 5
	echo "Checking deployment status - Attempt ${i}"
	done=$( (curl -s \
                      -H "Content-Type: application/json" \
                      -H "Authorization: Bearer ${access_token}" \
                      "${api_root}/${instance_operation_path}" | jq -r .done) || "")
	if [[ "${done}" == "true" ]]; then
            break
	fi
    done

    if [[ "${done}" != "true" ]]; then
	echo "Deployment to ${instance_id} failed"
        exit 1
    fi

    if [[ "${CRON_UPDATE}" != "false" ]]; then
	gcloud app deploy appengine-staging/WEB-INF/appengine-generated/cron.yaml \
	       --project="${instance_id}" --quiet
    fi

    if [[ "${INDEX_UPDATE}" != "false" ]]; then
	gcloud app deploy appengine-staging/WEB-INF/appengine-generated/index.yaml \
	       --project="${instance_id}" --quiet
    fi
    if [[ "${QUEUE_UPDATE}" != "false" ]]; then
	gcloud app deploy appengine-staging/WEB-INF/appengine-generated/queue.yaml \
	       --project="${instance_id}" --quiet
    fi
}

export -f deploy_instance
echo "Deploying instances... $*"
mkdir "${tmp}/parallel"
parallel --results "${tmp}/parallel" --jobs 10 --joblog "${deploy_id}.log" deploy_instance ::: "$@"
echo "Done"
