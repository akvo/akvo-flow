package org.waterforpeople.mapping.app.gwt.client.community;

import java.io.Serializable;

public class CountryDto implements Serializable {

	private static final long serialVersionUID = -4677743992145776719L;
	private String countryCode;
	private String name;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
