#!/usr/bin/env bash

#
#  Copyright (C) 2017-2020 Stichting Akvo (Akvo Foundation)
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

/app/src/ci/check-property-encoding.sh

SRC_DIR="/app/src"

cd "${SRC_DIR}/Dashboard"

npm ci
npm rebuild node-sass
npm run lint:quiet
npm run build:prod

cd "${SRC_DIR}/Dashboard/app/cljs"

lein build

cd "${SRC_DIR}/GAE"

mvn package
