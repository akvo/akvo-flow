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

package org.akvo.flow.locale;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

/**
 * Class to load and return language specific strings
 */
public class UIStrings {
    public static String getStrings(final InputStream uiStringsResource,
            final InputStream localeStringsResource) throws IOException {

        final Properties uiStrings = new Properties();
        uiStrings.load(uiStringsResource);

        if (localeStringsResource == null) { // return default EN strings
            return new JSONObject(uiStrings).toString();
        } else {
            final Properties localeStrings = new Properties();
            localeStrings.load(localeStringsResource);
            return new JSONObject(translateKeys(uiStrings, localeStrings)).toString();
        }
    }

    /**
     * Translate keys for non-english langauges
     *
     * @param strings
     * @param tr
     * @return
     */
    private static Map<String, String> translateKeys(Properties strings, Properties tr) {
        final Map<String, String> result = new HashMap<String, String>();
        Iterator<Object> keys = strings.keySet().iterator();
        while (keys.hasNext()) {
            String k = (String) keys.next();
            String v = tr.getProperty(strings.getProperty(k));
            if (v == null) {
                // log.log(Level.WARNING, "Translation for term " + k
                // + " not found, using English term");
                result.put(k, strings.getProperty(k));
            } else {
                result.put(k, v);
            }
        }
        return result;
    }
}
