package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPoint {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String collectionDate = null;
	@Persistent
	private Double latitude = 0.0;
	@Persistent
	private Double longitude = 0.0;
	@Persistent
	private Double altitude = 0.0;
	@Persistent
	private String communityCode = null;
	@Persistent
	private String waterPointPhotoURL = null;
	@Persistent
	private String typeOfWaterPointTechnology = null;
	@Persistent
	private String constructionDateOfWaterPoint = null;
	@Persistent
	private String numberOfHouseholdsUsingWaterPoint = null;
	@Persistent
	private String costPer = null;
	@Persistent
	private String farthestHouseholdfromWaterPoint = null;
	@Persistent
	private String CurrentManagementStructureWaterPoint = null;
	@Persistent
	private String waterSystemStatus = null;
	@Persistent
	private String sanitationPointPhotoURL = null;
	@Persistent
	private String primaryImprovedSanitationTech = null;
	@Persistent
	private String percentageOfHouseholdsWithImprovedSanitation = null;
	@Persistent
	private String waterPointPhotoCaption = null;
	@Persistent
	private String description = null;
	
	public AccessPoint(){
	}

	public String getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getTypeOfWaterPointTechnology() {
		return typeOfWaterPointTechnology;
	}

	public void setTypeOfWaterPointTechnology(String typeOfWaterPointTechnology) {
		this.typeOfWaterPointTechnology = typeOfWaterPointTechnology;
	}

	public String getConstructionDateOfWaterPoint() {
		return constructionDateOfWaterPoint;
	}

	public void setConstructionDateOfWaterPoint(
			String constructionDateOfWaterPoint) {
		this.constructionDateOfWaterPoint = constructionDateOfWaterPoint;
	}

	public String getNumberOfHouseholdsUsingWaterPoint() {
		return numberOfHouseholdsUsingWaterPoint;
	}

	public void setNumberOfHouseholdsUsingWaterPoint(
			String numberOfHouseholdsUsingWaterPoint) {
		this.numberOfHouseholdsUsingWaterPoint = numberOfHouseholdsUsingWaterPoint;
	}

	public String getCostPer() {
		return costPer;
	}

	public void setCostPer(String costPer) {
		this.costPer = costPer;
	}

	public String getFarthestHouseholdfromWaterPoint() {
		return farthestHouseholdfromWaterPoint;
	}

	public void setFarthestHouseholdfromWaterPoint(
			String farthestHouseholdfromWaterPoint) {
		this.farthestHouseholdfromWaterPoint = farthestHouseholdfromWaterPoint;
	}

	public String getCurrentManagementStructureWaterPoint() {
		return CurrentManagementStructureWaterPoint;
	}

	public void setCurrentManagementStructureWaterPoint(
			String currentManagementStructureWaterPoint) {
		CurrentManagementStructureWaterPoint = currentManagementStructureWaterPoint;
	}

	public String getWaterSystemStatus() {
		return waterSystemStatus;
	}

	public void setWaterSystemStatus(String waterSystemStatus) {
		this.waterSystemStatus = waterSystemStatus;
	}

	public String getPrimaryImprovedSanitationTech() {
		return primaryImprovedSanitationTech;
	}

	public void setPrimaryImprovedSanitationTech(
			String primaryImprovedSanitationTech) {
		this.primaryImprovedSanitationTech = primaryImprovedSanitationTech;
	}

	public String getPercentageOfHouseholdsWithImprovedSanitation() {
		return percentageOfHouseholdsWithImprovedSanitation;
	}

	public void setPercentageOfHouseholdsWithImprovedSanitation(
			String percentageOfHouseholdsWithImprovedSanitation) {
		this.percentageOfHouseholdsWithImprovedSanitation = percentageOfHouseholdsWithImprovedSanitation;
	}

	public String getWaterPointPhotoCaption() {
		return waterPointPhotoCaption;
	}

	public void setWaterPointPhotoCaption(String waterPointPhotoCaption) {
		this.waterPointPhotoCaption = waterPointPhotoCaption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getWaterPointPhotoURL() {
		return waterPointPhotoURL;
	}

	public void setWaterPointPhotoURL(String waterPointPhotoURL) {
		this.waterPointPhotoURL = waterPointPhotoURL;
	}

	public String getSanitationPointPhotoURL() {
		return sanitationPointPhotoURL;
	}

	public void setSanitationPointPhotoURL(String sanitationPointPhotoURL) {
		this.sanitationPointPhotoURL = sanitationPointPhotoURL;
	}

	public AccessPoint(Double latitude, Double longitude, Double altitude,
			String communityCode, String waterPointPhotoURL,
			String sanitationPointPhotoURL) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.communityCode = communityCode;
		this.waterPointPhotoURL = waterPointPhotoURL;
		this.sanitationPointPhotoURL = sanitationPointPhotoURL;
	}

	public AccessPoint(String collectionDate, Double latitude,
			Double longitude, Double altitude, String communityCode,
			String waterPointPhotoURL, String typeOfWaterPointTechnology,
			String constructionDateOfWaterPoint,
			String numberOfHouseholdsUsingWaterPoint, String costPer,
			String farthestHouseholdfromWaterPoint,
			String CurrentManagementStructureWaterPoint,
			String waterSystemStatus, String sanitationPointPhotoURL,
			String primaryImprovedSanitationTech,
			String percentageOfHouseholdsWithImprovedSanitation,
			String waterPointPhotoCaption, String description) {
		this.collectionDate = collectionDate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.communityCode = communityCode;
		this.waterPointPhotoURL = waterPointPhotoURL;
		this.typeOfWaterPointTechnology = typeOfWaterPointTechnology;
		this.constructionDateOfWaterPoint = constructionDateOfWaterPoint;
		this.numberOfHouseholdsUsingWaterPoint = numberOfHouseholdsUsingWaterPoint;
		this.costPer = costPer;
		this.farthestHouseholdfromWaterPoint = farthestHouseholdfromWaterPoint;
		this.CurrentManagementStructureWaterPoint = CurrentManagementStructureWaterPoint;
		this.waterSystemStatus = waterSystemStatus;
		this.sanitationPointPhotoURL = sanitationPointPhotoURL;
		this.primaryImprovedSanitationTech = primaryImprovedSanitationTech;
		this.percentageOfHouseholdsWithImprovedSanitation = percentageOfHouseholdsWithImprovedSanitation;
		this.waterPointPhotoCaption = waterPointPhotoCaption;
		this.description = description;

	}

	public void saveAccessPoint(Double latitude, Double longitude,
			Double altitude, String communityCode, String waterPointPhotoURL,
			String sanitationPointPhotoURL) {

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
}
