package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;

public class AccessPointSummaryDto implements Serializable {

	private static final long serialVersionUID = -2236767586876399553L;

	private String type;
	private String communityCode;
	private String countryCode;
	private String year;
	private Long count;
	private String status;
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
}