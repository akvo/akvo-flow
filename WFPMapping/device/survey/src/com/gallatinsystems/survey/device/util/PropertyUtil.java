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
