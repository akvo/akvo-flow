package com.gallatinsystems.survey.device.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.res.Resources;

/**
 * utility class for manipulating the preference settings that allow for
 * multi-selection of language-specific arrays
 * 
 * @author Christopher Fagiani
 * 
 */
public class ArrayPreferenceUtil {

	/**
	 * loads the array from the applications resources and initializes the
	 * selection array based on the value of the selectionString passed in
	 * 
	 * @param context
	 * @param selection
	 * @return
	 */
	public static ArrayPreferenceData loadArray(Context context,
			String selection, int resourceId) {
		Resources res = context.getResources();
		String[] stringArray = res.getStringArray(resourceId);
		boolean[] selectedItems = new boolean[stringArray.length];
		for (int i = 0; i < selectedItems.length; i++) {
			selectedItems[i] = false;
		}
		if (selection != null) {
			StringTokenizer strTok = new StringTokenizer(selection, ",");
			while (strTok.hasMoreTokens()) {
				selectedItems[Integer.parseInt(strTok.nextToken())] = true;
			}
		}
		return new ArrayPreferenceData(stringArray, selectedItems);
	}

	/**
	 * uses the values in the two arrays passed in to form a comma delimited
	 * list of the items that are selected.
	 * 
	 * @param values
	 * @param selected
	 * @return
	 */
	public static String formSelectedItemString(String[] values,
			boolean[] selected) {
		boolean isFirst = true;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < selected.length; i++) {
			if (selected[i]) {
				if (!isFirst) {
					buffer.append(",");

				} else {
					isFirst = false;
				}
				buffer.append(values[i]);
			}
		}
		return buffer.toString();
	}

	/**
	 * returns an arrayList of codes that correspond to the array
	 * positions with true values
	 * 
	 * 
	 * @return
	 */
	public static String[] getSelectedCodes(Context context,
			boolean[] selectedItems, int codeResourceId) {
		ArrayList<String> codes = new ArrayList<String>();
		Resources res = context.getResources();
		String[] allCodes = res.getStringArray(codeResourceId);
		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i]) {
				codes.add(allCodes[i]);
			}
		}
		return codes.toArray(new String[codes.size()]);
	}

	/**
	 * forms a comma-delimited string of array index values used to persist the
	 * selected items to the db.
	 * 
	 * @param 
	 * @return
	 */
	public static String formPreferenceString(
			boolean[] selectedItems) {
		StringBuffer newSelection = new StringBuffer();
		boolean isFirst = true;
		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i]) {
				if (!isFirst) {
					newSelection.append(",");
				} else {
					isFirst = false;
				}
				newSelection.append(i);
			}
		}
		return newSelection.toString();
	}
}
