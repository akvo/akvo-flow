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
CALLING_SCRIPT_NAME="$1"
INSTANCE_NAME="$2"
BUILD_PROPERTIES_DIR="$3"

function display_usage_and_exit
{
    printf "Usage: $CALLING_SCRIPT_NAME <instance_name> [build_properties_directory]\n"
    printf "       where [build_properties_directory] contains an external build.properties file to use\n"
    exit -1
}

if [[ -z "$INSTANCE_NAME" ]]; then
    echo "## Missing parameter: <instance_name>"
    display_usage_and_exit
fi

PROJECT_HOME="$(cd `dirname "$THIS_SCRIPT"`/.. && pwd)"

cd "$PROJECT_HOME"

if [[ -n "$BUILD_PROPERTIES_DIR" ]]; then
    printf ">> Linking build.properties from $BUILD_PROPERTIES_DIR\n\n"
    ln -s "$BUILD_PROPERTIES_DIR/build.properties"
fi

echo ">> Java compiler:"
java -version

printf "\n>> Ant runner:\n"
ant -version
printf "\n"
