package com.gallatinsystems.survey.device.domain;

import java.util.HashMap;

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
}
