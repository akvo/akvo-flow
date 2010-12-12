package com.gallatinsystems.launcher.device.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gallatinsystems.launcher.device.R;
import com.gallatinsystems.launcher.device.domain.AppDescriptor;
import com.gallatinsystems.launcher.device.view.ApplicationArrayAdapter;
import com.gallatinsystems.launcher.device.view.ClippedImage;

/**
 * 
 * Home screen replacement application that locks down the phone to only allow
 * approved functions/apps
 * 
 * @author Christopher Fagiani
 */
public class FlowLauncher extends Activity {

	private static final int SETTINGS_MENU = Menu.FIRST + 1;

	private static ArrayList<AppDescriptor> filteredApps;
	private String[] approvedAppArray;

	private final BroadcastReceiver appInstallationReceiver = new ApplicationsIntentReceiver();

	private GridView appGrid;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		approvedAppArray = getResources().getStringArray(R.array.approvedapps);
		filteredApps = new ArrayList<AppDescriptor>();

		setContentView(R.layout.home);
		registerIntentReceivers();
		setWallpaper();
		loadApplications();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// Close the menu
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
			getWindow().closeAllPanels();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Remove the callback for the cached drawables or we leak
		// the previous Home screen on orientation change
		final int count = filteredApps.size();
		for (int i = 0; i < count; i++) {
			filteredApps.get(i).getIcon().setCallback(null);
		}

		unregisterReceiver(appInstallationReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * register ourself to listen for application
	 * installations/updates/removalsF
	 */
	private void registerIntentReceivers() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		filter.addDataScheme("package");
		registerReceiver(appInstallationReceiver, filter);
	}

	/**
	 * render the wallpaper
	 */
	private void setWallpaper() {
		Drawable wallpaper = WallpaperManager.getInstance(this).peekDrawable();
		if (wallpaper != null) {
			getWindow().setBackgroundDrawable(new ClippedImage(wallpaper));
		}
	}

	/**
	 * presents the survey options menu when the user presses the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SETTINGS_MENU, 0, R.string.settingsoption);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case SETTINGS_MENU:
			authorizeSettingsSelection();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * lists all applications installed on the system
	 * 
	 * @param manager
	 * @return
	 */
	private List<ResolveInfo> listInstalledApps(PackageManager manager) {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		return apps;
	}

	/**
	 * Loads the list of installed applications and then filters them down by
	 * the approved list from an array resource.
	 */
	private void loadApplications() {
		PackageManager manager = getPackageManager();
		List<ResolveInfo> apps = listInstalledApps(manager);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		if (apps != null) {
			if (filteredApps == null) {
				filteredApps = new ArrayList<AppDescriptor>(apps.size());
			}
			filteredApps.clear();

			for (int i = 0; i < apps.size(); i++) {
				AppDescriptor application = new AppDescriptor();
				ResolveInfo info = apps.get(i);
				if (isApproved(info.loadLabel(manager))) {
					application.setName(info.loadLabel(manager));
					application
							.setActivity(
									new ComponentName(
											info.activityInfo.applicationInfo.packageName,
											info.activityInfo.name),
									Intent.FLAG_ACTIVITY_NEW_TASK
											| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					application.setIcon(info.activityInfo.loadIcon(manager));
					filteredApps.add(application);
				}
			}
		}
		if (appGrid == null) {
			appGrid = (GridView) findViewById(R.id.appgridview);
		}
		appGrid.setAdapter(new ApplicationArrayAdapter(this, filteredApps));
		appGrid.setSelection(0);
		appGrid.setOnItemClickListener(new ApplicationLauncher());
	}
 
	
	/**
	 * checks to see if the application name is in the array of approved apps
	 */
	private boolean isApproved(CharSequence name) {
		boolean isApproved = false;
		if (name != null && approvedAppArray != null) {
			for (int i = 0; i < approvedAppArray.length; i++) {
				if (name.toString().equalsIgnoreCase(approvedAppArray[i])) {
					isApproved = true;
				}
			}
		}
		return isApproved;
	}

	/**
	 * shows an authentication dialog that asks for the administrator passcode.
	 * If the passcode matches, then the settings intent is launched.
	 * 
	 * TODO: move the ViewUtil from the survey app into a shared library and use
	 * the showAdminDialog method there instead.
	 * 
	 * @param parentContext
	 * 
	 */
	private void authorizeSettingsSelection() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LinearLayout main = new LinearLayout(this);
		main.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		main.setOrientation(LinearLayout.VERTICAL);
		final String passcode = getString(R.string.passcode);
		TextView tipText = new TextView(this);
		builder.setTitle(R.string.authtitle);
		tipText.setText(R.string.authtext);
		main.addView(tipText);
		final EditText input = new EditText(this);
		main.addView(input);
		builder.setView(main);
		builder.setPositiveButton(R.string.okbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String val = input.getText().toString();
						if (passcode.equals(val)) {
							Intent settingsIntent = new Intent(
									Settings.ACTION_SETTINGS);
							startActivity(settingsIntent);
							dialog.dismiss();
						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									FlowLauncher.this);
							TextView tipText = new TextView(FlowLauncher.this);
							builder.setTitle(R.string.authfailed);
							tipText.setText(R.string.invalidpassword);
							builder.setView(tipText);
							builder.setPositiveButton(R.string.okbutton,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

										}
									});
							builder.show();
							dialog.dismiss();
						}
					}
				});

		builder.setNegativeButton(R.string.cancelbutton,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();
	}

	/**
	 * Receives notifications when applications are added/removed.
	 */
	private class ApplicationsIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadApplications();
		}
	}

	/**
	 * Starts the selected activity/application in the grid view.
	 */
	@SuppressWarnings("unchecked")
	private class ApplicationLauncher implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			AppDescriptor app = (AppDescriptor) parent
					.getItemAtPosition(position);
			startActivity(app.getLaunchIntent());
		}
	}
}