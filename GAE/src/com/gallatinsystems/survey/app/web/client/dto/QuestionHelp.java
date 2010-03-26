package com.gallatinsystems.survey.app.web.client.dto;


public class QuestionHelp {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1563528591253495401L;
	private String text;
	private String resourceUrl;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public enum QuestionHelpType {
		TEXT, PICTURE_GALLERY, PICTURE, MOVIE
	}
}
