package com.gallatinsystems.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Singleton for accessing system properties.
 * 
 * @author Dru Borden
 * 
 */
public class PropertyUtil {

	private static Properties props = null;

	private PropertyUtil() {
		initProperty();
	}

	/**
	 * initializes the static instance with the contents of System.properties
	 */
	private void initProperty() {
		if (props == null) {
			props = System.getProperties();
		}
	}

	/**
	 * returns the value of a single property (or null if not found)
	 * 
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String propertyName) {
		if (props == null) {
			new PropertyUtil();
		}
		return props.getProperty(propertyName);
	}

	/**
	 * returns a copy of the the properties map
	 * 
	 * @param keyList
	 * @return
	 */
	public static HashMap<String, String> getPropertiesMap(
			ArrayList<String> keyList) {
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		for (String key : keyList) {
			String value = props.getProperty(key);
			propertyMap.put(key, value);
		}
		return propertyMap;
	}

}
