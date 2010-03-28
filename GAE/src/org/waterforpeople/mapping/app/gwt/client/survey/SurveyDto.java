package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;

public class SurveyDto implements Serializable{
	private static final long serialVersionUID = 6593732844403807030L;
	private String name;
	private String version;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
