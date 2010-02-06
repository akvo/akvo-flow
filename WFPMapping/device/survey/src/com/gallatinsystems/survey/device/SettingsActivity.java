package com.gallatinsystems.survey.device;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Displays the settings menu and handles the user choices
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class SettingsActivity extends ListActivity {

	private static final String LABEL = "label";
	private static final String DESC = "desc";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsmenu);

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Resources resources = getResources();
		list.add(createMap(resources.getString(R.string.sendoptlabel),
				resources.getString(R.string.sendoptdesc)));
		list.add(createMap(resources.getString(R.string.exportoptlabel),
				resources.getString(R.string.exportoptdesc)));
		list.add(createMap(resources.getString(R.string.poweroptlabel),
				resources.getString(R.string.poweroptdesc)));
		String[] fromKeys = { LABEL, DESC };
		int[] toIds = { R.id.optionLabel, R.id.optionDesc };

		setListAdapter(new SimpleAdapter(this.getApplicationContext(), list,
				R.layout.settingsdetail, fromKeys, toIds));
	}

	private HashMap<String, String> createMap(String label, String desc) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(LABEL, label);
		map.put(DESC, desc);
		return map;
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		TextView label = (TextView) view.findViewById(R.id.optionLabel);
		if (label != null) {
			String val = label.getText().toString();
			Resources resources = getResources();
			if (resources.getString(R.string.poweroptlabel).equals(val)) {
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
				if (!wm.isWifiEnabled()) {
					wm.setWifiEnabled(true);
				} else {
					wm.setWifiEnabled(false);
				}
			} else {
				Intent i = new Intent(view.getContext(), DataSyncActivity.class);
				if (resources.getString(R.string.sendoptlabel).equals(val)) {
					i
							.putExtra(DataSyncActivity.TYPE_KEY,
									DataSyncActivity.SEND);
				} else {
					i.putExtra(DataSyncActivity.TYPE_KEY,
							DataSyncActivity.EXPORT);
				}
				i.putExtra(DataSyncActivity.FORCE_KEY, true);
				getApplicationContext().startService(i);
				// terminate this activity
				finish();
			}
		}
	}
}
