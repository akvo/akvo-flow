package com.gallatinsystems.survey.device.domain;

import java.util.HashMap;

/**
 * simple data structure for representing question options.
 * 
 * @author Christopher Fagiani
 * 
 */
public class Option {

	private String text;
	private String value;
	private HashMap<String, AltText> altTextMap = new HashMap<String, AltText>();

	public void addAltText(AltText altText) {
		altTextMap.put(altText.getLanguage(), altText);
	}
	
	public HashMap<String, AltText> getAltTextMap() {
		return altTextMap;
	}

	public AltText getAltText(String lang) {
		return altTextMap.get(lang);
	}

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

}
