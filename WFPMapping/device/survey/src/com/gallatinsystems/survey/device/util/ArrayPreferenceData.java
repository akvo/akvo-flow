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
