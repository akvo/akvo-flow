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
 * Utilities for manipulating arrays
 * 
 * @author Christopher Fagiani
 * 
 */
public class ArrayUtil {

	/**
	 * copies the values from the source array into the destination array
	 * starting at index startIndex.
	 * 
	 * @param destination
	 * @param source
	 * @param startIndex
	 * @throws IllegalArgumentException
	 *             - if the destination array isn't large enough to accommodate
	 *             all elements in the source
	 */
	public static void combineArrays(Object[] destination, Object[] source,
			int startIndex) {
		if (destination.length < startIndex + source.length) {
			throw new IllegalArgumentException(
					"Destination array is of insufficient size");
		}
		for (int i = 0; i < source.length; i++) {
			destination[i + startIndex] = source[i];
		}
	}
}
