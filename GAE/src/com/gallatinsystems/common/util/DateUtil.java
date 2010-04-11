package com.gallatinsystems.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Collection of methods for date manipulation
 * 
 * @author Christopher Fagiani
 * 
 */
public class DateUtil {

	/**
	 * Returns a new date that is the same as the one passed in except it has a
	 * time of 00:00:00
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
	 * takes a string containing a 4 digit year and returns a Date object that
	 * has the year indicated and all other fields set to 0 (so jan 1 of that
	 * year at 00:00:00)
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
