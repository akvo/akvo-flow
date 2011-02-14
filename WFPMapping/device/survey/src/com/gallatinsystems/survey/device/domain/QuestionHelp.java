package com.gallatinsystems.survey.device.domain;

import java.util.HashMap;

/**
 * domain object for help media. If the type == tip, then value is undefined.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionHelp {

	private HashMap<String, AltText> altTextMap = new HashMap<String, AltText>();
	private String type;
	private String text;
	private String value;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, AltText> getAltTextMap() {
		return altTextMap;
	}

	public AltText getAltText(String lang) {
		return altTextMap.get(lang);
	}

	public void addAltText(AltText altText) {
		altTextMap.put(altText.getLanguage(), altText);
	}

	/**
	 * 
	 * checks whether this help object is well formed
	 */
	public boolean isValid() {
		if (text == null || text.trim().length() == 0) {
			// if text is null, then value must be populated for this to be
			// valid
			if (value == null || value.trim().length() == 0) {
				return false;
			}
		} else {
			// if text is not null, then it can't be the string "null"
			if ("null".equalsIgnoreCase(text.trim())) {
				return false;
			}
		}

		return true;

	}
}
