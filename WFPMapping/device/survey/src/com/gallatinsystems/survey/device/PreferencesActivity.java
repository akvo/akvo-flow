package com.gallatinsystems.survey.device;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
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
	private TextView uploadOptionTextView;
	private TextView languageTextView;
	private SurveyDbAdapter database;
	private String[] languageArray;
	private String[] uploadArray;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);

		saveUserCheckbox = (CheckBox) findViewById(R.id.lastusercheckbox);

		uploadOptionTextView = (TextView) findViewById(R.id.uploadoptionvalue);
		languageTextView = (TextView) findViewById(R.id.surveylangvalue);
		Resources res = getResources();
		languageArray = res.getStringArray(R.array.languages);
		uploadArray = res.getStringArray(R.array.celluploadoptions);
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

		val = settings.get(ConstantUtil.CELL_UPLOAD_SETTING_KEY);
		if (val != null) {
			uploadOptionTextView.setText(uploadArray[Integer.parseInt(val)]);
		}
		val = settings.get(ConstantUtil.SURVEY_LANG_SETTING_KEY);
		if (val != null) {
			languageTextView.setText(languageArray[Integer.parseInt(val)]);
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
		((ImageButton) findViewById(R.id.uploadoptionbutton))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.suveylangbutton))
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
					uploadOptionTextView);
		} else if (R.id.suveylangbutton == v.getId()) {
			showPreferenceDialog(R.string.surveylanglabel, R.array.languages,
					ConstantUtil.SURVEY_LANG_SETTING_KEY, languageArray,
					languageTextView);
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
	 */
	private void showPreferenceDialog(int titleId, int listId,
			final String settingKey, final String[] valueArray,
			final TextView currentValView) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId).setItems(listId,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						database.savePreference(settingKey, which + "");
						currentValView.setText(valueArray[which]);
					}
				});
		builder.show();
	}

	/**
	 * saves the value of the checkbox to the database
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		database.savePreference(ConstantUtil.USER_SAVE_SETTING_KEY, ""
				+ isChecked);
	}
}
