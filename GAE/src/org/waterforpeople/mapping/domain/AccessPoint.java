package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.weightsmeasures.domain.Currency;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPoint extends BaseDomain {

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
	private UnitOfMeasure costPerUnitOfMeasure = null;
	private Currency costPerCurrency = null;
	@Persistent
	private String farthestHouseholdfromPoint = null;
	@Persistent
	private String currentManagementStructurePoint = null;
	@Persistent
	private AccessPoint.Status pointStatus = null;
	private String otherStatus = null;
	@Persistent
	private String pointPhotoCaption = null;
	@Persistent
	private String description = null;
	@Persistent
	private AccessPointType pointType;

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
		WATER_POINT, SANITATION_POINT
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

}
