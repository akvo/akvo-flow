package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;



public class CurrencyDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6576282682037639811L;
	private String name = null;
	private CountryDto countryOfIssue = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CountryDto getCountryOfIssue() {
		return countryOfIssue;
	}

	public void setCountryOfIssue(CountryDto countryOfIssue) {
		this.countryOfIssue = countryOfIssue;
	}

}
