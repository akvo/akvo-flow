#!/usr/bin/env bash

#
#  Copyright (C) 2017 Stichting Akvo (Akvo Foundation)
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

SRC_DIR="/app/src"

cd "$SRC_DIR/Dashboard"

bundle exec rake watchbg > "$SRC_DIR/rake.log" 2>&1 &

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/admin/frames/users.js" ]]; then
    cd "$SRC_DIR/Dashboard/app/cljs"
    lein build
else
    echo "Skipping ClojureScript build ..."
fi

cd "$SRC_DIR/GAE"

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/local_db.bin" ]]; then
    mkdir -p "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/"
    wget "https://s3-eu-west-1.amazonaws.com/akvoflow/test-data/local_db.bin" -O "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-generated/local_db.bin"
fi

if [[ ! -f "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-web.xml" ]]; then
    mkdir -p "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/"
    cp "$SRC_DIR/tests/dev-appengine-web.xml" "$SRC_DIR/GAE/target/akvo-flow/WEB-INF/appengine-web.xml"
fi

mvn package appengine:run

tail -F ./target/akvo-flow/flow0.log "$SRC_DIR/rake.log"
