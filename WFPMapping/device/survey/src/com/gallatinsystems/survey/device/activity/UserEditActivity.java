package com.gallatinsystems.survey.device.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * this activity is used to edit a user's profile information and persist it to
 * the database.
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserEditActivity extends Activity {
	private EditText displayName;
	private EditText emailAddr;
	private Long userId;
	private SurveyDbAdapter databaseAdaptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.useredit);

		displayName = (EditText) findViewById(R.id.displayNameField);
		emailAddr = (EditText) findViewById(R.id.emailField);

		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();

		Button saveButton = (Button) findViewById(R.id.confirm);

		userId = savedInstanceState != null ? savedInstanceState
				.getLong(ConstantUtil.ID_KEY) : null;
		if (userId == null || userId == 0L) {
			Bundle extras = getIntent().getExtras();
			userId = extras != null ? new Long(extras
					.getString(ConstantUtil.ID_KEY)) : null;
		}
		populateFields();

		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				saveState();
				setResult(RESULT_OK);
				finish();
			}
		});
	}

	/**
	 * put loaded data into the views for display
	 */
	private void populateFields() {
		if (userId != null) {
			Cursor user = databaseAdaptor.findUser(userId);
			startManagingCursor(user);
			if (user.getCount() > 0) {
				displayName.setText(user.getString(user
						.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
				emailAddr.setText(user.getString(user
						.getColumnIndexOrThrow(SurveyDbAdapter.EMAIL_COL)));
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null && userId != null) {
			outState.putLong(ConstantUtil.ID_KEY, userId);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();		
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
	}

	/**
	 * save the name and email address to the db
	 */
	private void saveState() {
		String name = displayName.getText().toString();
		String email = emailAddr.getText().toString();
		name = cleanupString(name);
		email = cleanupString(name);
		databaseAdaptor.createOrUpdateUser(userId, name, email);
	}

	private String cleanupString(String input) {
		if (input != null) {
			input = input.trim();
			input = input.replaceAll("\n", " ");
			input = input.replaceAll(",", " ");
		}
		return input;
	}
}
