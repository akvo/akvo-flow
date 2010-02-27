package com.gallatinsystems.survey.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * this class will listen to any Broadcast messages fired by the system and will
 * react to anything it knows how to handle. The intent filters need to be set
 * up correctly in the application manifest
 * 
 * @author Christopher Fagiani
 * 
 */
public class BroadcastDispatcher extends BroadcastReceiver {
	@SuppressWarnings("unused")
	private static final String TAG = "BroadcastDispatcher";
	

	public void onReceive(Context context, Intent intent) {
		// right now, we do the same thing for all event types so need to check
		// the intent's action
		// launch the service telling it to SEND data to the server
		Intent i = new Intent(context, DataSyncService.class);
		i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.SEND);
		context.startService(i);
	}
}
