package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.service.DataSyncService;
import com.gallatinsystems.survey.device.util.ConstantUtil;

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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settingsmenu);

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		Resources resources = getResources();
		list.add(createMap(resources.getString(R.string.prefoptlabel),
				resources.getString(R.string.prefoptdesc)));
		list.add(createMap(resources.getString(R.string.sendoptlabel),
				resources.getString(R.string.sendoptdesc)));
		list.add(createMap(resources.getString(R.string.exportoptlabel),
				resources.getString(R.string.exportoptdesc)));
		list.add(createMap(resources.getString(R.string.poweroptlabel),
				resources.getString(R.string.poweroptdesc)));
		list.add(createMap(resources.getString(R.string.gpsstatuslabel),
				resources.getString(R.string.gpsstatusdesc)));
		list.add(createMap(resources.getString(R.string.aboutlabel), resources
				.getString(R.string.aboutdesc)));
		String[] fromKeys = { LABEL, DESC };
		int[] toIds = { R.id.optionLabel, R.id.optionDesc };

		setListAdapter(new SimpleAdapter(this.getApplicationContext(), list,
				R.layout.settingsdetail, fromKeys, toIds));
	}

	/**
	 * creates data structure for use in list adapter
	 * 
	 * @param label
	 * @param desc
	 * @return
	 */
	private HashMap<String, String> createMap(String label, String desc) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(LABEL, label);
		map.put(DESC, desc);
		return map;
	}

	/**
	 * when an item is clicked, use the label value to determine what option it
	 * was and then handle that type of action
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		TextView label = (TextView) view.findViewById(R.id.optionLabel);
		if (label != null) {
			String val = label.getText().toString();
			Resources resources = getResources();
			if(resources.getString(R.string.prefoptlabel).equals(val)){
				Intent i = new Intent(this, PreferencesActivity.class);
				startActivity(i);
			}
			else if (resources.getString(R.string.poweroptlabel).equals(val)) {
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
				if (!wm.isWifiEnabled()) {
					wm.setWifiEnabled(true);
				} else {
					wm.setWifiEnabled(false);
				}
			} else if (resources.getString(R.string.gpsstatuslabel).equals(val)) {
				try {
					Intent i = new Intent(ConstantUtil.GPS_STATUS_INTENT);
					startActivity(i);
				} catch (Exception e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					TextView tipText = new TextView(this);
					tipText.setText(R.string.nogpsstatus);
					builder.setView(tipText);
					builder.setPositiveButton(R.string.okbutton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
					builder.show();
				}
			} else if (resources.getString(R.string.aboutlabel).equals(val)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				TextView tipText = new TextView(this);
				tipText.setText(R.string.abouttext);
				builder.setTitle(R.string.abouttitle);
				builder.setView(tipText);
				builder.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();
			} else {
				Intent i = new Intent(view.getContext(), DataSyncService.class);
				if (resources.getString(R.string.sendoptlabel).equals(val)) {
					i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.SEND);
				} else {
					i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.EXPORT);
				}
				i.putExtra(ConstantUtil.FORCE_KEY, true);
				getApplicationContext().startService(i);
				// terminate this activity
				finish();
			}
		}
	}
}
