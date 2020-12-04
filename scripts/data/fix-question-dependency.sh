#!/usr/bin/env bash

set -eu

if [[ ! -f "./bin/org/akvo/gae/remoteapi/FixCopyDependency.class" ]]; then
    echo "You must compile the project first"
    exit 1
fi

config="${1:-}"

if [[ -z "${config}" ]]; then
    echo "Usage: ./fix-question-dependency.sh <path-to-config-repo>"
    echo "You must provide the path to akvo config repo"
    exit 1;
fi

export config

execute_script() {
    instance_id="${1}"
    service_account="sa-${instance_id}@${instance_id}.iam.gserviceaccount.com"
    p12_file="${config}/${instance_id}/${instance_id}.p12"

    echo "Executing for: ${instance_id}"

    java -cp bin:../../GAE/target/akvo-flow/WEB-INF/classes:"lib/*" \
	 org.akvo.gae.remoteapi.RemoteAPI \
	 FixCopyDependency \
	 "${instance_id}" \
	 "${service_account}" \
	 "${p12_file}"
}

export -f execute_script


find "${config}" -name 'appengine-web.xml' | awk -F '/' '{print $(NF-1)}' | sort > /tmp/instances.txt

cat < /tmp/instances.txt | while IFS= read -r instance
do
    execute_script "${instance}"
done
