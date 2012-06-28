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

import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources;
import android.util.Log;

import com.gallatinsystems.survey.device.R;

/**
 * Utility for populating a Properties object from the contents of a well-known
 * property file in the raw resource directory.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PropertyUtil {
	private static final String TAG = "PROPERTY_UTIL";
	private static Properties properties = null;

	/**
	 * reads the property file from the apk and returns the contents in a
	 * Properties object.
	 * 
	 * @param resources
	 * @return
	 */
	private static void loadProperties(Resources resources) {
		if (properties == null) {
			properties = new Properties();
			try {				
				InputStream rawResource = resources
						.openRawResource(R.raw.survey);
				properties.load(rawResource);
			} catch (Exception e) {
				Log.e(TAG, "Coult not load properties", e);
			}
		}
	}

	public PropertyUtil(Resources resources) {
		loadProperties(resources);
	}

	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

}
