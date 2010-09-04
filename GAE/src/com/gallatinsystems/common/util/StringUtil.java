package com.gallatinsystems.common.util;

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
			}else{
				chars[i] = Character.toLowerCase(chars[i]);
			}
		}
		return String.valueOf(chars);
	}
}
