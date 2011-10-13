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
