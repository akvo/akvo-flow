#!/usr/bin/env bash

set -eu

TARGET_DIR=GAE/target/akvo-flow/WEB-INF/

mkdir -p "${TARGET_DIR}"

cp "tests/dev-appengine-web.xml" "${TARGET_DIR}/appengine-web.xml"