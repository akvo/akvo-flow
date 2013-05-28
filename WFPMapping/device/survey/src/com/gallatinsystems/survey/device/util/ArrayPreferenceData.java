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

package com.gallatinsystems.survey.device.util;

/**
 * simple data structure to return pairs of data back from the
 * ArrayPreferenceUtil apis. This class is to be used instead of just using
 * android.util.Pair since Pair was introduced in API level 5 and we need to run
 * on 4+
 * 
 * @author Christopher Fagiani
 * 
 */
public class ArrayPreferenceData {
	private String[] items;
	private boolean[] selectedItems;

	public ArrayPreferenceData(String[] items, boolean[] selections) {
		this.items = items;
		selectedItems = selections;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}

	public boolean[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(boolean[] selectedItems) {
		this.selectedItems = selectedItems;
	}
}
