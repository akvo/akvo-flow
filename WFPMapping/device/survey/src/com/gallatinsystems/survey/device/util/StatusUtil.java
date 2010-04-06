package com.gallatinsystems.survey.device.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * utilities for checking system state
 * 
 * @author Christopher Fagiani
 * 
 */
public class StatusUtil {

	/**
	 * checks whether or not we have a usable data connection
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasDataConnection(Context context, boolean wifiOnly) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr != null) {
			NetworkInfo[] infoArr = connMgr.getAllNetworkInfo();
			if (infoArr != null) {
				for (int i = 0; i < infoArr.length; i++) {
					if (!wifiOnly) {
						// if we don't care what KIND of
						// connection we have, just that there is one
						if (NetworkInfo.State.CONNECTED == infoArr[i]
								.getState()) {
							return true;
						}
					} else {
						// if we only want to use wifi, we need to check the
						// type
						if (infoArr[i].getType() == ConnectivityManager.TYPE_WIFI
								&& NetworkInfo.State.CONNECTED == infoArr[i]
										.getState()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * gets the device's primary phone number
	 * 
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager teleMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return teleMgr.getLine1Number();
	}
}
