/*
 *  Copyright (C) 2016 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.serialization.response;

import java.io.IOException;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.domain.response.value.Media;

public class MediaResponse {
    private static final Logger log = Logger.getLogger(MediaResponse.class.getName());
    private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    public static final int VERSION_STRING = 0;
    public static final int VERSION_GEOTAGGING = 1;

    /**
     * Format media value with the given format. If the data is already in the output format, no
     * transformations are applied.
     */
    public static String format(String value, int version) {
        Media media;
        int savedVersion;
        try {
            media = JSON_OBJECT_MAPPER.readValue(value, Media.class);
            savedVersion = VERSION_GEOTAGGING;
        } catch (IOException e) {
            // Value is not JSON-formatted
            media = new Media();
            media.setFilename(value);
            savedVersion = VERSION_STRING;
        }

        if (version == savedVersion) {
            return value;
        }

        if (version == VERSION_GEOTAGGING) {
            try {
                return JSON_OBJECT_MAPPER.writeValueAsString(media);
            } catch (IOException e) {
                log.warning(e.getMessage());
                return "";
            }
        }

        // VERSION_INITIAL
        return media.getFilename();
    }

    public static Media parse(String value) {
        try {
            return JSON_OBJECT_MAPPER.readValue(value, Media.class);
        } catch (IOException e) {
        }

        // Value is not JSON-formatted
        Media media = new Media();
        media.setFilename(value);
        return media;
    }
}
