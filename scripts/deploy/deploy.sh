#!/usr/bin/env bash

set -euo pipefail

TARGET_DIR="${TARGET_DIR:=/akvo-flow/GAE/target/akvo-flow}"
CONFIG_REPO="${CONFIG_REPO:=/akvo-flow-server-config}"
DEPLOY_BUCKET_NAME="${DEPLOY_BUCKET_NAME:=akvoflowsandbox-deployment}"
API_ROOT="https://appengine.googleapis.com/v1"
tmp="/tmp/$(date +%s)"

# Install requirements assuming Debian jessie
echo "Installing dependencies..."
echo "deb http://ftp.debian.org/debian jessie-backports main" >> /etc/apt/sources.list && \
    apt-get update && \
    apt-get -t jessie-backports install -y -qq --no-install-recommends jq unzip

# Force login
gcloud auth login --activate --force

akvo_org_account=$( (gcloud auth list --filter="active" 2>&1 | grep -E '^\*.*akvo\.org$') || echo "")

if [[ -z "${akvo_org_account}" ]]; then
    echo >&2 "Unable to detect an akvo.org account for gcloud"
    exit 1
fi

echo "Updating akvo-flow-server-config to latest changes..."
(
    cd "${CONFIG_REPO}"
    git checkout -- .
    git checkout master
    # temporary hack to enable java8
    patch -p1 < java8.diff
    git diff
)

echo "Deploying to akvoflowsandbox..."
mkdir -p "${tmp}"
cp -v "${CONFIG_REPO}/akvoflowsandbox/appengine-web.xml" "${TARGET_DIR}/WEB-INF/"
sed -i -e "s/__VERSION__/$(git describe)/" "${TARGET_DIR}/admin/js/app.js"

gcloud app deploy "${TARGET_DIR}/WEB-INF/appengine-web.xml" \
       --project=akvoflowsandbox \
       --bucket="gs://${DEPLOY_BUCKET_NAME}" \
       --version=1 \
       --promote

gcloud app deploy "${TARGET_DIR}/WEB-INF/appengine-web.xml" \
       --project=akvoflowsandbox \
       --bucket="gs://${DEPLOY_BUCKET_NAME}" \
       --version=dataprocessor \
       --no-promote

echo "Retrieving version definitions..."

access_token=$(gcloud auth print-access-token)

curl -H "Authorization: Bearer ${access_token}" \
     "${API_ROOT}/apps/akvoflowsandbox/services/default/versions/1?view=FULL" \
     > "${tmp}/1.json"

curl -H "Authorization: Bearer ${access_token}" \
     "${API_ROOT}/apps/akvoflowsandbox/services/default/versions/dataprocessor?view=FULL" \
     > "${tmp}/dataprocessor.json"

find "${CONFIG_REPO}" -name 'appengine-web.xml' -exec sha1sum {} + > "${tmp}/sha1sum.txt"

(
    cd "${tmp}"

    instance_id="akvoflow-uat2"
    instance_file="${instance_id}.json"

    # select required keys
    jq -M ". | {id, instanceClass, runtime, env, threadsafe, handlers, deployment}" \
       1.json > "${instance_file}.tmp"

    # remove __static__ entries
    jq '.deployment.files |= with_entries(select (.key | test("^__static__") | not))' "${instance_file}.tmp" \
       > "${instance_file}"

    rm -rf "${instance_file}.tmp"

    sed -i -e "s|apps/akvoflowsandbox/|apps/${instance_id}/|g" "${instance_file}"

    sandbox_sha1_sum=$(awk '$2 ~ "/akvoflowsandbox/appengine-web.xml$" {print $1}' sha1sum.txt)
    instance_sha1_sum=$(awk -v instance="${instance_id}" '$2 ~ "/"instance"/appengine-web.xml$" {print $1}' sha1sum.txt)

    sed -i -e "s|${sandbox_sha1_sum}|${instance_sha1_sum}|g" "${instance_file}"
    gsutil cp -J "${CONFIG_REPO}/${instance_id}/appengine-web.xml" "gs://${DEPLOY_BUCKET_NAME}/${instance_sha1_sum}"

    curl -X POST -T "${instance_file}" -H "Content-Type: application/json" \
	 -H "Authorization: Bearer ${access_token}" \
	 "${API_ROOT}/apps/${instance_id}/services/default/versions"
)
