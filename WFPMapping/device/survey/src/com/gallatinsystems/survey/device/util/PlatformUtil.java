package com.gallatinsystems.survey.device.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * Utilities class to provide Android related functionalities
 *
 */
public class PlatformUtil {
	private static final String TAG = PlatformUtil.class.getSimpleName();
	
	/**
	 * Get the version name assigned in AndroidManifest.xml
	 * @param context
	 * @return versionName
	 */
	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
			return "";
		}
	}

}
