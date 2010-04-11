package org.waterforpeople.mapping.app.gwt.client.community;

import java.io.Serializable;

public class CountryDto implements Serializable {

	private static final long serialVersionUID = -4677743992145776719L;

	private String displayName = null;
	private String name = null;
	private String isoAlpha2Code = null;
	private String isoAlpha3Code = null;
	private Integer isoNumeric3Code = null;

	public String getDisplayName() {
		return displayName != null ? displayName : name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIsoAlpha2Code() {
		return isoAlpha2Code;
	}

	public void setIsoAlpha2Code(String isoAlpha2Code) {
		this.isoAlpha2Code = isoAlpha2Code;
	}

	public String getIsoAlpha3Code() {
		return isoAlpha3Code;
	}

	public void setIsoAlpha3Code(String isoAlpha3Code) {
		this.isoAlpha3Code = isoAlpha3Code;
	}

	public Integer getIsoNumeric3Code() {
		return isoNumeric3Code;
	}

	public void setIsoNumeric3Code(Integer isoNumeric3Code) {
		this.isoNumeric3Code = isoNumeric3Code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
