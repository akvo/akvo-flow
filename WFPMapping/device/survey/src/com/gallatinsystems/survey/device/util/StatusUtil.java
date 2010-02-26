package com.gallatinsystems.survey.device.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
	public static boolean hasDataConnection(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr != null) {
			NetworkInfo[] infoArr = connMgr.getAllNetworkInfo();
			if (infoArr != null) {
				for (int i = 0; i < infoArr.length; i++) {
					if (NetworkInfo.State.CONNECTED == infoArr[i].getState()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
