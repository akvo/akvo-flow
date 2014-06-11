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

package com.gallatinsystems.common.util;

/**
 * utility class for dealing with CSV files (handles things like escaping/unescaping quotes, commas
 * and newlines)
 */
public class CSVUtil {

    private static final String QUOTE = "\"";
    private static final String ESCAPED_QUOTE = "\"\"";
    private static char[] CHARACTERS_THAT_MUST_BE_QUOTED = {
            ',', '"', '\n'
    };

    public static String Escape(String s) {
        if (s.contains(QUOTE))
            s = s.replace(QUOTE, ESCAPED_QUOTE);

        for (char item : CHARACTERS_THAT_MUST_BE_QUOTED) {
            if (s.indexOf(item) > -1) {
                s = QUOTE + s + QUOTE;
                break;
            }
        }
        return s;
    }

    public static String Unescape(String s) {
        if (s.startsWith(QUOTE) && s.endsWith(QUOTE)) {
            s = s.substring(1, s.length() - 2);
            if (s.contains(ESCAPED_QUOTE))
                s = s.replace(ESCAPED_QUOTE, QUOTE);
        }
        return s;
    }
}
