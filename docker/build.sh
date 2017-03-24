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
APP_ENGINE_SDK_FILE="appengine-java-sdk-1.9.50.zip"

unzip "$HOME/.cache/$APP_ENGINE_SDK_FILE" -d /tmp

cd "$SRC_DIR/Dashboard"

RAKEP_MODE=production bundle exec rake build --trace

cd "$SRC_DIR/Dashboard/app/cljs"

lein build

cd "$SRC_DIR/GAE"

cp -f build.properties.template build.properties

sed -i 's/^sdk\.dir=.*/sdk\.dir=\/tmp\/appengine-java-sdk-1.9.50/' build.properties

ant compile datanucleusenhance
