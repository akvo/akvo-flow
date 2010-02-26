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
