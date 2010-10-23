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

	/**
	 * reads the property file from the apk and returns the contents in a
	 * Properties object.
	 * 
	 * @param resources
	 * @return
	 */
	public static Properties loadProperties(Resources resources) {
		Properties properties = new Properties();
		try {
			InputStream rawResource = resources.openRawResource(R.raw.survey);
			properties.load(rawResource);
		} catch (Exception e) {
			Log.e(TAG, "Coult not load properties", e);
		}
		return properties;
	}
}
