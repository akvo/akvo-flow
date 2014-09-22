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

package com.gallatinsystems.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for finding specific class annotations
 */
public class ClassAttributeUtil {
    private static HashMap<String, TreeMap<String, String>> classAttributeMap = new HashMap<String, TreeMap<String, String>>();
    private static final Logger logger = Logger
            .getLogger(ClassAttributeUtil.class.getName());

    @SuppressWarnings("rawtypes")
    public static TreeMap<String, String> listObjectAttributes(String className) {
        TreeMap<String, String> attributesList = classAttributeMap
                .get(className);
        if (attributesList == null) {
            attributesList = new TreeMap<String, String>();
            Class cls = null;
            try {
                cls = Class.forName(className);

                for (Field item : cls.getDeclaredFields()) {
                    // exclude JDO injected fields and the geoCells objects
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
