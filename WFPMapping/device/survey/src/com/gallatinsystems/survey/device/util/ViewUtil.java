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
		builder.setMessage(R.string.geodialog)
				.setCancelable(true)
				.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								parentContext
										.startActivity(new Intent(
												"android.settings.LOCATION_SOURCE_SETTINGS"));
							}
						})
				.setNegativeButton(R.string.cancelbutton,
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
						if (dialog != null) {
							dialog.cancel();
						}
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
		showConfirmDialog(titleId, textId, parentContext, includeNegative,
				listener, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog != null) {
							dialog.dismiss();
						}
					}
				});
	}

	/**
	 * displays a simple dialog box with a single positive button and an
	 * optional (based on a flag) cancel button using the resource ids of the
	 * strings passed in for the title and text. users can install listeners for
	 * both the positive and negative buttons
	 * 
	 * @param titleId
	 * @param textId
	 * @param parentContext
	 * @param includeNegative
	 * @param positiveListener
	 *            - if includeNegative is false, this will also be bound to the
	 *            cancel handler
	 * @param negativeListener
	 *            - only used if includeNegative is true - if the negative
	 *            listener is non-null, it will also be bound to the cancel
	 *            listener so pressing back to dismiss the dialog will have the
	 *            same effect as clicking the negative button.
	 */
	public static void showConfirmDialog(int titleId, int textId,
			Context parentContext, boolean includeNegative,
			final DialogInterface.OnClickListener positiveListener,
			final DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		TextView tipText = new TextView(parentContext);
		builder.setTitle(titleId);
		tipText.setText(textId);
		builder.setView(tipText);
		builder.setPositiveButton(R.string.okbutton, positiveListener);
		if (includeNegative) {
			builder.setNegativeButton(R.string.cancelbutton, negativeListener);
			if (negativeListener != null) {
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						negativeListener.onClick(dialog, -1);
					}
				});
			}
		} else if (positiveListener != null) {
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					positiveListener.onClick(dialog, -1);
				}
			});
		}

		builder.show();
	}

	/**
	 * displays a simple dialog box with a single positive button and an
	 * optional (based on a flag) cancel button using the resource id of the
	 * string passed in for the title, and a String parameter for the text.
	 * users can install listeners for both the positive and negative buttons
	 * 
	 * @param titleId
	 * @param text
	 * @param parentContext
	 * @param includeNegative
	 * @param positiveListener
	 *            - if includeNegative is false, this will also be bound to the
	 *            cancel handler
	 * @param negativeListener
	 *            - only used if includeNegative is true - if the negative
	 *            listener is non-null, it will also be bound to the cancel
	 *            listener so pressing back to dismiss the dialog will have the
	 *            same effect as clicking the negative button.
	 */
	public static void showConfirmDialog(int titleId, String text,
			Context parentContext, boolean includeNegative,
			final DialogInterface.OnClickListener positiveListener,
			final DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		TextView tipText = new TextView(parentContext);
		builder.setTitle(titleId);
		tipText.setText(text);
		builder.setView(tipText);
		builder.setPositiveButton(R.string.okbutton, positiveListener);
		if (includeNegative) {
			builder.setNegativeButton(R.string.cancelbutton, negativeListener);
			if (negativeListener != null) {
				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						negativeListener.onClick(dialog, -1);
					}
				});
			}
		} else if (positiveListener != null) {
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					positiveListener.onClick(dialog, -1);
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
		Notification notification = new Notification(icon, headline,
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(context, DataSyncService.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, headline, body, contentIntent);
		notifcationMgr.notify(id, notification);
	}

	public static void fireNotification(String headline, String body,
			Context context, int id, Integer iconId, Intent notificationIntent,
			boolean includeSound) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notifcationMgr = (NotificationManager) context
				.getSystemService(ns);
		int icon = android.R.drawable.ic_dialog_info;
		if (iconId != null) {
			icon = iconId;
		}
		Notification notification = new Notification(icon, headline,
				System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
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
		AlertDialog dia = new AlertDialog.Builder(context)
				.setTitle(labelResourceId)
				.setMultiChoiceItems(valueArrayResourceId, selections,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								switch (which) {
								case DialogInterface.BUTTON_POSITIVE:
									break;
								}
							}
						})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

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
											if (dialog != null) {
												dialog.dismiss();
											}
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
		final EditText input = new EditText(parentContext);
		input.setSingleLine();
		ShowTextInputDialog(parentContext, R.string.authtitle,
				R.string.authtext, input,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String val = input.getText().toString();
						if (ConstantUtil.ADMIN_AUTH_CODE.equals(val)) {
							listener.onAuthenticated();
							if (dialog != null) {
								dialog.dismiss();
							}
						} else {
							showConfirmDialog(R.string.authfailed,
									R.string.invalidpassword, parentContext);
							if (dialog != null) {
								dialog.dismiss();
							}
						}
					}
				});
	}

	/**
	 * shows a dialog that prompts the user to enter a single text value as
	 * input
	 * 
	 * @param parentContext
	 * @param title
	 * @param text
	 * @param clickListener
	 */
	public static void ShowTextInputDialog(final Context parentContext,
			int title, int text, EditText inputView,
			DialogInterface.OnClickListener clickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(parentContext);
		LinearLayout main = new LinearLayout(parentContext);
		main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		main.setOrientation(LinearLayout.VERTICAL);
		TextView tipText = new TextView(parentContext);
		builder.setTitle(title);
		tipText.setText(text);
		main.addView(tipText);
		main.addView(inputView);
		builder.setView(main);
		builder.setPositiveButton(R.string.okbutton, clickListener);

		builder.setNegativeButton(R.string.cancelbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dialog != null) {
							dialog.dismiss();
						}
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
