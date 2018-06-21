#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

yellow='\033[1;33m'
green='\033[0;32m'
nc='\033[0m'

if [[ "$#" -lt 2 ]]; then
    echo "Usage: ./scripts/deploy/run.sh <version> <instance-id-1> [<instance-id-2> ... <instance-id-n>]"
    exit 1
fi

export version="${1}"         # <version> as $1
shift 1                       # $@ rest of instances

deploy_id="$(date +%s)"
tmp="/tmp/${deploy_id}"

mkdir -p "${tmp}"
mkdir -p "${tmp}/parallel"

# Move to tmp folder and work there
cd "${tmp}"

export config_repo="${CONFIG_REPO:=${tmp}/akvo-flow-server-config}"
export deploy_bucket_name="${deploy_bucket_name:=akvoflowsandbox-deployment}"

gh_user="${GH_USER:=unknown}"
gh_token="${GH_TOKEN:=unknown}"

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

gsutil cp "gs://${deploy_bucket_name}/${version}.zip" "${version}.zip"
unzip "${version}.zip"

if [[ ! -d "appengine-staging" ]]; then
    echo "Staging folder is not present"
    exit 1
fi

function deploy_instance {
    instance_id="${1}"
    staging_dir="appengine-staging-${1}"

    echo "Copying staging dir to ${staging_dir}"
    cp -r appengine-staging "${staging_dir}"

    echo "Deleting dataprocessor version"
    gcloud app versions delete dataprocessor --project="${instance_id}" --quiet

    echo "Deploying ${instance_id} from ${staging_dir}"

    cp "${config_repo}/${instance_id}/appengine-web.xml" "${staging_dir}/WEB-INF/appengine-web.xml"

    java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
	 com.google.appengine.tools.admin.AppCfg \
	 --retain_upload_dir \
	 --application="${instance_id}" \
	 update "${staging_dir}"

    java -cp /google-cloud-sdk/platform/google_appengine/google/appengine/tools/java/lib/appengine-tools-api.jar \
	 com.google.appengine.tools.admin.AppCfg \
	 --retain_upload_dir \
	 --application="${instance_id}" \
	 backends update "${staging_dir}"
}

export -f deploy_instance

# Deploy first instance to be able to grab OAuth2 token in $HOME/.appcfg_oauth2_tokens_java
deploy_instance "${1}"

# Deploy rest if present
shift 1

if [[ "$#" -eq 0 ]]; then
    exit 0
fi

echo "Deploying instances... $*"

parallel --results "${tmp}/parallel" --retries 3 --jobs 10 --joblog "${deploy_id}.log" deploy_instance ::: "$@"

echo "Done"
