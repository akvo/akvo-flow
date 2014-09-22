/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.image;

/**
 * utility class for image manipulation
 */
public class ImageUtils {
    /*
     * Utility method to return the parts of an image path for S3 Position 0 = web domain with
     * bucket ends with / Position 1 = middle path elements ends with / Position 2 = file name
     */
    public static String[] parseImageParts(String url) {
        String[] parts = new String[3];
        url = url.replace("http://", "");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        String[] items = url.split("/");
        if (items.length == 3) {
            // no country in path
            parts[0] = ("http://:" + items[0] + "/");
            parts[1] = (items[1] + "/");
            parts[2] = (items[2]);
        } else if (items.length > 3) {
            parts[0] = ("http://:" + items[0] + "/");
            String middlePath = "";
            int i = 0;
            for (i = 1; i < items.length - 1; i++)
                middlePath += items[i] + "/";
            parts[1] = (middlePath);
            parts[2] = (items[i]);
        }

        return parts;
    }
}
