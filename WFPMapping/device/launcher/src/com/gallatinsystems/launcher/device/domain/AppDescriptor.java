/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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