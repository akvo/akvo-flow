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

package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.beoui.geocell.model.LocationCapable;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.MappableField;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.weightsmeasures.domain.Currency;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPoint extends BaseDomain implements LocationCapable {

    private static final long serialVersionUID = -7708214468114860788L;

    @MappableField(displayName = "SMS Identifier Code")
    private String smsCode;

    @MappableField(displayName = "Collection Date")
    private Date collectionDate = null;
    @MappableField(displayName = "Access Point Code")
    private String accessPointCode = null;
    @MappableField(displayName = "Latitude")
    private Double latitude = 0.0;
    @MappableField(displayName = "Longitude")
    private Double longitude = 0.0;
    @MappableField(displayName = "Altitude")
    private Double altitude = 0.0;
    @MappableField(displayName = "Community Code")
    private String communityCode = null;
    @MappableField(displayName = "Community Name")
    private String communityName = null;
    @MappableField(displayName = "Institution Name")
    private String institutionName = null;
    @MappableField(displayName = "Balloon Title")
    private String balloonTitle = null;
    @MappableField(displayName = "Region")
    private String region;
    @MappableField(displayName = "URL of Photo")
    private String photoURL = null;
    @MappableField(displayName = "Do Not Use = Type Technology")
    @Persistent
    private TechnologyType typeTechnology = null;
    @MappableField(displayName = "Do Not Use = Type Technology2")
    private String technologyTypeOther = null;
    @MappableField(displayName = "Type of Technology")
    private String typeTechnologyString = null;
    @MappableField(displayName = "Do Not Use = Construction Date")
    private Date constructionDate = null;
    @MappableField(displayName = "Year of Construction")
    private String constructionDateYear = null;
    @MappableField(displayName = "Number of households using point")
    private Long numberOfHouseholdsUsingPoint = null;
    @MappableField(displayName = "Tarrif")
    private Double costPer = null;
    @MappableField(displayName = "Do Not Use = costPerUnitOfMeasure")
    private UnitOfMeasure costPerUnitOfMeasure = null;
    @MappableField(displayName = "Do Not Use = Cost Per Currency")
    private Currency costPerCurrency = null;
    @MappableField(displayName = "Farthest Household From Point")
    private String farthestHouseholdfromPoint = null;
    @MappableField(displayName = "Current Management Structure")
    private String currentManagementStructurePoint = null;
    @MappableField(displayName = "Point Status")
    @Persistent
    private AccessPoint.Status pointStatus = null;
    @MappableField(displayName = "Do Not Use = Other Status")
    private String otherStatus = null;
    @MappableField(displayName = "Photo Caption")
    private String pointPhotoCaption = null;
    @MappableField(displayName = "Point Description")
    private String description = null;
    @MappableField(displayName = "Local Spare Parts on Hand")
    private Boolean localSparePartsFlag = null;
    @MappableField(displayName = "Don't Use - Spare Parts on Hand")
    private Boolean nearbySparePartsFlag = null;
    @MappableField(displayName = "Spare Parts on hand")
    private Boolean sparePartsOnHand = null;
    @MappableField(displayName = "Supports new Users since Construction/Rehabilitation")
    private Boolean supportNewUsers = null;
    @MappableField(displayName = "Positive Financial Balance")
    private Boolean positiveBalance = null;
    @MappableField(displayName = "Point Type")
    @Persistent
    private AccessPointType pointType;
    @MappableField(displayName = "Financial Records/Books Available for Point")
    private Boolean financialRecordsAvailableDayOfVisitFlag = null;
    @MappableField(displayName = "Enumerator Reviewed Record")
    private Boolean enumeratorReviewedRecord = null;
    @MappableField(displayName = "Country Code")
    private String countryCode;
    private List<String> geocells;
    @MappableField(displayName = "Estimated Population")
    private Long extimatedPopulation;
    @MappableField(displayName = "Estimated Number of Households")
    private Long estimatedHouseholds;
    @MappableField(displayName = "Estimated People Per Household")
    private Long estimatedPeoplePerHouse;
    @MappableField(displayName = "District")
    private String district;
    @MappableField(displayName = "Organization")
    private String organization;
    @MappableField(displayName = "Water For People Supported Project")
    private Boolean waterForPeopleProjectFlag = null;
    @MappableField(displayName = "Balloon Header")
    private String header = null;
    @MappableField(displayName = "Balloon Footer")
    private String footer = null;
    @MappableField(displayName = "Photo Name")
    private String photoName = null;
    @MappableField(displayName = "Meet Quality Standard")
    private Boolean meetGovtQualityStandardFlag = null;
    @MappableField(displayName = "Meet Quantity Standard Flag")
    private Boolean meetGovtQuantityStandardFlag = null;
    @MappableField(displayName = "Who Repairs Point")
    private String whoRepairsPoint = null;
    @MappableField(displayName = "Secondary TechnologyType")
    private String secondaryTechnologyString = null;
    @MappableField(displayName = "Provide Adequate Quantity")
    private Boolean provideAdequateQuantity = null;
    @MappableField(displayName = "System Been Down 1 Day/30")
    private Boolean hasSystemBeenDown1DayFlag = null;
    @MappableField(displayName = "Location Type")
    private String locationTypeString = null;
    @MappableField(displayName = "Water For People Role")
    private String waterForPeopleRole = null;
    @MappableField(displayName = "Current Problem Flag")
    private Boolean currentProblemFlag = null;
    @MappableField(displayName = "Current Problem")
    private String currentProblem = null;
    @MappableField(displayName = "Current Treatment Flag")
    private Boolean currentTreatmentFlag = null;
    @MappableField(displayName = "Current Treatment")
    private String currentTreatment = null;
    @MappableField(displayName = "Water Available Day of Visit")
    private Boolean waterAvailableDayVisitFlag = null;
    @MappableField(displayName = "PPM Fecal Coliform")
    private Double ppmFecalColiform = null;
    @MappableField(displayName = "Reason For Inadequate Water Supply")
    private String reasonForInadequateWaterSupply = null;
    @MappableField(displayName = "Frequency of Tariff")
    private String frequencyOfTariff = null;
    @MappableField(displayName = "Percentage Of Users Paying Tariff")
    private Double percentageOfUsersPaying = null;
    @MappableField(displayName = "Access Point Usage")
    private String accessPointUsage = null;
    @MappableField(displayName = "Access Point Quality Description")
    private String qualityDescription = null;
    @MappableField(displayName = "Access Point Quantity Description")
    private String quantityDescription = null;
    @MappableField(displayName = "Is improved Waterpoint Flag")
    private Boolean improvedWaterPointFlag = null;
    @MappableField(displayName = "Collect Tariff Flag")
    private Boolean collectTariffFlag = null;
    @MappableField(displayName = "Location Type - Urban, Peri Urban, Rural, Other")
    private LocationType locationType = null;
    private Integer score = null;
    private Integer sustainabilityScore = null;
    private Integer levelOfServiceScore = null;
    private Date scoreComputationDate = null;
    private String sub1 = null;
    private String sub2 = null;
    private String sub3 = null;
    private String sub4 = null;
    private String sub5 = null;
    private String sub6 = null;
    private Long surveyInstanceId = null;
    private Long surveyId = null;
    @MappableField(displayName = "Number of Households Within Acceptable Distance")
    private Integer numberWithinAcceptableDistance = 0;
    @MappableField(displayName = "Number of Households Outside Acceptable Distance")
    private Integer numberOutsideAcceptableDistance = 0;
    @MappableField(displayName = "Number of Users of Point")
    private Integer numberOfUsers = null;
    @MappableField(displayName = "Number of liters per person per day")
    private Integer numberOfLitersPerPersonPerDay = null;
    @MappableField(displayName = "System Support Expansion")
    private String systemExpansion = null;
    @MappableField(displayName = "Where does the money come from to operate and maintain the water point/system?")
    private String moneySource = null;
    @NotPersistent
    private List<AccessPointScoreDetail> apScoreDetailList = null;

    public Boolean getLocalSparePartsFlag() {
        return localSparePartsFlag;
    }

    public void setLocalSparePartsFlag(Boolean localSparePartsFlag) {
        this.localSparePartsFlag = localSparePartsFlag;
    }

    public Boolean getNearbySparePartsFlag() {
        return nearbySparePartsFlag;
    }

    public void setNearbySparePartsFlag(Boolean nearbySparePartsFlag) {
        this.nearbySparePartsFlag = nearbySparePartsFlag;
    }

    public Boolean getSupportNewUsers() {
        return supportNewUsers;
    }

    public void setSupportNewUsers(Boolean supportNewUsers) {
        this.supportNewUsers = supportNewUsers;
    }

    public Boolean getImprovedWaterPointFlag() {
        return improvedWaterPointFlag;
    }

    public void setImprovedWaterPointFlag(Boolean improvedWaterPointFlag) {
        this.improvedWaterPointFlag = improvedWaterPointFlag;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Boolean getCollectTariffFlag() {
        return collectTariffFlag;
    }

    public String getQualityDescription() {
        return qualityDescription;
    }

    public void setQualityDescription(String qualityDescription) {
        this.qualityDescription = qualityDescription;
    }

    public String getQuantityDescription() {
        return quantityDescription;
    }

    public void setQuantityDescription(String quantityDescription) {
        this.quantityDescription = quantityDescription;
    }

    public String getAccessPointUsage() {
        return accessPointUsage;
    }

    public void setAccessPointUsage(String accessPointUsage) {
        this.accessPointUsage = accessPointUsage;
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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Long getExtimatedPopulation() {
        return extimatedPopulation;
    }

    public void setExtimatedPopulation(Long extimatedPopulation) {
        this.extimatedPopulation = extimatedPopulation;
    }

    public Long getEstimatedHouseholds() {
        return estimatedHouseholds;
    }

    public void setEstimatedHouseholds(Long estimatedHouseholds) {
        this.estimatedHouseholds = estimatedHouseholds;
    }

    public Long getEstimatedPeoplePerHouse() {
        return estimatedPeoplePerHouse;
    }

    public void setEstimatedPeoplePerHouse(Long estimatedPeoplePerHouse) {
        this.estimatedPeoplePerHouse = estimatedPeoplePerHouse;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

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

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

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
        WATER_POINT, SANITATION_POINT, SCHOOL, HEALTH_POSTS, PUBLIC_INSTITUTION, HOUSEHOLD
    }

    public enum LocationType {
        RURAL, URBAN, PERIURBAN, OTHER
    }

    public enum Status {
        FUNCTIONING_HIGH, FUNCTIONING_OK, FUNCTIONING_WITH_PROBLEMS, BROKEN_DOWN, NO_IMPROVED_SYSTEM, OTHER, LATRINE_FULL, LATRINE_USED_TECH_PROBLEMS, LATRINE_NOT_USED_TECH_STRUCT_PROBLEMS, LATRINE_DO_NOT_KNOW, LATRINE_FUNCTIONAL
    }

    public void setTechnologyTypeOther(String technologyTypeOther) {
        this.technologyTypeOther = technologyTypeOther;
    }

    public String getTechnologyTypeOther() {
        return technologyTypeOther;
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
        } else if (unencodedStatus.trim().toLowerCase()
                .equals("functioning but with problems")) {
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

    public void setHasSystemBeenDown1DayFlag(Boolean hasSystemBeenDown1DayFlag) {
        this.hasSystemBeenDown1DayFlag = hasSystemBeenDown1DayFlag;
    }

    public Boolean getHasSystemBeenDown1DayFlag() {
        return hasSystemBeenDown1DayFlag;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
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

    public void setLocationTypeString(String locationTypeString) {
        this.locationTypeString = locationTypeString;
    }

    public String getLocationTypeString() {
        return locationTypeString;
    }

    public void setWaterForPeopleRole(String waterForPeopleRole) {
        this.waterForPeopleRole = waterForPeopleRole;
    }

    public String getWaterForPeopleRole() {
        return waterForPeopleRole;
    }

    public void setImprovedWaterPointFlag(boolean improvedWaterPointFlag) {
        this.improvedWaterPointFlag = improvedWaterPointFlag;
    }

    public Boolean isImprovedWaterPointFlag() {
        return improvedWaterPointFlag;
    }

    public void setCollectTariffFlag(Boolean collectTariffFlag) {
        this.collectTariffFlag = collectTariffFlag;
    }

    public Boolean isCollectTariffFlag() {
        return collectTariffFlag;
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

    public void setApScoreDetail(AccessPointScoreDetail apsd) {
        if (apScoreDetailList == null) {
            apScoreDetailList = new ArrayList<AccessPointScoreDetail>();
        }
        apScoreDetailList.add(apsd);
    }

    public void setApScoreDetailList(
            List<AccessPointScoreDetail> apScoreDetailList) {
        this.apScoreDetailList = apScoreDetailList;
    }

    public List<AccessPointScoreDetail> getApScoreDetailList() {
        return apScoreDetailList;
    }

    public void setSustainabilityScore(Integer sustainabilityScore) {
        this.sustainabilityScore = sustainabilityScore;
    }

    public Integer getSustainabilityScore() {
        return sustainabilityScore;
    }

    public void setLevelOfServiceScore(Integer levelOfServiceScore) {
        this.levelOfServiceScore = levelOfServiceScore;
    }

    public Integer getLevelOfServiceScore() {
        return levelOfServiceScore;
    }

    public void setPositiveBalance(Boolean positiveBalance) {
        this.positiveBalance = positiveBalance;
    }

    public Boolean getPositiveBalance() {
        return positiveBalance;
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

    public Boolean getFinancialRecordsAvailableDayOfVisitFlag() {
        return financialRecordsAvailableDayOfVisitFlag;
    }

    public void setFinancialRecordsAvailableDayOfVisitFlag(
            Boolean financialRecordsAvailableDayOfVisitFlag) {
        this.financialRecordsAvailableDayOfVisitFlag = financialRecordsAvailableDayOfVisitFlag;
    }

    public Boolean getEnumeratorReviewedRecord() {
        return enumeratorReviewedRecord;
    }

    public void setEnumeratorReviewedRecord(Boolean enumeratorReviewedRecord) {
        this.enumeratorReviewedRecord = enumeratorReviewedRecord;
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
            field.setAccessible(true);
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

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setSparePartsOnHand(Boolean sparePartsOnHand) {
        this.sparePartsOnHand = sparePartsOnHand;
    }

    public Boolean getSparePartsOnHand() {
        return sparePartsOnHand;
    }

    public void setSystemExpansion(String systemExpansion) {
        this.systemExpansion = systemExpansion;
    }

    public String getSystemExpansion() {
        return systemExpansion;
    }

    public void setMoneySource(String moneySource) {
        this.moneySource = moneySource;
    }

    public String getMoneySource() {
        return moneySource;
    }
}
