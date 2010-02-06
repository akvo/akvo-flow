package com.gallatinsystems.survey.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * this class will listen to any Broadcast messages fired by the system and will
 * react to anything it knows how to handle. The intent filters need to be set
 * up correctly in the application manifest
 * 
 * @author Christopher Fagiani
 * 
 */
public class BroadcastDispatcher extends BroadcastReceiver {
	private static final String TAG = "BroadcastDispatcher";

	public static final String DATA_AVAILABLE_INTENT = "com.gallatinsystems.survey.device.DATA_SUBMITTED";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() != null
				&& DATA_AVAILABLE_INTENT.equals(intent.getAction())) {
			// launch the service telling it to SEND data to the server
			Intent i = new Intent(context, DataSyncService.class);
			i.putExtra(DataSyncService.TYPE_KEY, DataSyncService.SEND);
			context.startService(i);
		}
	}
}
