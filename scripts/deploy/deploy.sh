#!/usr/bin/env bash

set -euo pipefail

export SHELL=/bin/bash

function log {
   echo "$(date +"%T") - INFO - $*"
}

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

exit 0

log Done
