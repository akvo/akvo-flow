package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;
import java.util.Date;

public class AccessPointSearchCriteriaDto implements Serializable {

	private static final long serialVersionUID = -4084451388783004593L;

	private String countryCode;
	private String communityCode;
	private Date collectionDateFrom;
	private Date collectionDateTo;
	private String pointType;
	private String techType;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public Date getCollectionDateFrom() {
		return collectionDateFrom;
	}

	public void setCollectionDateFrom(Date collectionDateFrom) {
		this.collectionDateFrom = collectionDateFrom;
	}

	public Date getCollectionDateTo() {
		return collectionDateTo;
	}

	public void setCollectionDateTo(Date collectionDateTo) {
		this.collectionDateTo = collectionDateTo;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}
}
