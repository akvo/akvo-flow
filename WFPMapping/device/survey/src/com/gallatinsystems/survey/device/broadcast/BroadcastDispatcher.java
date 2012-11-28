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

package com.gallatinsystems.survey.device.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gallatinsystems.survey.device.service.DataSyncService;
import com.gallatinsystems.survey.device.service.SurveyDownloadService;
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

		if (ConstantUtil.DATA_AVAILABLE_INTENT.equals(intent.getAction())) {
			// launch the service telling it to SEND data to the server
			Intent i = new Intent(context, DataSyncService.class);
			i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.SEND);
			context.startService(i);
		} else if (ConstantUtil.PRECACHE_INTENT.equals(intent.getAction())) {
			context.startService(new Intent(context,
					SurveyDownloadService.class));
		}
	}
}
