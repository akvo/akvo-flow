package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.beoui.geocell.model.LocationCapable;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.weightsmeasures.domain.Currency;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPoint extends BaseDomain implements LocationCapable {

	private static final long serialVersionUID = -7708214468114860788L;
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
	private String communityName = null;
	private String institutionName = null;
	private String balloonTitle = null;

	public String getBalloonTitle() {
		return balloonTitle;
	}

	public void setBalloonTitle(String balloonTitle) {
		this.balloonTitle = balloonTitle;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getPhotoName() {
		return photoName;
	}

	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

	public Boolean getMeetGovtQualityStandardFlag() {
		return meetGovtQualityStandardFlag;
	}

	public void setMeetGovtQualityStandardFlag(
			Boolean meetGovtQualityStandardFlag) {
		this.meetGovtQualityStandardFlag = meetGovtQualityStandardFlag;
	}

	public Boolean getMeetGovtQuantityStandardFlag() {
		return meetGovtQuantityStandardFlag;
	}

	public void setMeetGovtQuantityStandardFlag(
			Boolean meetGovtQuantityStandardFlag) {
		this.meetGovtQuantityStandardFlag = meetGovtQuantityStandardFlag;
	}

	public String getWhoRepairsPoint() {
		return whoRepairsPoint;
	}

	public void setWhoRepairsPoint(String whoRepairsPoint) {
		this.whoRepairsPoint = whoRepairsPoint;
	}

	public String getSecondaryTechnologyString() {
		return secondaryTechnologyString;
	}

	public void setSecondaryTechnologyString(String secondaryTechnologyString) {
		this.secondaryTechnologyString = secondaryTechnologyString;
	}

	public Boolean getProvideAdequateQuantity() {
		return provideAdequateQuantity;
	}

	public void setProvideAdequateQuantity(Boolean provideAdequateQuantity) {
		this.provideAdequateQuantity = provideAdequateQuantity;
	}

	private String header = null;
	private String footer = null;
	private String photoName = null;
	private Boolean meetGovtQualityStandardFlag = null;
	private Boolean meetGovtQuantityStandardFlag = null;
	private String whoRepairsPoint = null;
	private String secondaryTechnologyString = null;
	private Boolean provideAdequateQuantity = null;

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	@Persistent
	private String photoURL = null;
	@Persistent
	private TechnologyType typeTechnology = null;
	@Persistent
	private String TechnologyTypeOther = null;
	private String typeTechnologyString = null;
	@Persistent
	// ToDo need to implement correct business rule
	private Date constructionDate = null;
	private String constructionDateYear = null;
	@Persistent
	private Long numberOfHouseholdsUsingPoint = null;
	@Persistent
	private Double costPer = null;
	@Persistent
	private UnitOfMeasure costPerUnitOfMeasure = null;
	@Persistent
	private Currency costPerCurrency = null;
	@Persistent
	private String farthestHouseholdfromPoint = null;
	@Persistent
	private String currentManagementStructurePoint = null;
	@Persistent
	private AccessPoint.Status pointStatus = null;
	@Persistent
	private String otherStatus = null;
	@Persistent
	private String pointPhotoCaption = null;
	@Persistent
	private String description = null;
	@Persistent
	private AccessPointType pointType;
	@Persistent
	private String countryCode;
	private List<String> geocells;

	public List<String> getGeocells() {
		return geocells;
	}

	public void setGeocells(List<String> geocells) {
		this.geocells = geocells;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public AccessPointType getPointType() {
		return pointType;
	}

	public void setPointType(AccessPointType pointType) {
		this.pointType = pointType;
	}

	public AccessPoint() {
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

	public Long getNumberOfHouseholdsUsingPoint() {
		return numberOfHouseholdsUsingPoint;
	}

	public void setNumberOfHouseholdsUsingPoint(
			Long numberOfHouseholdsUsingPoint) {
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

	public AccessPoint.Status getPointStatus() {
		return pointStatus;
	}

	public void setPointStatus(AccessPoint.Status pointStatus) {
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
		WATER_POINT, SANITATION_POINT, SCHOOL, HEALTH_POSTS, PUBLIC_INSTITUTION
	}

	public enum Status {
		FUNCTIONING_HIGH, FUNCTIONING_OK, FUNCTIONING_WITH_PROBLEMS, NO_IMPROVED_SYSTEM, OTHER
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public void setTechnologyTypeOther(String technologyTypeOther) {
		TechnologyTypeOther = technologyTypeOther;
	}

	public String getTechnologyTypeOther() {
		return TechnologyTypeOther;
	}

	public void setCostPerUnitOfMeasure(UnitOfMeasure costPerUnitOfMeasure) {
		this.costPerUnitOfMeasure = costPerUnitOfMeasure;
	}

	public UnitOfMeasure getCostPerUnitOfMeasure() {
		return costPerUnitOfMeasure;
	}

	public void setCostPerCurrency(Currency costPerCurrency) {
		this.costPerCurrency = costPerCurrency;
	}

	public Currency getCostPerCurrency() {
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

	public void setTypeTechnologyString(String typeTechnologyString) {
		this.typeTechnologyString = typeTechnologyString;
	}

	public String getTypeTechnologyString() {
		return typeTechnologyString;
	}

	public void setConstructionDateYear(String constructionDateYear) {
		this.constructionDateYear = constructionDateYear;
	}

	public String getConstructionDateYear() {
		return constructionDateYear;
	}

	@Override
	public String getKeyString() {
		return getKey().getId() + "";
	}

	@Override
	public Point getLocation() {
		return new Point(latitude, longitude);
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getInstitutionName() {
		return institutionName;
	}

}
