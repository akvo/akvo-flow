/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.Date;
import java.util.List;

import com.gallatinsystems.common.util.MappableField;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class AccessPointDto extends BaseDto {

	private static final long serialVersionUID = -9059171394832476797L;

	private String countryCode;

	private Long year;

	private Date collectionDate = null;

	private Double latitude = 0.0;

	private Double longitude = 0.0;

	private Double altitude = 0.0;

	private String communityCode = null;
	private String communityName = null;
	private String institutionName = null;

	private String photoURL = null;

	private String TechnologyTypeOther = null;
	private String typeTechnologyString = null;
	private String constructionDateYear = null;
	private Long estimatedPopulation = null;
	private Long estimatedPeoplePerHouse = null;
	private Boolean meetGovtQualityStandards = null;
	private Boolean meetGovtQunatityStandardsFlag = null;
	private String whoRepairsPoint = null;
	private String secondaryTechnologyString = null;
	private Boolean provideAdequateQuantity = null;
	private Boolean hasSystemBeenDown1DayFlag = null;
	private String smsCode;
	private Boolean waterForPeopleProjectFlag = null;
	private String accessPointCode = null;
	private String locationTypeString = null;
	private String waterForPeopleRole = null;
	private String currentProblem = null;
	private Boolean currentProblemFlag = null;
	private String currentTreatment = null;
	private Boolean currentTreatmentFlag = null;
	private Boolean waterAvailableDayVisitFlag = null;
	private Double ppmFecalColiform = null;
	private String reasonForInadequateWaterSupply=null;
	private String frequencyOfTariff=null;
	private Double percentageOfUsersPaying = null;
	private Integer score = null;
	private Date scoreComputationDate = null;
	private List<AccessPointScoreDetailDto> apScoreDetailList= null;
	private Boolean improvedWaterPointFlag = null;
	private Boolean collectTariffFlag = null;
	private String sub1;
	private String sub2;
	private String sub3;
	private String sub4;
	private String sub5;
	private String sub6;
	private Integer numberWithinAcceptableDistance = null;
	private Integer numberOutsideAcceptableDistance = null;
	private Integer numberOfUsers= null;
	private Integer numberOfLitersPerPersonPerDay = null;
	private Boolean financialRecordsAvailableDayOfVisitFlag = null;
	private Boolean enumeratorReviewedRecord = null;
	private String moneySource = null;
	
	public enum LocationType{
		URBAN, RURAL, PERIURBAN
	}
	
	public Boolean getImprovedWaterPointFlag() {
		return improvedWaterPointFlag;
	}

	public void setImprovedWaterPointFlag(Boolean improvedWaterPointFlag) {
		this.improvedWaterPointFlag = improvedWaterPointFlag;
	}

	public Boolean getCollectTariffFlag() {
		return collectTariffFlag;
	}

	public void setCollectTariffFlag(Boolean collectTariffFlag) {
		this.collectTariffFlag = collectTariffFlag;
	}

	public String getSub1() {
		return sub1;
	}

	public void setSub1(String sub1) {
		this.sub1 = sub1;
	}

	public String getSub2() {
		return sub2;
	}

	public void setSub2(String sub2) {
		this.sub2 = sub2;
	}

	public String getSub3() {
		return sub3;
	}

	public void setSub3(String sub3) {
		this.sub3 = sub3;
	}

	public String getSub4() {
		return sub4;
	}

	public void setSub4(String sub4) {
		this.sub4 = sub4;
	}

	public String getSub5() {
		return sub5;
	}

	public void setSub5(String sub5) {
		this.sub5 = sub5;
	}

	public String getSub6() {
		return sub6;
	}

	public void setSub6(String sub6) {
		this.sub6 = sub6;
	}

	public String getLocationTypeString() {
		return locationTypeString;
	}

	public void setLocationTypeString(String locationTypeString) {
		this.locationTypeString = locationTypeString;
	}

	public String getWaterForPeopleRole() {
		return waterForPeopleRole;
	}

	public void setWaterForPeopleRole(String waterForPeopleRole) {
		this.waterForPeopleRole = waterForPeopleRole;
	}

	public String getCurrentProblem() {
		return currentProblem;
	}

	public void setCurrentProblem(String currentProblem) {
		this.currentProblem = currentProblem;
	}

	public String getCurrentTreatment() {
		return currentTreatment;
	}

	public void setCurrentTreatment(String currentTreatment) {
		this.currentTreatment = currentTreatment;
	}

	public Boolean getWaterAvailableDayVisitFlag() {
		return waterAvailableDayVisitFlag;
	}

	public void setWaterAvailableDayVisitFlag(Boolean waterAvailableDayVisitFlag) {
		this.waterAvailableDayVisitFlag = waterAvailableDayVisitFlag;
	}

	public Double getPpmFecalColiform() {
		return ppmFecalColiform;
	}

	public void setPpmFecalColiform(Double ppmFecalColiform) {
		this.ppmFecalColiform = ppmFecalColiform;
	}

	public String getReasonForInadequateWaterSupply() {
		return reasonForInadequateWaterSupply;
	}

	public void setReasonForInadequateWaterSupply(
			String reasonForInadequateWaterSupply) {
		this.reasonForInadequateWaterSupply = reasonForInadequateWaterSupply;
	}

	public String getFrequencyOfTariff() {
		return frequencyOfTariff;
	}

	public void setFrequencyOfTariff(String frequencyOfTariff) {
		this.frequencyOfTariff = frequencyOfTariff;
	}

	public Double getPercentageOfUsersPaying() {
		return percentageOfUsersPaying;
	}

	public void setPercentageOfUsersPaying(Double percentageOfUsersPaying) {
		this.percentageOfUsersPaying = percentageOfUsersPaying;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getTypeTechnologyString() {
		return typeTechnologyString;
	}

	public void setTypeTechnologyString(String typeTechnologyString) {
		this.typeTechnologyString = typeTechnologyString;
	}

	public String getConstructionDateYear() {
		return constructionDateYear;
	}

	public void setConstructionDateYear(String constructionDateYear) {
		this.constructionDateYear = constructionDateYear;
	}

	public Long getEstimatedPopulation() {
		return estimatedPopulation;
	}

	public void setEstimatedPopulation(Long estimatedPopulation) {
		this.estimatedPopulation = estimatedPopulation;
	}

	public Long getEstimatedPeoplePerHouse() {
		return estimatedPeoplePerHouse;
	}

	public void setEstimatedPeoplePerHouse(Long estimatedPeoplePerHouse) {
		this.estimatedPeoplePerHouse = estimatedPeoplePerHouse;
	}

	public Boolean getMeetGovtQualityStandards() {
		return meetGovtQualityStandards;
	}

	public void setMeetGovtQualityStandards(Boolean meetGovtQualityStandards) {
		this.meetGovtQualityStandards = meetGovtQualityStandards;
	}

	public Boolean getMeetGovtQunatityStandardsFlag() {
		return meetGovtQunatityStandardsFlag;
	}

	public void setMeetGovtQunatityStandardsFlag(
			Boolean meetGovtQunatityStandardsFlag) {
		this.meetGovtQunatityStandardsFlag = meetGovtQunatityStandardsFlag;
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

	public Boolean getHasSystemBeenDown1DayFlag() {
		return hasSystemBeenDown1DayFlag;
	}

	public void setHasSystemBeenDown1DayFlag(Boolean hasSystemBeenDown1DayFlag) {
		this.hasSystemBeenDown1DayFlag = hasSystemBeenDown1DayFlag;
	}

	private Date constructionDate = null;

	private Long numberOfHouseholdsUsingPoint = null;

	private Double costPer = null;
	private UnitOfMeasureDto costPerUnitOfMeasure = null;
	

	private String farthestHouseholdfromPoint = null;

	private String currentManagementStructurePoint = null;

	private Status pointStatus = null;
	private String otherStatus = null;

	private String pointPhotoCaption = null;

	private String description = null;

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
		WATER_POINT, SANITATION_POINT, PUBLIC_INSTITUTION, SCHOOL
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

	
	public void setOtherStatus(String otherStatus) {
		this.otherStatus = otherStatus;
	}

	public String getOtherStatus() {
		return otherStatus;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
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

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setWaterForPeopleProjectFlag(Boolean waterForPeopleProjectFlag) {
		this.waterForPeopleProjectFlag = waterForPeopleProjectFlag;
	}

	public Boolean getWaterForPeopleProjectFlag() {
		return waterForPeopleProjectFlag;
	}

	public void setAccessPointCode(String accessPointCode) {
		this.accessPointCode = accessPointCode;
	}

	public String getAccessPointCode() {
		return accessPointCode;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScore() {
		return score;
	}

	public void setScoreComputationDate(Date scoreComputationDate) {
		this.scoreComputationDate = scoreComputationDate;
	}

	public Date getScoreComputationDate() {
		return scoreComputationDate;
	}

	public void setApScoreDetailList(List<AccessPointScoreDetailDto> apScoreDetailList) {
		this.apScoreDetailList = apScoreDetailList;
	}

	public List<AccessPointScoreDetailDto> getApScoreDetailList() {
		return apScoreDetailList;
	}

	public void setNumberWithinAcceptableDistance(
			Integer numberWithinAcceptableDistance) {
		this.numberWithinAcceptableDistance = numberWithinAcceptableDistance;
	}

	public Integer getNumberWithinAcceptableDistance() {
		return numberWithinAcceptableDistance;
	}

	public void setNumberOutsideAcceptableDistance(
			Integer numberOutsideAcceptableDistance) {
		this.numberOutsideAcceptableDistance = numberOutsideAcceptableDistance;
	}

	public Integer getNumberOutsideAcceptableDistance() {
		return numberOutsideAcceptableDistance;
	}

	public void setCurrentProblemFlag(Boolean currentProblemFlag) {
		this.currentProblemFlag = currentProblemFlag;
	}

	public Boolean getCurrentProblemFlag() {
		return currentProblemFlag;
	}

	public void setCurrentTreatmentFlag(Boolean currentTreatmentFlag) {
		this.currentTreatmentFlag = currentTreatmentFlag;
	}

	public Boolean getCurrentTreatmentFlag() {
		return currentTreatmentFlag;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfLitersPerPersonPerDay(
			Integer numberOfLitersPerPersonPerDay) {
		this.numberOfLitersPerPersonPerDay = numberOfLitersPerPersonPerDay;
	}

	public Integer getNumberOfLitersPerPersonPerDay() {
		return numberOfLitersPerPersonPerDay;
	}

	public void setFinancialRecordsAvailableDayOfVisitFlag(
			Boolean financialRecordsAvailableDayOfVisitFlag) {
		this.financialRecordsAvailableDayOfVisitFlag = financialRecordsAvailableDayOfVisitFlag;
	}

	public Boolean getFinancialRecordsAvailableDayOfVisitFlag() {
		return financialRecordsAvailableDayOfVisitFlag;
	}

	public void setEnumeratorReviewedRecord(Boolean enumeratorReviewedRecord) {
		this.enumeratorReviewedRecord = enumeratorReviewedRecord;
	}

	public Boolean getEnumeratorReviewedRecord() {
		return enumeratorReviewedRecord;
	}

	public void setMoneySource(String moneySource) {
		this.moneySource = moneySource;
	}

	public String getMoneySource() {
		return moneySource;
	}
}
