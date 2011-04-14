package com.gallatinsystems.survey.device.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.Html;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.service.DataSyncService;
import com.gallatinsystems.survey.device.service.SurveyDownloadService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;

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
		list.add(createMap(resources.getString(R.string.reloadsurveyslabel),
				resources.getString(R.string.reloadsurveysdesc)));
		list.add(createMap(resources.getString(R.string.flushpointslabel),
				resources.getString(R.string.flushpointsdesc)));
		list.add(createMap(resources.getString(R.string.downloadsurveylabel),
				resources.getString(R.string.downloadsurveydesc)));
		list.add(createMap(resources.getString(R.string.resetall),
				resources.getString(R.string.resetalldesc)));
		list.add(createMap(resources.getString(R.string.checksd),
				resources.getString(R.string.checksddesc)));
		list.add(createMap(resources.getString(R.string.aboutlabel),
				resources.getString(R.string.aboutdesc)));

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
			if (resources.getString(R.string.prefoptlabel).equals(val)) {
				Intent i = new Intent(this, PreferencesActivity.class);
				startActivity(i);
			} else if (resources.getString(R.string.poweroptlabel).equals(val)) {
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
				String txt = resources.getString(R.string.abouttext) + " "
						+ resources.getString(R.string.appversion);
				tipText.setText(txt);
				builder.setTitle(R.string.abouttitle);
				builder.setView(tipText);
				builder.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				builder.show();
			} else if (resources.getString(R.string.reloadsurveyslabel).equals(
					val)) {
				ViewUtil.showAdminAuthDialog(this,
						new ViewUtil.AdminAuthDialogListener() {

							@Override
							public void onAuthenticated() {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										SettingsActivity.this);
								TextView tipText = new TextView(
										SettingsActivity.this);
								tipText.setText(R.string.reloadconftext);
								builder.setTitle(R.string.conftitle);
								builder.setView(tipText);
								builder.setPositiveButton(R.string.okbutton,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												SurveyDbAdapter database = new SurveyDbAdapter(
														SettingsActivity.this);
												database.open();
												database.deleteAllSurveys();
												database.close();
												getApplicationContext()
														.startService(
																new Intent(
																		SettingsActivity.this,
																		SurveyDownloadService.class));
											}
										});
								builder.setNegativeButton(
										R.string.cancelbutton,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});
								builder.show();
							}
						});

			} else if (resources.getString(R.string.downloadsurveylabel)
					.equals(val)) {
				ViewUtil.showAdminAuthDialog(this,
						new ViewUtil.AdminAuthDialogListener() {

							@Override
							public void onAuthenticated() {
								AlertDialog.Builder inputDialog = new AlertDialog.Builder(
										SettingsActivity.this);
								inputDialog
										.setTitle(R.string.downloadsurveylabel);
								inputDialog
										.setMessage(R.string.downloadsurveyinstr);

								// Set an EditText view to get user input
								final EditText input = new EditText(
										SettingsActivity.this);

								input.setKeyListener(new DigitsKeyListener(
										false, false));
								inputDialog.setView(input);

								inputDialog.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												String value = input.getText()
														.toString();
												if (value != null
														&& value.trim()
																.length() > 0) {
													if (value.trim()
															.equals("0")) {
														SurveyDbAdapter database = new SurveyDbAdapter(
																SettingsActivity.this);
														database.open();
														database.reinstallTestSurvey();
														database.close();
													} else {
														Intent downloadIntent = new Intent(
																SettingsActivity.this,
																SurveyDownloadService.class);
														downloadIntent
																.putExtra(
																		ConstantUtil.SURVEY_ID_KEY,
																		value);
														getApplicationContext()
																.startService(
																		downloadIntent);
													}
												}
											}
										});

								inputDialog.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												// Canceled.
											}
										});

								inputDialog.show();

							}
						});

			} else if (resources.getString(R.string.flushpointslabel).equals(
					val)) {
				ViewUtil.showAdminAuthDialog(this,
						new ViewUtil.AdminAuthDialogListener() {
							@Override
							public void onAuthenticated() {
								SurveyDbAdapter database = new SurveyDbAdapter(
										SettingsActivity.this);
								database.open();
								database.deleteAllPoints();
								database.close();
							}
						});
			} else if (resources.getString(R.string.resetall).equals(val)) {
				ViewUtil.showAdminAuthDialog(this,
						new ViewUtil.AdminAuthDialogListener() {
							@Override
							public void onAuthenticated() {
								SurveyDbAdapter database = new SurveyDbAdapter(
										SettingsActivity.this);
								database.open();
								database.clearAllData();
								database.close();
							}
						});
			} else if (resources.getString(R.string.checksd).equals(val)) {
				String state = Environment.getExternalStorageState();
				StringBuilder builder = new StringBuilder();
				if (state == null || !Environment.MEDIA_MOUNTED.equals(state)) {
					builder.append("<b>")
							.append(resources.getString(R.string.sdmissing))
							.append("</b><br>");
				} else {
					builder.append(resources.getString(R.string.sdmounted))
							.append("<br>");
					File f = Environment.getExternalStorageDirectory();
					if (f != null) {
						// normally, we could just do f.getFreeSpace() but that
						// would tie us to later versions of Android. So for
						// maximum compatibility, just use StatFS
						StatFs fs = new StatFs(f.getAbsolutePath());
						if (fs != null) {
							long space = fs.getFreeBlocks() * fs.getBlockSize();
							builder.append(
									resources.getString(R.string.sdcardspace))
									.append(String.format(" %.2f", (double)space/(double)(1024*1024)));
						}
					}
				}
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				TextView tipText = new TextView(this);
				tipText.setText(Html.fromHtml(builder.toString()),
						BufferType.SPANNABLE);
				dialog.setTitle(R.string.checksd);
				dialog.setView(tipText);
				dialog.setPositiveButton(R.string.okbutton,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				dialog.show();

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
