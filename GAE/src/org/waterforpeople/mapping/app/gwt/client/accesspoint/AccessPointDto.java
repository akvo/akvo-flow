package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Persistent;




public class AccessPointDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9059171394832476797L;
	private Long keyId = null;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	@Persistent
	private Date collectionDate = null;
	@Persistent
	private Double latitude = 0.0;
	@Persistent
	private Double longitude = 0.0;
	@Persistent
	private Double altitude = 0.0;
	@Persistent
	private String communityCode = null;
	@Persistent
	private String photoURL = null;
	@Persistent
	private TechnologyType typeTechnology = null;
	@Persistent
	private String TechnologyTypeOther = null;
	@Persistent
	private Date constructionDate = null;
	@Persistent
	private String numberOfHouseholdsUsingPoint = null;
	@Persistent
	private Double costPer = null;
	private UnitOfMeasureDto costPerUnitOfMeasure = null;
	private CurrencyDto costPerCurrency = null;
	@Persistent
	private String farthestHouseholdfromPoint = null;
	@Persistent
	private String currentManagementStructurePoint = null;
	@Persistent
	private Status pointStatus = null;
	private String otherStatus = null;
	@Persistent
	private String pointPhotoCaption = null;
	@Persistent
	private String description = null;
	@Persistent
	private AccessPointType pointType;

	public enum Status {
		FUNCTIONING_HIGH, FUNCTIONING_OK, FUNCTIONING_WITH_PROBLEMS, NO_IMPROVED_SYSTEM, OTHER
	}

	public AccessPointType getPointType() {
		return pointType;
	}

	public void setPointType(AccessPointType pointType) {
		this.pointType = pointType;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public TechnologyType getTypeTechnology() {
		return typeTechnology;
	}

	public void setTypeTechnology(TechnologyType typeTechnology) {
		this.typeTechnology = typeTechnology;
	}

	public Date getConstructionDate() {
		return constructionDate;
	}

	public void setConstructionDate(Date constructionDate) {
		this.constructionDate = constructionDate;
	}

	public String getNumberOfHouseholdsUsingPoint() {
		return numberOfHouseholdsUsingPoint;
	}

	public void setNumberOfHouseholdsUsingPoint(
			String numberOfHouseholdsUsingPoint) {
		this.numberOfHouseholdsUsingPoint = numberOfHouseholdsUsingPoint;
	}

	public Double getCostPer() {
		return costPer;
	}

	public void setCostPer(Double costPer) {
		this.costPer = costPer;
	}

	public String getFarthestHouseholdfromPoint() {
		return farthestHouseholdfromPoint;
	}

	public void setFarthestHouseholdfromPoint(String farthestHouseholdfromPoint) {
		this.farthestHouseholdfromPoint = farthestHouseholdfromPoint;
	}

	public String getCurrentManagementStructurePoint() {
		return currentManagementStructurePoint;
	}

	public void setCurrentManagementStructurePoint(
			String currentManagementStructurePoint) {
		this.currentManagementStructurePoint = currentManagementStructurePoint;
	}

	public Status getPointStatus() {
		return pointStatus;
	}

	public void setPointStatus(Status pointStatus) {
		this.pointStatus = pointStatus;
	}

	public String getPointPhotoCaption() {
		return pointPhotoCaption;
	}

	public void setPointPhotoCaption(String pointPhotoCaption) {
		this.pointPhotoCaption = pointPhotoCaption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public enum AccessPointType {
		WATER_POINT, SANITATION_POINT
	}

	public void setTechnologyTypeOther(String technologyTypeOther) {
		TechnologyTypeOther = technologyTypeOther;
	}

	public String getTechnologyTypeOther() {
		return TechnologyTypeOther;
	}

	public void setCostPerUnitOfMeasure(UnitOfMeasureDto costPerUnitOfMeasure) {
		this.costPerUnitOfMeasure = costPerUnitOfMeasure;
	}

	public UnitOfMeasureDto getCostPerUnitOfMeasure() {
		return costPerUnitOfMeasure;
	}

	public void setCostPerCurrency(CurrencyDto costPerCurrency) {
		this.costPerCurrency = costPerCurrency;
	}

	public CurrencyDto getCostPerCurrency() {
		return costPerCurrency;
	}

	public void setOtherStatus(String otherStatus) {
		this.otherStatus = otherStatus;
	}

	public String getOtherStatus() {
		return otherStatus;
	}

	public Status encodeStatus(String unencodedStatus) {
		if (unencodedStatus.trim().equals("high")) {
			return Status.FUNCTIONING_HIGH;
		} else if (unencodedStatus.trim().toLowerCase().equals(
				"functioning but with problems")) {
			return Status.FUNCTIONING_WITH_PROBLEMS;
		} else if (unencodedStatus.trim().toLowerCase().equals("functional")
				|| unencodedStatus.trim().toLowerCase().equals("functioning")
				|| unencodedStatus.trim().equals("ok")) {
			return Status.FUNCTIONING_OK;
		} else if (unencodedStatus.trim().toLowerCase().contains("no improved")) {
			return Status.NO_IMPROVED_SYSTEM;
		} else {
			return Status.OTHER;
		}
	}
}
