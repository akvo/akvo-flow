#!/bin/bash

#   Copyright (C) 2010-2013 Stichting Akvo (Akvo Foundation)
#
#   This file is part of Akvo FLOW.
#
#   Akvo FLOW is free software: you can redistribute it and modify it under the terms of
#   the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
#   either version 3 of the License or any later version.
#
#   Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
#   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
#   See the GNU Affero General Public License included below for more details.
#
#   The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.

THIS_SCRIPT="$0"
BUILD_MODE="$1"

PROJECT_HOME="$(cd `dirname "$THIS_SCRIPT"`/.. && pwd)"

cd "$PROJECT_HOME"
printf "\n>> Building from `pwd`\n"

if [ -e "build.properties" ]; then
    printf ">> Found expected build.properties file in $PROJECT_HOME\n\n"
else
    if [ $BUILD_MODE = "ci" ]; then
        printf ">> Linking build properties for CI build\n\n"
        ln -s /usr/local/etc/akvo/build/flow/server/build.properties
    else
        printf "## Missing build.properties file in $PROJECT_HOME\n\n"
        exit -1
    fi
fi
