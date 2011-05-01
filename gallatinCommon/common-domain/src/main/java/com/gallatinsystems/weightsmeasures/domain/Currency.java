package com.gallatinsystems.weightsmeasures.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.gis.geography.domain.Country;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Currency extends BaseDomain {

	private static final long serialVersionUID = 2732185712823409196L;

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
