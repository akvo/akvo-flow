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
 * Collection of methods for date manipulation
 * 
 * @author Christopher Fagiani
 */
public class DateUtil {

    /**
     * Returns a new date that is the same as the one passed in except it has a time of 00:00:00
     * 
     * @param oldDate
     * @return
     */
    public static Date getDateNoTime(Date oldDate) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(oldDate);
        // drop the time portion
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * returns the year as a Long
     */
    public static Long getYear(Date dt) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dt);
        return new Long(cal.get(Calendar.YEAR));
    }

    /**
     * takes a string containing a 4 digit year and returns a Date object that has the year
     * indicated and all other fields set to 0 (so jan 1 of that year at 00:00:00)
     * 
     * @param year
     * @return
     * @throws NumberFormatException
     */
    public static Date getYearOnlyDate(String year)
            throws NumberFormatException {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.YEAR, new Integer(year));
        // sets the time portion to 00:00:00 and returns
        return getDateNoTime(cal.getTime());
    }
}
