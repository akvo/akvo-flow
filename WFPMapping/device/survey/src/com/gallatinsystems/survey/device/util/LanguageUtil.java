package com.gallatinsystems.survey.device.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.res.Resources;

import com.gallatinsystems.survey.device.R;

/**
 * utility class for manipulating the language settings for surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class LanguageUtil {

	/**
	 * loads the languages from the applications resources and initializes the
	 * selection array based on the value of the selectionString passed in
	 * 
	 * @param context
	 * @param langSelection
	 * @return
	 */
	public static LanguageData loadLanguages(Context context,
			String langSelection) {
		Resources res = context.getResources();
		String[] languageArray = res.getStringArray(R.array.languages);
		boolean[] selectedLanguages = new boolean[languageArray.length];
		for (int i = 0; i < selectedLanguages.length; i++) {
			selectedLanguages[i] = false;
		}
		if (langSelection != null) {
			StringTokenizer strTok = new StringTokenizer(langSelection, ",");
			while (strTok.hasMoreTokens()) {
				selectedLanguages[Integer.parseInt(strTok.nextToken())] = true;
			}
		}
		return new LanguageData(languageArray, selectedLanguages);
	}

	/**
	 * uses the values in the two arrays passed in to form a comma delimited
	 * list of the language names that are selected.
	 * 
	 * @param langs
	 * @param selected
	 * @return
	 */
	public static String formSelectedLanguageString(String[] langs,
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
				buffer.append(langs[i]);
			}
		}
		return buffer.toString();
	}

	/**
	 * returns an arrayList of language codes that correspond to the array
	 * positions with true values
	 * 
	 * @param selectedLanguages
	 * @return
	 */
	public static String[] getSelectedLangageCodes(Context context,
			boolean[] selectedLanguages) {
		ArrayList<String> codes = new ArrayList<String>();
		Resources res = context.getResources();
		String[] langCodes = res.getStringArray(R.array.languagecodes);
		for (int i = 0; i < selectedLanguages.length; i++) {
			if (selectedLanguages[i]) {
				codes.add(langCodes[i]);
			}
		}
		return codes.toArray(new String[codes.size()]);
	}

	/**
	 * forms a comma-delimited string of array index values used to persist the
	 * selected languages to the db.
	 * 
	 * @param selectedLanguages
	 * @return
	 */
	public static String formLanguagePreferenceString(
			boolean[] selectedLanguages) {
		StringBuffer newSelection = new StringBuffer();
		boolean isFirst = true;
		for (int i = 0; i < selectedLanguages.length; i++) {
			if (selectedLanguages[i]) {
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
