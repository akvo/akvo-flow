#!/usr/bin/env bash

set -u

display_usage() {
    echo "This script expects 2 arguments"
    echo -e "\nUsage:\n./delete-kind.sh <project-id> <kind-name>"
    echo -e "\nExample:\n./delete-kind.sh akvoflowsandbox SurveyalValue"
    echo -e "\nDependencies:\ngcloud  - https://cloud.google.com/sdk/docs/\njq - https://stedolan.github.io/jq/"
}

if [[ "$#" -ne 2 ]]; then
    display_usage
    exit 1
fi

project="${1}"
kind="${2}"
bucket="${project/-/}"
DISABLE_API="${DISABLE_API:=yes}"
ts="$(date +%s)"
job_name="deletejob-${ts}"

gcloud config set project "${project}"

project_location=$(gcloud app describe --format=json | jq -r -M .locationId)
region="${project_location}1"

gsutil rm -r "gs://tmpdelete${bucket}/"
gsutil mb "gs://tmpdelete${bucket}/"

if gcloud services list | grep DataF; then
    echo "Dataflow API was enabled"
    DISABLE_API=no
else
    echo "Enabling dataflow api"
    DISABLE_API=yes
    gcloud services enable dataflow.googleapis.com
    sleep 60
fi

gcloud dataflow jobs run "${job_name}" \
--region "${region}" \
--gcs-location gs://dataflow-templates/latest/Datastore_to_Datastore_Delete \
--parameters datastoreReadGqlQuery="SELECT __key__ from ${kind}",\
datastoreReadProjectId="${project}",\
datastoreDeleteProjectId="${project}"

until gcloud dataflow jobs list \
	     --region="${region}" \
	     --filter="name=${job_name}" \
	     --limit=1 \
	     --status=terminated \
	     --format=json | jq -M -r .[0].state | grep Done; do
    gcloud dataflow jobs list --region "${region}" --filter="name=${job_name}"
    sleep 600;
done

gsutil rm -r "gs://tmpdelete${bucket}/"

if [[ "${DISABLE_API}" == "yes" ]]; then
    echo "Disabling Dataflow API"
    gcloud services disable dataflow.googleapis.com
fi
