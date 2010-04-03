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
}
