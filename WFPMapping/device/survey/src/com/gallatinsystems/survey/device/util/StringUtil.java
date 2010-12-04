package com.gallatinsystems.survey.device.util;

/**
 * simple string convenience functions
 * 
 * @author Christopher Fagiani
 * 
 */
public class StringUtil {

	/**
	 * checks a string to see if it's null or has no non-whitespace characters
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNullOrEmpty(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
