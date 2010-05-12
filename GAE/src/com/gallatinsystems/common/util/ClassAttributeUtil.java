package com.gallatinsystems.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ClassAttributeUtil {
	private static HashMap<String, ArrayList<String>> classAttributeMap = new HashMap<String, ArrayList<String>>();

	public static ArrayList<String> listObjectAttributes(String className) {
		ArrayList<String> attributesList = classAttributeMap.get(className);
		if (attributesList == null) {
			attributesList = new ArrayList<String>();
			Class cls;
			try {
				cls = Class.forName(className);

				for (Field item : cls.getDeclaredFields()) {
					if (!item.getName().contains("jdo")
							&& !item.getName().equals("serialVersionUID")
							&& !item.getName().equals("geoCells"))
						attributesList.add(item.getName());
				}
				Collections.sort(attributesList);
				classAttributeMap.put(className, attributesList);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return attributesList;
	}

}
