package com.gallatinsystems.survey.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;

/**
 * Displays the settings menu and handles the user choices
 * 
 *TODO: display highlighting when a item is clicked?
 * 
 * @author Christopher Fagiani
 * 
 */
public class SettingsActivity extends Activity implements OnClickListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsmenu);
		TableRow row = (TableRow) findViewById(R.id.sendOption);
		row.setOnClickListener(this);
		row = (TableRow) findViewById(R.id.exportDataOption);
		row.setOnClickListener(this);
		row = (TableRow) findViewById(R.id.powerMgmtOption);
		row.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.powerMgmtOption) {
			// TODO: launch the intent to show the power settings screen
			v.getContext().startActivity(
					new Intent(
							"android.settings.LOCATION_SOURCE_SETTINGS"));

		} else {
			Intent i = new Intent(v.getContext(), DataSyncActivity.class);
			if (v.getId() == R.id.sendOption) {
				i.putExtra(DataSyncActivity.TYPE_KEY, DataSyncActivity.SEND);
			} else {
				i.putExtra(DataSyncActivity.TYPE_KEY, DataSyncActivity.EXPORT);
			}
			startActivity(i);
			// terminate this activity
			finish();
		}
	}
}
