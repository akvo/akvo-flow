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
INSTANCE_NAME="$1"
BUILD_PROPERTIES_DIR="$2"

SCRIPTS_HOME="$(cd `dirname "$THIS_SCRIPT"` && pwd)"
PROJECT_HOME="$(cd "$SCRIPTS_HOME"/.. && pwd)"

"$SCRIPTS_HOME"/verify_build_properties.sh `basename "$THIS_SCRIPT"` "$INSTANCE_NAME" "$BUILD_PROPERTIES_DIR"

# continue with build if no errors were found
if [ $? -eq 0 ]; then
    cd "$PROJECT_HOME"
    ant -Dinstance.name=$INSTANCE_NAME clean copyconfig compile datanucleusenhance GWTcompile
fi
