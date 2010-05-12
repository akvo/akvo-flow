package com.gallatinsystems.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class ClassAttributeUtil {
	private static HashMap<String, Map<String, String>> classAttributeMap = new HashMap<String, Map<String, String>>();

	public static HashMap<String, String> listObjectAttributes(String className) {
		Map<String,String> attributesList = classAttributeMap
				.get(className);
		if (attributesList == null) {
			attributesList = new HashMap<String, String>();
			Class cls;
			try {
				cls = Class.forName(className);

				for (Field item : cls.getDeclaredFields()) {
					if (!item.getName().contains("jdo")
							&& !item.getName().equals("serialVersionUID")
							&& !item.getName().equals("geoCells")) {
						String displayName = null;
						if (((DisplayName) item
								.getAnnotation(DisplayName.class)) != null)
							displayName = ((DisplayName) item
									.getAnnotation(DisplayName.class)).value();
						attributesList.put(item.getName(), displayName);
					}

				}
				
				Map<String,String> sortedMap = new TreeMap<String,String>(attributesList);
				classAttributeMap.put(className, sortedMap);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (HashMap)attributesList;
	}

	public static void main(String[] args) {

		for (Entry<String,String> item : listObjectAttributes("org.waterforpeople.mapping.domain.AccessPoint").entrySet()) {
			System.out.println(item.getKey() + " " + item.getValue());
		}
	}

}
