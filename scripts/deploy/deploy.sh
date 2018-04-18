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

export version="${1}" # <version> as "$1"
shift                 # we want "$@" to be the instances to deploy

export config_repo="${CONFIG_REPO:=/akvo-flow-server-config}"
export deploy_bucket_name="${deploy_bucket_name:=akvoflowsandbox-deployment}"
export api_root="https://appengine.googleapis.com/v1"

deploy_id="$(date +%s)"
tmp="/tmp/${deploy_id}"
target_dir="${tmp}/akvo-flow"
gh_user="${GH_USER:=''}"
gh_token="${GH_TOKEN:=''}"

# Install requirements assuming Debian jessie
echo "Installing dependencies..."
echo "deb http://ftp.debian.org/debian jessie-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get -t jessie-backports install -y -qq --no-install-recommends \
	    jq=1.5+dfsg-1.3~bpo8+1 \
	    unzip=6.0-16+deb8u3 \
	    parallel=20130922-1

# Force login
gcloud auth login --brief --activate --force

akvo_org_account=$( (gcloud auth list --filter="active" 2>&1 | grep -E '^\*.*akvo\.org$') || echo "")

if [[ -z "${akvo_org_account}" ]]; then
    echo >&2 "Unable to detect an akvo.org account for gcloud"
    exit 1
fi

echo "Cloning akvo-flow-server-config..."

if [[ -n "${gh_user}" ]] && [[ -n "${gh_token}" ]]; then
    git clone --depth=50 --branch=master \
	"https://${gh_user}:${gh_token}@github.com/akvo/akvo-flow-server-config.git" "${config_repo}" > /dev/null
else
    echo -e "${yellow}WARNING:${nc} If you have 2FA enabled in GitHub, make sure you have a
             ${green}personal access token${nc} available and use it as password"
    echo "Visit for more info: https://blog.github.com/2013-09-03-two-factor-authentication/#how-does-it-work-for-command-line-git"
    git clone --depth=50 --branch=master \
	"https://github.com/akvo/akvo-flow-server-config.git" "${config_repo}" > /dev/null
fi

echo "Deploying to akvoflowsandbox using gcloud..."
mkdir -p "${tmp}"
gsutil cp "gs://${deploy_bucket_name}/${version}.war" "${tmp}"
unzip "${tmp}/${version}.war" -d "${target_dir}"
rm -rf "${tmp}/${version}.war"
cp -v "${config_repo}/akvoflowsandbox/appengine-web.xml" "${target_dir}/WEB-INF/"
sed -i -e "s/__VERSION__/${version}/" "${target_dir}/admin/js/app.js"

gcloud app deploy "${target_dir}/WEB-INF/appengine-web.xml" \
       --project=akvoflowsandbox \
       --bucket="gs://${deploy_bucket_name}" \
       --version=1 \
       --promote

gcloud app deploy "${target_dir}/WEB-INF/appengine-web.xml" \
       --project=akvoflowsandbox \
       --bucket="gs://${deploy_bucket_name}" \
       --version=dataprocessor \
       --no-promote

echo "Retrieving version definitions..."

access_token=$(gcloud auth print-access-token)
export access_token

curl -s -H "Authorization: Bearer ${access_token}" \
     "${api_root}/apps/akvoflowsandbox/services/default/versions/1?view=FULL" \
     > "${tmp}/1.json"

find "${config_repo}" -name 'appengine-web.xml' -exec sha1sum {} + > "${tmp}/sha1sum.txt"

echo "Deploying instances..."

# Move to tmp folder and work there
cd "${tmp}"

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

    sed -i "s|apps/akvoflowsandbox/|apps/${instance_id}/|g" "${instance_file}"

    sandbox_sha1_sum=$(awk '$2 ~ "/akvoflowsandbox/appengine-web.xml$" {print $1}' sha1sum.txt)
    instance_sha1_sum=$(awk -v instance="${instance_id}" '$2 ~ "/"instance"/appengine-web.xml$" {print $1}' sha1sum.txt)

    sed -i "s|${sandbox_sha1_sum}|${instance_sha1_sum}|g" "${instance_file}"

    # Copy definition and modify `version` and `instanceClass`
    cp "${instance_file}" "${backend_file}"
    sed -i 's/"F1"/"B2"/' "${backend_file}"
    sed -i 's/"1"/"dataprocessor"/' "${backend_file}"

    gsutil cp -J "${config_repo}/${instance_id}/appengine-web.xml" "gs://${deploy_bucket_name}/${instance_sha1_sum}"

    echo "Deploying ${instance_id} using GAE Admin API..."

    curl -s -X POST -T "${instance_file}" -H "Content-Type: application/json" \
	 -H "Authorization: Bearer ${access_token}" \
	 "${api_root}/apps/${instance_id}/services/default/versions" > \
	 "${instance_id}_operation.json"

    curl -s -X POST -T "${backend_file}" -H "Content-Type: application/json" \
	 -H "Authorization: Bearer ${access_token}" \
	 "${api_root}/apps/${instance_id}/services/default/versions" > \
	 "${instance_id}_dataprocessor_operation.json"

    instance_operation_path=$(jq -r .name "${instance_id}_operation.json")

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
}

export -f deploy_instance
parallel -j 8 --joblog "${deploy_id}.log" deploy_instance ::: "$@"
echo "Done"
