package com.gallatinsystems.survey.device.domain;

/**
 * domain class to store translations of questions/options
 * 
 * @author Christopher Fagiani
 * 
 */
public class AltText {
	private String language;
	private String type;
	private String text;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
