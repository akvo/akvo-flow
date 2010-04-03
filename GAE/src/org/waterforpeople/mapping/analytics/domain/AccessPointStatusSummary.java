package org.waterforpeople.mapping.analytics.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

public class AccessPointStatusSummary extends BaseDomain {

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private static final long serialVersionUID = 6629466550148260904L;
	private Long count;
	private String year;
	private String status;
	private String country;
	private String community;
	private String type;
}