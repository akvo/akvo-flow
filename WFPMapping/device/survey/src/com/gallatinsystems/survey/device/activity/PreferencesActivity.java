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

package com.gallatinsystems.survey.device.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.service.LocationService;
import com.gallatinsystems.survey.device.util.ArrayPreferenceData;
import com.gallatinsystems.survey.device.util.ArrayPreferenceUtil;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

/**
 * Displays user editable preferences and takes care of persisting them to the
 * database. Some options require the user to enter an administrator passcode
 * via a dialog box before the operation can be performed.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PreferencesActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private CheckBox saveUserCheckbox;
	private CheckBox beaconCheckbox;
	private CheckBox screenOnCheckbox;
	private CheckBox photoSizeReminderCheckbox;
	private CheckBox shrinkPhotosCheckbox;
	private TextView uploadOptionTextView;
	private TextView languageTextView;
	private TextView precacheHelpTextView;
	private TextView precachePointsTextView;
	private TextView surveyUpdateTextView;
	private TextView uploadErrorTextView;
	private TextView serverTextView;
	private TextView identTextView;
	private TextView radiusTextView;
	private SurveyDbAdapter database;
	private String[] languageArray;
	private boolean[] selectedLanguages;
	private String[] precacheCountryArray;
	private boolean[] selectedPrecacheCountries;
	private String[] uploadArray;
	private String[] precacheHelpArray;
	private String[] serverArray;
	private PropertyUtil props;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.preferences);

		saveUserCheckbox = (CheckBox) findViewById(R.id.lastusercheckbox);
		beaconCheckbox = (CheckBox) findViewById(R.id.beaconcheckbox);
		screenOnCheckbox = (CheckBox) findViewById(R.id.screenoptcheckbox);
		photoSizeReminderCheckbox = (CheckBox) findViewById(R.id.photosizeremindercheckbox);
		shrinkPhotosCheckbox = (CheckBox) findViewById(R.id.shrinkphotoscheckbox);

		uploadOptionTextView = (TextView) findViewById(R.id.uploadoptionvalue);
		languageTextView = (TextView) findViewById(R.id.surveylangvalue);
		precachePointsTextView = (TextView) findViewById(R.id.cacheptcountryvalue);
		precacheHelpTextView = (TextView) findViewById(R.id.precachehelpvalue);

		surveyUpdateTextView = (TextView) findViewById(R.id.surveycheckvalue);
		uploadErrorTextView = (TextView) findViewById(R.id.uploaderrorvalue);
		serverTextView = (TextView) findViewById(R.id.servervalue);
		identTextView = (TextView) findViewById(R.id.identvalue);
		radiusTextView = (TextView) findViewById(R.id.radiusvalue);

		Resources res = getResources();
		props = new PropertyUtil(res);


		uploadArray = res.getStringArray(R.array.celluploadoptions);
		precacheHelpArray = res.getStringArray(R.array.precachehelpoptions);
		serverArray = res.getStringArray(R.array.servers);
	}

	/**
	 * loads the preferences from the DB and sets their current value in the UI
	 */
	private void populateFields() {
		HashMap<String, String> settings = database.listPreferences();
		String val = settings.get(ConstantUtil.USER_SAVE_SETTING_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			saveUserCheckbox.setChecked(true);
		} else {
			saveUserCheckbox.setChecked(false);
		}

		val = settings.get(ConstantUtil.SCREEN_ON_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			screenOnCheckbox.setChecked(true);
		} else {
			screenOnCheckbox.setChecked(false);
		}

		val = settings.get(ConstantUtil.LOCATION_BEACON_SETTING_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			beaconCheckbox.setChecked(true);
		} else {
			beaconCheckbox.setChecked(false);
		}

		val = settings.get(ConstantUtil.PHOTO_SIZE_REMINDER_KEY);
		photoSizeReminderCheckbox.setChecked(val != null && Boolean.parseBoolean(val));

		val = settings.get(ConstantUtil.SHRINK_PHOTOS_KEY);
		shrinkPhotosCheckbox.setChecked(val != null && Boolean.parseBoolean(val));

		val = settings.get(ConstantUtil.CELL_UPLOAD_SETTING_KEY);
		if (val != null) {
			uploadOptionTextView.setText(uploadArray[Integer.parseInt(val)]);
		}
		val = settings.get(ConstantUtil.SURVEY_LANG_SETTING_KEY);
		ArrayPreferenceData langs = ArrayPreferenceUtil.loadArray(this, val,
				R.array.languages);
		languageArray = langs.getItems();
		selectedLanguages = langs.getSelectedItems();
		languageTextView.setText(ArrayPreferenceUtil.formSelectedItemString(
				languageArray, selectedLanguages));

		val = settings.get(ConstantUtil.PRECACHE_POINT_COUNTRY_KEY);
		ArrayPreferenceData precacheCountries = ArrayPreferenceUtil.loadArray(
				this, val, R.array.countries);
		precacheCountryArray = precacheCountries.getItems();
		selectedPrecacheCountries = precacheCountries.getSelectedItems();
		precachePointsTextView.setText(ArrayPreferenceUtil
				.formSelectedItemString(precacheCountryArray,
						selectedPrecacheCountries));

		val = settings.get(ConstantUtil.PRECACHE_SETTING_KEY);
		if (val != null) {
			precacheHelpTextView.setText(precacheHelpArray[Integer
					.parseInt(val)]);
		}

		val = settings.get(ConstantUtil.CHECK_FOR_SURVEYS);
		if (val != null) {
			surveyUpdateTextView.setText(precacheHelpArray[Integer
					.parseInt(val)]);
		}

		val = settings.get(ConstantUtil.UPLOAD_ERRORS);
		if (val != null) {
			uploadErrorTextView
					.setText(precacheHelpArray[Integer.parseInt(val)]);
		}

		val = settings.get(ConstantUtil.SERVER_SETTING_KEY);
		if (val != null && val.trim().length() > 0) {
			serverTextView.setText(serverArray[Integer.parseInt(val)]);
		} else {
			serverTextView.setText(props.getProperty(ConstantUtil.SERVER_BASE));
		}
		
		val = settings.get(ConstantUtil.DEVICE_IDENT_KEY);
		if (val != null) {
			identTextView.setText(val);
		}
		
		val = settings.get(ConstantUtil.NEARBY_RADIUS);
		if (val != null) {
			radiusTextView.setText(Double.parseDouble(val)/1000.0+" km");
		}
		

	}

	/**
	 * opens db connection and sets up listeners (after we hydrate values so we
	 * don't trigger the onCheckChanged listener when we set initial values)
	 */
	public void onResume() {
		super.onResume();
		database = new SurveyDbAdapter(this);
		database.open();
		populateFields();
		saveUserCheckbox.setOnCheckedChangeListener(this);
		beaconCheckbox.setOnCheckedChangeListener(this);
		screenOnCheckbox.setOnCheckedChangeListener(this);
		photoSizeReminderCheckbox.setOnCheckedChangeListener(this);
		shrinkPhotosCheckbox.setOnCheckedChangeListener(this);
		((ImageButton) findViewById(R.id.uploadoptionbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.surveylangbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.precachepointbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.precachehelpbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.serverbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.identbutton)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.surveycheckbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.uploaderrorbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.radiusbutton))
				.setOnClickListener(this);
	}

	public void onPause() {	
		database.close();
		super.onPause();
	}

	/**
	 * displays a pop-up dialog containing the upload or language options
	 * depending on what was clicked
	 */
	@Override
	public void onClick(View v) {
		if (R.id.radiusbutton == v.getId()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle(R.string.radiuslabel);
			alert.setMessage(R.string.radiusprompt);

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			//make it accept numbers only; no negatives, decimals ok
			input.setKeyListener( new DigitsKeyListener(false, true) );
			alert.setView(input);

			alert.setPositiveButton(R.string.okbutton, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String radiusValue = input.getText().toString();
				try {//validate input
					Double nearbyRadius = Double.parseDouble(radiusValue)*1000.0;
					//save to DB
					database.savePreference(ConstantUtil.NEARBY_RADIUS,nearbyRadius.toString());
					//Show it
					radiusTextView.setText(radiusValue + " km");
				}
				catch (NumberFormatException e){
					/*could complain here*/
					};
				
			  }
			});

			alert.setNegativeButton(R.string.cancelbutton, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
		} else if (R.id.uploadoptionbutton == v.getId()) {
			showPreferenceDialog(R.string.uploadoptiondialogtitle,
					R.array.celluploadoptions,
					ConstantUtil.CELL_UPLOAD_SETTING_KEY, uploadArray,
					uploadOptionTextView,
					uploadArray[ConstantUtil.UPLOAD_DATA_ALLWAYS_IDX],
					ConstantUtil.DATA_AVAILABLE_INTENT);
		} else if (R.id.surveylangbutton == v.getId()) {
			ViewUtil.displayLanguageSelector(this, selectedLanguages,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int clicked) {
							database.savePreference(
									ConstantUtil.SURVEY_LANG_SETTING_KEY,
									ArrayPreferenceUtil
											.formPreferenceString(selectedLanguages));
							languageTextView.setText(ArrayPreferenceUtil
									.formSelectedItemString(languageArray,
											selectedLanguages));
							if(dialog!=null){
								dialog.dismiss();
							}
						}
					});
		} else if (R.id.precachehelpbutton == v.getId()) {
			showPreferenceDialog(R.string.precachehelpdialogtitle,
					R.array.precachehelpoptions,
					ConstantUtil.PRECACHE_SETTING_KEY, precacheHelpArray,
					precacheHelpTextView,
					precacheHelpArray[ConstantUtil.PRECACHE_ALWAYS_IDX],
					ConstantUtil.PRECACHE_INTENT);
		} else if (R.id.surveycheckbutton == v.getId()) {
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							showPreferenceDialog(R.string.surveychecklabel,
									R.array.precachehelpoptions,
									ConstantUtil.CHECK_FOR_SURVEYS,
									precacheHelpArray, surveyUpdateTextView,
									null, null);
						}
					});
		} else if (R.id.uploaderrorbutton == v.getId()) {
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							showPreferenceDialog(R.string.uploaderrorlabel,
									R.array.precachehelpoptions,
									ConstantUtil.UPLOAD_ERRORS,
									precacheHelpArray, uploadErrorTextView,
									null, null);
						}
					});
		} else if (R.id.serverbutton == v.getId()) {
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							showPreferenceDialogBase(R.string.serverlabel,
									props.getProperty(ConstantUtil.SERVER_BASE),
									ConstantUtil.SERVER_SETTING_KEY,
									serverArray, serverTextView);

						}
					});

		} else if (R.id.precachepointbutton == v.getId()) {
			ViewUtil.displayCountrySelector(this, selectedPrecacheCountries,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int clicked) {
							database.savePreference(
									ConstantUtil.PRECACHE_POINT_COUNTRY_KEY,
									ArrayPreferenceUtil
											.formPreferenceString(selectedPrecacheCountries));
							precachePointsTextView.setText(ArrayPreferenceUtil
									.formSelectedItemString(
											precacheCountryArray,
											selectedPrecacheCountries));
							if(dialog!=null){
								dialog.dismiss();
							}
						}
					});
		} else if (R.id.identbutton == v.getId()) {
			ViewUtil.showAdminAuthDialog(this,
					new ViewUtil.AdminAuthDialogListener() {
						@Override
						public void onAuthenticated() {
							final EditText inputView = new EditText(
									PreferencesActivity.this);
							ViewUtil.ShowTextInputDialog(
									PreferencesActivity.this,
									R.string.identlabel,
									R.string.setidentlabel, inputView,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											identTextView.setText(inputView
													.getText());
											database.savePreference(
													ConstantUtil.DEVICE_IDENT_KEY,
													inputView.getText()
															.toString());
										}
									});
						}
					});
		}
	}

	/**
	 * displays a dialog that allows the user to choose a setting from a string
	 * array
	 * 
	 * @param titleId
	 *            - resource id of dialog title
	 * @param listId
	 *            - resource id of item array
	 * @param settingKey
	 *            - key of setting to edit
	 * @param valueArray
	 *            - string array containing values
	 * @param currentValView
	 *            - view to update with value selected
	 * @param actionValue
	 *            - if the selected value matches this, then fire an intent with
	 *            the value of actionIntent
	 * @param actionIntent
	 *            - intent to fire if actionValue matches the selection
	 */
	private void showPreferenceDialog(int titleId, int listId,
			final String settingKey, final String[] valueArray,
			final TextView currentValView, final String actionValue,
			final String actionIntent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId).setItems(listId,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						database.savePreference(settingKey, which + "");
						currentValView.setText(valueArray[which]);
						if (actionValue != null && actionIntent != null) {
							if (valueArray[which].equals(actionValue)) {
								sendBroadcast(new Intent(actionIntent));
							}
						}
						if(dialog != null){
							dialog.dismiss();
						}
					}
				});
		builder.show();
	}
	/**
	 * displays a dialog that allows the user to choose a setting from a string
	 * array
	 * 
	 * @param titleId
	 *            - resource id of dialog title
	 * @param baseValue
	 *            - Value resulting in empty setting value
	 * @param settingKey
	 *            - key of setting to edit
	 * @param valueArray
	 *            - string array containing values
	 * @param currentValView
	 *            - view to update with value selected
	 */
	private void showPreferenceDialogBase(int titleId, String baseValue,
			final String settingKey, final String[] valueArray,
			final TextView currentValView) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String[] extraValueArray = new String[valueArray.length + 1];
		extraValueArray[0] = baseValue;
		for (int i = 0; i < valueArray.length; i++ ){
			extraValueArray[i+1] = valueArray[i];
		}
		builder.setTitle(titleId).setItems(extraValueArray,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0)
							database.savePreference(settingKey, "");
						else
							database.savePreference(settingKey, (which - 1) + "");
						currentValView.setText(extraValueArray[which]);
						if(dialog != null){
							dialog.dismiss();
						}
					}
				});
		builder.show();
	}

	/**
	 * saves the value of the checkbox to the database
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == saveUserCheckbox) {
			database.savePreference(ConstantUtil.USER_SAVE_SETTING_KEY, ""
					+ isChecked);
		} else if (buttonView == beaconCheckbox) {
			database.savePreference(ConstantUtil.LOCATION_BEACON_SETTING_KEY,
					"" + isChecked);
			if (isChecked) {
				// if the option changed, kick the service so it reflects the
				// change
				startService(new Intent(this, LocationService.class));
			} else {
				stopService(new Intent(this, LocationService.class));
			}
		} else if (buttonView == screenOnCheckbox) {
			database.savePreference(ConstantUtil.SCREEN_ON_KEY, "" + isChecked);
		} else if (buttonView == photoSizeReminderCheckbox) {
			database.savePreference(ConstantUtil.PHOTO_SIZE_REMINDER_KEY, "" + isChecked);
		} else if (buttonView == shrinkPhotosCheckbox) {
			database.savePreference(ConstantUtil.SHRINK_PHOTOS_KEY, "" + isChecked);
		}
	}
}
