package org.waterforpeople.mapping.app.gwt.client.user;

import java.io.Serializable;

public class UserConfigDto implements Serializable {
	private static final long serialVersionUID = 4515497143926759239L;

	private String group;
	private String name;
	private String value;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
