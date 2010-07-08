package com.gallatinsystems.survey.device.util;

/**
 * simple data structure to return pairs of data back from the languageUtil
 * apis. This class is to be used instead of just using android.util.Pair since
 * Pair was introduced in API level 5 and we need to run on 4+
 * 
 * @author Christopher Fagiani
 * 
 */
public class LanguageData {
	private String[] languages;
	private boolean[] selectedLanguages;

	public LanguageData(String[] langs, boolean[] selections) {
		languages = langs;
		selectedLanguages = selections;
	}

	public String[] getLanguages() {
		return languages;
	}

	public void setLanguages(String[] languages) {
		this.languages = languages;
	}

	public boolean[] getSelectedLanguages() {
		return selectedLanguages;
	}

	public void setSelectedLanguages(boolean[] selectedLanguages) {
		this.selectedLanguages = selectedLanguages;
	}
}
