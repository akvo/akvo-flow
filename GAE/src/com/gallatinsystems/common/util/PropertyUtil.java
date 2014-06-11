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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Singleton for accessing system properties.
 * 
 * @author Dru Borden
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
