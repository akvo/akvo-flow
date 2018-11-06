#!/usr/bin/env bash

set -eu

APP_ID="${1}"
FLOW_CONFIG_DIR=../akvo-flow-server-config

if [[ ! -d "${FLOW_CONFIG_DIR}" ]]; then
    echo "Flow config repository not found at ${FLOW_CONFIG_DIR}"
    exit 1
fi

if [[ -z "${APP_ID}" ]]; then
    echo "First param must be the tenant folder (for example akvoflow-uat2 or akvoflowsandbox)"
    exit 2
fi

TARGET_DIR=GAE/target/akvo-flow/WEB-INF/

mkdir -p "${TARGET_DIR}"

cp "${FLOW_CONFIG_DIR}/${APP_ID}/appengine-web.xml" "${TARGET_DIR}"
