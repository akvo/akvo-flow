package com.gallatinsystems.survey.device.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.service.DataSyncService;

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
				.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								parentContext
										.startActivity(new Intent(
												"android.settings.LOCATION_SOURCE_SETTINGS"));
							}
						}).setNegativeButton(R.string.cancelbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		builder.show();
	}

	/**
	 * displays a simple dialog box with only a single, positive button using
	 * the resource ids of the strings passed in for the title and text.
	 * 
	 * @param titleId
	 * @param textId
	 * @param parentContext
	 */
	public static void showConfirmDialog(int titleId, int textId,
			Context parentContext) {
		showConfirmDialog(titleId, textId, parentContext, false,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
	}

	/**
	 * displays a simple dialog box with a single positive button and an
	 * optional (based on a flag) cancel button using the resource ids of the
	 * strings passed in for the title and text.
	 * 
	 * @param titleId
	 * @param textId
	 * @param parentContext
	 */
	public static void showConfirmDialog(int titleId, int textId,
			Context parentContext, boolean includeNegative,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		TextView tipText = new TextView(parentContext);
		builder.setTitle(titleId);
		tipText.setText(textId);
		builder.setView(tipText);
		builder.setPositiveButton(R.string.okbutton, listener);
		if (includeNegative) {
			builder.setNegativeButton(R.string.cancelbutton,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
		}
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
			Context context, int id, Integer iconId) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notifcationMgr = (NotificationManager) context
				.getSystemService(ns);
		int icon = R.drawable.info;
		if (iconId != null) {
			icon = iconId;
		}
		Notification notification = new Notification(icon, headline, System
				.currentTimeMillis());
		Intent notificationIntent = new Intent(context, DataSyncService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, headline, body, contentIntent);
		notifcationMgr.notify(id, notification);
	}

	/**
	 * cancels a previously fired notification
	 * 
	 * @param id
	 */
	public static void cancelNotification(int id, Context context) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notifcationMgr = (NotificationManager) context
				.getSystemService(ns);
		notifcationMgr.cancel(id);
	}

	/**
	 * displays a dialog box for selection of one or more survey languages
	 */
	public static void displayLanguageSelector(final Context context,
			final boolean[] selections,
			final DialogInterface.OnClickListener listener) {
		displaySelectionDialog(context, selections, listener,
				R.string.surveylanglabel, R.array.languages, true,
				R.string.langmandatorytitle, R.string.langmandatorytext);
	}

	/**
	 * displays a dialog box for selection of one or more countries
	 */
	public static void displayCountrySelector(final Context context,
			final boolean[] selections,
			final DialogInterface.OnClickListener listener) {
		displaySelectionDialog(context, selections, listener,
				R.string.cacheptcountrylabel, R.array.countries, false, 0, 0);
	}

	/**
	 * displays a dialog box for allowing selection of values from an array
	 * resource
	 * 
	 * @param context
	 * @param selections
	 * @param listener
	 * @param labelResourceId
	 * @param valueArrayResourceId
	 * @param selectionMandatory
	 * @param mandatoryTitleResourceId
	 * @param mandatoryTextResourceId
	 */
	private static void displaySelectionDialog(final Context context,
			final boolean[] selections,
			final DialogInterface.OnClickListener listener,
			final int labelResourceId, final int valueArrayResourceId,
			final boolean selectionMandatory,
			final int mandatoryTitleResourceId,
			final int mandatoryTextResourceId) {
		AlertDialog dia = new AlertDialog.Builder(context).setTitle(
				labelResourceId).setMultiChoiceItems(valueArrayResourceId,
				selections, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							break;
						}
					}
				}).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						boolean isValid = false;
						if (selectionMandatory) {
							for (int i = 0; i < selections.length; i++) {
								if (selections[i]) {
									isValid = true;
									break;
								}
							}
						} else {
							isValid = true;
						}
						if (isValid) {
							listener.onClick(dialog, which);
						} else {
							showConfirmDialog(mandatoryTitleResourceId,
									mandatoryTextResourceId, context, false,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											displaySelectionDialog(context,
													selections, listener,
													labelResourceId,
													valueArrayResourceId,
													selectionMandatory,
													mandatoryTitleResourceId,
													mandatoryTextResourceId);
										}
									});
						}
					}
				}).create();
		dia.show();
	}

	/**
	 * shows an authentication dialog that asks for the administrator passcode
	 * 
	 * @param parentContext
	 * @param listener
	 */
	public static void showAdminAuthDialog(final Context parentContext,
			final AdminAuthDialogListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		LinearLayout main = new LinearLayout(parentContext);
		main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		main.setOrientation(LinearLayout.VERTICAL);
		TextView tipText = new TextView(parentContext);
		builder.setTitle(R.string.authtitle);
		tipText.setText(R.string.authtext);
		main.addView(tipText);
		final EditText input = new EditText(parentContext);
		main.addView(input);
		builder.setView(main);
		builder.setPositiveButton(R.string.okbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String val = input.getText().toString();
						if (ConstantUtil.ADMIN_AUTH_CODE.equals(val)) {
							listener.onAuthenticated();
							dialog.dismiss();
						} else {
							showConfirmDialog(R.string.authfailed,
									R.string.invalidpassword, parentContext);
							dialog.dismiss();
						}
					}
				});

		builder.setNegativeButton(R.string.cancelbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();
	}

	/**
	 * interface that should be implemented by uses of the AdminAuthDialog to be
	 * notified when authorization is successful
	 * 
	 * 
	 * 
	 */
	public interface AdminAuthDialogListener {
		void onAuthenticated();
	}
}
