package com.gallatinsystems.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Utility for finding specific class annotations
 */
public class ClassAttributeUtil {
	private static HashMap<String, TreeMap<String, String>> classAttributeMap = new HashMap<String, TreeMap<String, String>>();
	private static final Logger logger = Logger
			.getLogger(ClassAttributeUtil.class.getName());

	@SuppressWarnings("unchecked")
	public static TreeMap<String, String> listObjectAttributes(String className) {
		TreeMap<String, String> attributesList = classAttributeMap
				.get(className);
		if (attributesList == null) {
			attributesList = new TreeMap<String, String>();
			Class cls;
			try {
				cls = Class.forName(className);

				for (Field item : cls.getDeclaredFields()) {
					if (!item.getName().contains("jdo")
							&& !item.getName().equals("serialVersionUID")
							&& !item.getName().equals("geoCells")) {
						String displayName = null;
						if (((MappableField) item
								.getAnnotation(MappableField.class)) != null)
							displayName = ((MappableField) item
									.getAnnotation(MappableField.class))
									.displayName();
						attributesList.put(item.getName(), displayName);
					}

				}

				classAttributeMap.put(className, attributesList);
			} catch (ClassNotFoundException e) {
				logger.log(Level.SEVERE, "Class not found: " + className, e);
			}
		}
		return attributesList;
	}
}
