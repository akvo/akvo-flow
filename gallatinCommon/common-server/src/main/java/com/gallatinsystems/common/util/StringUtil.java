package com.gallatinsystems.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * string manipulation utility methods
 * 
 * @author Christopher Fagiani
 * 
 */
public class StringUtil {

	/**
	 * capitalizes the first letter of each word in the string. Leaves case
	 * alone for all others
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
	 * returns the four digit year as a string or an empty string if the date is
	 * null
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
}
