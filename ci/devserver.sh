#!/usr/bin/env bash

#
#  Copyright (C) 2017-2018 Stichting Akvo (Akvo Foundation)
#
#  This file is part of Akvo FLOW.
#
#  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
#  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#  either version 3 of the License or any later version.
#
#  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#  See the GNU Affero General Public License included below for more details.
#
#  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
#

set -eu
set -o monitor

_term() {
  echo "Caught SIGCHLD signal"
  if jobs -l | grep "Exit" | tr -s " " | grep " $npm_process "; then
    echo "npm process died. Please check build.dev.log"
    echo "Stopping container now"
    exit 1
  else
    jobs -l
  fi
}

trap _term SIGCHLD

SRC_DIR="/app/src"

cd "$SRC_DIR/Dashboard"

(
    set -o monitor
    npm install
    npm run build:dev
) > "$SRC_DIR/build.dev.log" 2>&1 &

npm_process=$!

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/admin/frames/users.js" ]]; then
    cd "$SRC_DIR/Dashboard/app/cljs"
    lein build copyhtml
else
    echo "Skipping ClojureScript build ..."
fi

cd "$SRC_DIR/GAE"

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/local_db.bin" ]]; then
    mkdir -p "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/"
    mkdir -p "$SRC_DIR/GAE/tmp/"
    if [[ ! -f "$SRC_DIR/GAE/tmp/local_db.bin" ]]; then
	wget "https://s3-eu-west-1.amazonaws.com/akvoflow/test-data/local_db.bin" -O "$SRC_DIR/GAE/tmp/local_db.bin"
    fi
    cp "$SRC_DIR/GAE/tmp/local_db.bin" "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/local_db.bin"
fi

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-web.xml" ]]; then
    mkdir -p "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/"
    cp "$SRC_DIR/tests/dev-appengine-web.xml" "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-web.xml"
fi

mvn package appengine:start

tail -F -n 5000  "$SRC_DIR/build.dev.log" ./target/akvo-flow/flow0.log
