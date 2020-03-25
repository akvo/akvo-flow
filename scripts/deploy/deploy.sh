#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

yellow='\033[1;33m'
green='\033[0;32m'
nc='\033[0m'

if [[ "$#" -lt 2 ]]; then
    echo "Usage: ./scripts/deploy/run.sh <version> [all | <instance-id-1> <instance-id-2> ... <instance-id-n>]"
    exit 1
fi

export version="${1}"         # <version> as $1
shift 1                       # $@ rest of instances

deploy_id="$(date +%s)"
tmp="/tmp/${deploy_id}"

mkdir -p "${tmp}"

# Move to tmp folder and work there
cd "${tmp}"

gh_user="${GH_USER:=unknown}"
gh_token="${GH_TOKEN:=unknown}"

echo "Cloning akvo-flow-server-config..."

if [[ "${gh_user}" != "unknown" ]] && [[ "${gh_token}" != "unknown" ]]; then
    git clone --depth=50 --branch=master \
	"https://${gh_user}:${gh_token}@github.com/akvo/akvo-flow-server-config.git" > /dev/null
else
    echo -e "${yellow}WARNING:${nc} If you have 2FA enabled in GitHub, make sure you have a
             ${green}personal access token${nc} available and use it as password"
    echo "Visit for more info: https://blog.github.com/2013-09-03-two-factor-authentication/#how-does-it-work-for-command-line-git"
    git clone --depth=50 --branch=master \
	"https://github.com/akvo/akvo-flow-server-config.git" > /dev/null
fi

config="akvo-flow-server-config"
export config

gcloud auth activate-service-account --key-file="${config}/akvoflow-uat1/akvoflow-uat1-29cd359eae9b.json"

if [[ "${1}" == "all" ]]; then

    find "${config}" -name 'appengine-web.xml' | awk -F'/' '{print $2}' > instances.txt
    find "${config}" -name '.skip-deployment' | awk -F'/' '{print $2}' > skip.txt

    cat < skip.txt |  while IFS= read -r line
    do
	sed -i "/$line/d" instances.txt
    done
else
    echo "$@" > instances.tmp
    tr ' ' '\n' < instances.tmp > instances.txt
fi

access_token=$(gcloud auth print-access-token)
export access_token

# Get version definition
api_root="https://appengine.googleapis.com/v1"
export api_root

curl -s \
     -H "Authorization: Bearer ${access_token}" \
     -o uat1.full.json \
     "${api_root}/apps/akvoflow-uat1/services/default/versions/${version}?view=FULL"

# Cleanup
jq -M '. | {id, instanceClass, runtime, env, threadsafe, handlers, deployment}' uat1.full.json > uat1.tmp

jq -M '.deployment.files |= with_entries(select (.key | test("^__static__") | not))' uat1.tmp > uat1.final.json

uat1_sha1sum=$(jq -M -r '.deployment.files["WEB-INF/appengine-web.xml"].sha1Sum' uat1.final.json)
export uat1_sha1sum

function deploy_instance {
    instance_id="${1}"
    instance_sha1sum=$(sha1sum "${config}/${instance_id}/appengine-web.xml" | awk '{print $1}')
    instance_file="${instance_id}.final.json"

    cp uat1.final.json "${instance_file}"

    sed -i \
	-e "s|staging.akvoflow-uat1.appspot.com/${uat1_sha1sum}|staging.${instance_id}.appspot.com/${uat1_sha1sum}|" \
	-e "s|${uat1_sha1sum}|${instance_sha1sum}|g" \
	"${instance_file}"

    curl -s \
     -X POST \
     -T "${instance_file}" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer ${access_token}" \
     -o "${instance_id}-operation.json" \
     "${api_root}/apps/${instance_id}/services/default/versions"
}

export -f deploy_instance

echo "Deploying instances..."

cat < instances.txt |  while IFS= read -r instance
do
    deploy_instance "${instance}"
done

echo "Done"
