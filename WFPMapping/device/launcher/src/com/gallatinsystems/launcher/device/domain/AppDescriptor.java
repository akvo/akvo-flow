package com.gallatinsystems.launcher.device.domain;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * simple domain class to represent information about installed applications
 * 
 * @author Christopher Fagiani
 * 
 */
public class AppDescriptor {

	boolean scaled;
	private String name;
	private Drawable icon;
	private Intent launchIntent;

	public void setIsScaled(boolean isScaled) {
		scaled = isScaled;
	}

	public void setName(String n) {
		if (n != null) {
			name = n;
		} else {
			name = "";
		}
	}

	public void setName(CharSequence cs) {
		if (cs != null) {
			setName(cs.toString());
		} else {
			setName(null);
		}
	}

	public void setIcon(Drawable i) {
		icon = i;
	}

	public Drawable getIcon() {
		return icon;
	}

	public Intent getLaunchIntent() {
		return launchIntent;
	}

	public boolean isScaled() {
		return scaled;
	}

	public String getName() {
		return name;
	}

	/**
	 * constructs an intent that can be used to launch this application
	 * 
	 * @param className
	 * @param launchFlags
	 */
	public void setActivity(ComponentName className, int launchFlags) {
		launchIntent = new Intent(Intent.ACTION_MAIN);
		launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		launchIntent.setComponent(className);
		launchIntent.setFlags(launchFlags);
	}

}