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
	
	public static String ControlToSPace(String val) {
		String result = "";
		for (int i= 0; i < val.length(); i++) {
			if (val.charAt(i) < '\u0020')
				result = result + '\u0020';
			else
				result = result + val.charAt(i);
		}
		
		return result;
	}
}
