package com.gallatinsystems.survey.device.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.gallatinsystems.survey.device.DataSyncService;
import com.gallatinsystems.survey.device.R;

/**
 * Utility class to handle common features for the View tier
 * 
 * @author Christopher Fagiani
 * 
 */
public class ViewUtil {

	/**
	 * displays the alert dialog box warning that the GPS receiver is off. If
	 * the affirmative button is clicked, the Location Settings panel is
	 * launched. If the negative button is clicked, it will just close the
	 * dialog
	 * 
	 * @param parentContext
	 */
	public static void showGPSDialog(final Context parentContext) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		builder.setMessage(R.string.geodialog).setCancelable(true)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						parentContext.startActivity(new Intent(
								"android.settings.LOCATION_SOURCE_SETTINGS"));
					}
				}).setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
	}

	/**
	 * displays a notification in the system status bar
	 * 
	 * @param headline
	 *            - headline to display in notification bar
	 * @param body
	 *            - body of notification (when user expands bar)
	 * @param context
	 * @param id
	 *            - unique (within app) ID of notification
	 */
	public static void fireNotification(String headline, String body,
			Context context, int id) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);
		int icon = R.drawable.info;
		Notification notification = new Notification(icon, headline, System
				.currentTimeMillis());
		Intent notificationIntent = new Intent(context, DataSyncService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, headline, body, contentIntent);
		mNotificationManager.notify(id, notification);
	}
}
