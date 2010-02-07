package com.gallatinsystems.survey.device;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
		setContentView(R.layout.useredit);

		displayName = (EditText) findViewById(R.id.displayNameField);
		emailAddr = (EditText) findViewById(R.id.emailField);

		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();

		Button saveButton = (Button) findViewById(R.id.confirm);

		userId = savedInstanceState != null ? savedInstanceState
				.getLong(SurveyDbAdapter.USER_ID_COL) : null;
		if (userId == null) {
			Bundle extras = getIntent().getExtras();
			userId = extras != null ? extras
					.getLong(SurveyDbAdapter.USER_ID_COL) : null;
		}
		populateFields();

		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
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
			Cursor user = databaseAdaptor.fetchUser(userId);
			startManagingCursor(user);
			displayName.setText(user.getString(user
					.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
			emailAddr.setText(user.getString(user
					.getColumnIndexOrThrow(SurveyDbAdapter.EMAIL_COL)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(SurveyDbAdapter.USER_ID_COL, userId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	protected void onDestroy(){
		super.onDestroy();
		if(databaseAdaptor != null){
			databaseAdaptor.close();
		}
	}
	
	/**
	 * save the name and email address to the db
	 */
	private void saveState() {
		String name = displayName.getText().toString();
		String email = emailAddr.getText().toString();

		databaseAdaptor.createOrUpdateUser(userId, name, email);
	}
}
