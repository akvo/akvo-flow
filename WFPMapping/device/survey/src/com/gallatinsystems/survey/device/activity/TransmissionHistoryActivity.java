package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.FileTransmission;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.view.adapter.FileTransmissionArrayAdapter;

/**
 * Activity to show the transmission history of all files in a survey submission
 * 
 * @author Christopher Fagiani
 * 
 */
public class TransmissionHistoryActivity extends ListActivity {

	private SurveyDbAdapter databaseAdapter;
	private Long respondentId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (savedInstanceState != null) {
			respondentId = savedInstanceState
					.getLong(ConstantUtil.RESPONDENT_ID_KEY);
		} else {
			Bundle extras = getIntent().getExtras();
			respondentId = extras != null ? extras
					.getLong(ConstantUtil.RESPONDENT_ID_KEY) : null;
		}
		setContentView(R.layout.transmissionhistory);
		databaseAdapter = new SurveyDbAdapter(this);
		databaseAdapter.open();
		getData();
	}

	private void getData() {
		ArrayList<FileTransmission> transmissionList = databaseAdapter
				.listFileTransmission(respondentId, null, false);
		FileTransmissionArrayAdapter adapter = new FileTransmissionArrayAdapter(
				this, R.layout.transmissionrow,
				transmissionList != null ? transmissionList
						: new ArrayList<FileTransmission>());
		setListAdapter(adapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			outState.putLong(ConstantUtil.RESPONDENT_ID_KEY, respondentId);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		if (databaseAdapter != null) {
			databaseAdapter.close();
		}
	}

}
