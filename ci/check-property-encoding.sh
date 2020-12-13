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

set -euo pipefail

DIRECTORY=$(cd `dirname $0` && pwd)

check() {
    properties_files="$(find "${DIRECTORY}/.." -type f -name "*.properties")"
    if [[ -n "$properties_files" ]]
    then
        while read -r property_file
        do
            DETECTED_ENCODING=$(uchardet ${property_file})
            # Should we check for ISO-8859-1, or maybe not UTF-8?
            # Is it possible to check what version of Java?
            if [ "$DETECTED_ENCODING" != "ISO-8859-1" ]; then
                echo "Found Java properties file with $DETECTED_ENCODING encoding"
                echo "${property_file}"
            fi
        done <<< "${properties_files}"
    fi
}

check
