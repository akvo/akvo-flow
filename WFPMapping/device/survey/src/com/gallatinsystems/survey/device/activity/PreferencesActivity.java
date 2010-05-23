package com.gallatinsystems.survey.device.activity;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.service.LocationService;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Displays user editable preferences and takes care of persisting them to the
 * database;
 * 
 * @author Christopher Fagiani
 * 
 */
public class PreferencesActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private CheckBox saveUserCheckbox;
	private CheckBox beaconCheckbox;
	private TextView uploadOptionTextView;
	private TextView languageTextView;
	private TextView precacheHelpTextView;
	private TextView serverTextView;
	private SurveyDbAdapter database;
	private String[] languageArray;
	private String[] uploadArray;
	private String[] precacheHelpArray;
	private String[] serverArray;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.preferences);

		saveUserCheckbox = (CheckBox) findViewById(R.id.lastusercheckbox);
		beaconCheckbox = (CheckBox) findViewById(R.id.beaconcheckbox);

		uploadOptionTextView = (TextView) findViewById(R.id.uploadoptionvalue);
		languageTextView = (TextView) findViewById(R.id.surveylangvalue);
		precacheHelpTextView = (TextView) findViewById(R.id.precachehelpvalue);
		serverTextView = (TextView) findViewById(R.id.servervalue);

		Resources res = getResources();
		languageArray = res.getStringArray(R.array.languages);
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

		val = settings.get(ConstantUtil.LOCATION_BEACON_SETTING_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			beaconCheckbox.setChecked(true);
		} else {
			beaconCheckbox.setChecked(false);
		}

		val = settings.get(ConstantUtil.CELL_UPLOAD_SETTING_KEY);
		if (val != null) {
			uploadOptionTextView.setText(uploadArray[Integer.parseInt(val)]);
		}
		val = settings.get(ConstantUtil.SURVEY_LANG_SETTING_KEY);
		if (val != null) {
			languageTextView.setText(languageArray[Integer.parseInt(val)]);
		}
		val = settings.get(ConstantUtil.PRECACHE_HELP_SETTING_KEY);
		if (val != null) {
			precacheHelpTextView.setText(precacheHelpArray[Integer
					.parseInt(val)]);
		}

		val = settings.get(ConstantUtil.SERVER_SETTING_KEY);
		if (val != null) {
			serverTextView.setText(serverArray[Integer.parseInt(val)]);
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
		((ImageButton) findViewById(R.id.uploadoptionbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.suveylangbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.precachehelpbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.serverbutton))
				.setOnClickListener(this);
	}

	public void onPause() {
		super.onPause();
		database.close();
	}

	/**
	 * displays a pop-up dialog containing the upload or language options
	 * depending on what was clicked
	 */
	@Override
	public void onClick(View v) {
		if (R.id.uploadoptionbutton == v.getId()) {
			showPreferenceDialog(R.string.uploadoptiondialogtitle,
					R.array.celluploadoptions,
					ConstantUtil.CELL_UPLOAD_SETTING_KEY, uploadArray,
					uploadOptionTextView,
					uploadArray[ConstantUtil.UPLOAD_DATA_ALLWAYS_IDX],
					ConstantUtil.DATA_AVAILABLE_INTENT);
		} else if (R.id.suveylangbutton == v.getId()) {
			showPreferenceDialog(R.string.surveylanglabel, R.array.languages,
					ConstantUtil.SURVEY_LANG_SETTING_KEY, languageArray,
					languageTextView, null, null);
		} else if (R.id.precachehelpbutton == v.getId()) {
			showPreferenceDialog(R.string.precachehelpdialogtitle,
					R.array.precachehelpoptions,
					ConstantUtil.PRECACHE_HELP_SETTING_KEY, precacheHelpArray,
					precacheHelpTextView,
					precacheHelpArray[ConstantUtil.PRECACHE_HELP_ALLWAYS_IDX],
					ConstantUtil.PRECACHE_INTENT);
		} else if (R.id.serverbutton == v.getId()) {
			showPreferenceDialog(R.string.serverlabel, R.array.servers,
					ConstantUtil.SERVER_SETTING_KEY, serverArray,
					serverTextView, null, null);
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
						dialog.dismiss();
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
		} else {
			database.savePreference(ConstantUtil.LOCATION_BEACON_SETTING_KEY,
					"" + isChecked);
			if (isChecked) {
				// if the option changed, kick the service so it reflects the
				// change
				startService(new Intent(this, LocationService.class));
			} else {
				stopService(new Intent(this, LocationService.class));
			}
		}
	}
}
