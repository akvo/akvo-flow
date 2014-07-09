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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * string manipulation utility methods
 * 
 * @author Christopher Fagiani
 */
public class StringUtil {

    /**
     * capitalizes the first letter of each word in the string. Leaves case alone for all others
     * 
     * @param string
     * @return
     */
    public static String capitalizeString(String string) {
        char[] chars = string.toCharArray();
        boolean isFirst = true;
        for (int i = 0; i < chars.length; i++) {
            if (isFirst && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                isFirst = false;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.'
                    || chars[i] == '\'') {
                isFirst = true;
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }
        return String.valueOf(chars);
    }

    /**
     * returns the four digit year as a string or an empty string if the date is null
     * 
     * @param date
     * @return
     */
    public static String getYearString(Date date) {
        String yearString = "";
        if (date != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            yearString = cal.get(Calendar.YEAR) + "";
        }
        return yearString;
    }

    public static String capitalizeFirstCharacterString(String item) {
        String attributeField = item;
        String firstChar = attributeField.substring(0, 1);
        firstChar = firstChar.toUpperCase();
        return firstChar + attributeField.substring(1);
    }

    /**
     * takes a byte array and outputs it as a string of hex digits
     * 
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            hexString.append(Integer.toHexString(0xFF & bytes[i]));
        }
        return hexString.toString();
    }
}
