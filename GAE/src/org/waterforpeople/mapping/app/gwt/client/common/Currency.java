package org.waterforpeople.mapping.app.gwt.client.common;

import java.io.Serializable;



public class Currency implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6576282682037639811L;
	private String name = null;
	private Country countryOfIssue = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Country getCountryOfIssue() {
		return countryOfIssue;
	}

	public void setCountryOfIssue(Country countryOfIssue) {
		this.countryOfIssue = countryOfIssue;
	}

}
