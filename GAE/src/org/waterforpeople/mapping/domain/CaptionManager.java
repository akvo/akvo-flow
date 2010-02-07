package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CaptionManager {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String collectionDateCaption = null;
	@Persistent
	private Double latitude = 0.0;
	@Persistent
	private Double longitude = 0.0;
	@Persistent
	private Double altitude = 0.0;
	@Persistent
	private String communityCodeCaption = null;
	@Persistent
	private String waterPointPhotoURLCaption = null;
	@Persistent
	private String typeOfWaterPointTechnologyCaption = null;
	@Persistent
	private String constructionDateOfWaterPointCaption = null;
	@Persistent
	private String numberOfHouseholdsUsingWaterPointCaption = null;
	@Persistent
	private String costPerCaption = null;
	@Persistent
	private String farthestHouseholdfromWaterPointCaption = null;
	@Persistent
	private String CurrentManagementStructureWaterPointCaption = null;
	@Persistent
	private String waterSystemStatusCaption = null;
	@Persistent
	private String sanitationPointPhotoURLCaption = null;
	@Persistent
	private String primaryImprovedSanitationTechCaption = null;
	@Persistent
	private String percentageOfHouseholdsWithImprovedSanitationCaption = null;
	@Persistent
	private String waterPointPhotoCaption = null;
	@Persistent
	private String descriptionCaption = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCollectionDateCaption() {
		return collectionDateCaption;
	}

	public void setCollectionDateCaption(String collectionDateCaption) {
		this.collectionDateCaption = collectionDateCaption;
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

	public String getCommunityCodeCaption() {
		return communityCodeCaption;
	}

	public void setCommunityCodeCaption(String communityCodeCaption) {
		this.communityCodeCaption = communityCodeCaption;
	}

	public String getWaterPointPhotoURLCaption() {
		return waterPointPhotoURLCaption;
	}

	public void setWaterPointPhotoURLCaption(String waterPointPhotoURLCaption) {
		this.waterPointPhotoURLCaption = waterPointPhotoURLCaption;
	}

	public String getTypeOfWaterPointTechnologyCaption() {
		return typeOfWaterPointTechnologyCaption;
	}

	public void setTypeOfWaterPointTechnologyCaption(
			String typeOfWaterPointTechnologyCaption) {
		this.typeOfWaterPointTechnologyCaption = typeOfWaterPointTechnologyCaption;
	}

	public String getConstructionDateOfWaterPointCaption() {
		return constructionDateOfWaterPointCaption;
	}

	public void setConstructionDateOfWaterPointCaption(
			String constructionDateOfWaterPointCaption) {
		this.constructionDateOfWaterPointCaption = constructionDateOfWaterPointCaption;
	}

	public String getNumberOfHouseholdsUsingWaterPointCaption() {
		return numberOfHouseholdsUsingWaterPointCaption;
	}

	public void setNumberOfHouseholdsUsingWaterPointCaption(
			String numberOfHouseholdsUsingWaterPointCaption) {
		this.numberOfHouseholdsUsingWaterPointCaption = numberOfHouseholdsUsingWaterPointCaption;
	}

	public String getCostPerCaption() {
		return costPerCaption;
	}

	public void setCostPerCaption(String costPerCaption) {
		this.costPerCaption = costPerCaption;
	}

	public String getFarthestHouseholdfromWaterPointCaption() {
		return farthestHouseholdfromWaterPointCaption;
	}

	public void setFarthestHouseholdfromWaterPointCaption(
			String farthestHouseholdfromWaterPointCaption) {
		this.farthestHouseholdfromWaterPointCaption = farthestHouseholdfromWaterPointCaption;
	}

	public String getCurrentManagementStructureWaterPointCaption() {
		return CurrentManagementStructureWaterPointCaption;
	}

	public void setCurrentManagementStructureWaterPointCaption(
			String currentManagementStructureWaterPointCaption) {
		CurrentManagementStructureWaterPointCaption = currentManagementStructureWaterPointCaption;
	}

	public String getWaterSystemStatusCaption() {
		return waterSystemStatusCaption;
	}

	public void setWaterSystemStatusCaption(String waterSystemStatusCaption) {
		this.waterSystemStatusCaption = waterSystemStatusCaption;
	}

	public String getSanitationPointPhotoURLCaption() {
		return sanitationPointPhotoURLCaption;
	}

	public void setSanitationPointPhotoURLCaption(
			String sanitationPointPhotoURLCaption) {
		this.sanitationPointPhotoURLCaption = sanitationPointPhotoURLCaption;
	}

	public String getPrimaryImprovedSanitationTechCaption() {
		return primaryImprovedSanitationTechCaption;
	}

	public void setPrimaryImprovedSanitationTechCaption(
			String primaryImprovedSanitationTechCaption) {
		this.primaryImprovedSanitationTechCaption = primaryImprovedSanitationTechCaption;
	}

	public String getPercentageOfHouseholdsWithImprovedSanitationCaption() {
		return percentageOfHouseholdsWithImprovedSanitationCaption;
	}

	public void setPercentageOfHouseholdsWithImprovedSanitationCaption(
			String percentageOfHouseholdsWithImprovedSanitationCaption) {
		this.percentageOfHouseholdsWithImprovedSanitationCaption = percentageOfHouseholdsWithImprovedSanitationCaption;
	}

	public String getWaterPointPhotoCaption() {
		return waterPointPhotoCaption;
	}

	public void setWaterPointPhotoCaption(String waterPointPhotoCaption) {
		this.waterPointPhotoCaption = waterPointPhotoCaption;
	}

	public String getDescriptionCaption() {
		return descriptionCaption;
	}

	public void setDescriptionCaption(String descriptionCaption) {
		this.descriptionCaption = descriptionCaption;
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
