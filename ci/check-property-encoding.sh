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

# .properties files should be ISO-8859-1 encoded. It's easy to introduce for
# example UTF-8 when updating translations in some editors. This will render
# translations strings faulty. Hence we want to guard against this.
# The flow-builder container have the tool uchardet installed. We use it
# to try to detect the encoding. However this is a detection tool and will
# report things like ASCII for valid ISO-8859-1 files. Hence we only guard
# against findings of UTF-8 and UTF-16.

EXIT_CODE=0

check() {
    properties_files="$(find GAE -type f -name "*.properties")"
    if [[ -n "$properties_files" ]]
    then
        while read -r property_file
        do
            DETECTED_ENCODING=$(uchardet "${property_file}")
            if [[ "$DETECTED_ENCODING" == "UTF-8" || "$DETECTED_ENCODING" == "UTF-16" ]]
            then
                echo "${DETECTED_ENCODING=} - ${property_file}"
                EXIT_CODE=1
            fi
        done <<< "${properties_files}"
    fi
}

echo "Checking encoding of .properties files:"
check
if [[ $EXIT_CODE = 0 ]]
then
    echo "Could not find any file with problematic encoding."
fi

exit $EXIT_CODE
