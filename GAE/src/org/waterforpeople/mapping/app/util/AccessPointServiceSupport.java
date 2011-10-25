package org.waterforpeople.mapping.app.util;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointScoreComputationItemDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointScoreDetailDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.UnitOfMeasureDto;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;
import org.waterforpeople.mapping.domain.AccessPointScoreComputationItem;
import org.waterforpeople.mapping.domain.AccessPointScoreDetail;

import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.weightsmeasures.domain.UnitOfMeasure;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Utility class for use in any Service impl that deals with AccessPoint domain
 * objects or DTOs
 * 
 * 
 * @author Christopher Fagiani
 */
public class AccessPointServiceSupport {

	public static AccessPointDto copyCanonicalToDto(AccessPoint apCanonical) {

		AccessPointDto apDto = new AccessPointDto();
		// DtoMarshaller.copyToDto(apCanonical, apDto);
		apDto.setKeyId(apCanonical.getKey().getId());
		apDto.setAltitude(apCanonical.getAltitude());
		apDto.setLatitude(apCanonical.getLatitude());
		apDto.setLongitude(apCanonical.getLongitude());
		apDto.setCommunityCode(apCanonical.getCommunityCode());
		apDto.setCommunityName(apCanonical.getCommunityName());
		apDto.setCollectionDate(apCanonical.getCollectionDate());
		apDto.setConstructionDate(apCanonical.getConstructionDate());
		apDto.setCountryCode(apCanonical.getCountryCode());
		apDto.setCostPer(apCanonical.getCostPer());
		apDto.setCurrentManagementStructurePoint(apCanonical
				.getCurrentManagementStructurePoint());
		apDto.setDescription(apCanonical.getDescription());
		apDto.setFarthestHouseholdfromPoint(apCanonical
				.getFarthestHouseholdfromPoint());
		apDto.setNumberOfHouseholdsUsingPoint(apCanonical
				.getNumberOfHouseholdsUsingPoint());
		apDto.setPhotoURL(apCanonical.getPhotoURL());
		apDto.setPointPhotoCaption(apCanonical.getPointPhotoCaption());
		apDto.setSmsCode(apCanonical.getSmsCode());
		apDto.setImprovedWaterPointFlag(apCanonical.getImprovedWaterPointFlag());
		apDto.setNumberOfHouseholdsUsingPoint(apCanonical
				.getNumberOfHouseholdsUsingPoint());
		apDto.setEstimatedPeoplePerHouse(apCanonical
				.getEstimatedPeoplePerHouse());
		apDto.setEstimatedPopulation(apCanonical.getExtimatedPopulation());
		apDto.setMeetGovtQualityStandards(apCanonical
				.getMeetGovtQualityStandardFlag());
		apDto.setMeetGovtQunatityStandardsFlag(apCanonical
				.getMeetGovtQuantityStandardFlag());
		apDto.setFarthestHouseholdfromPoint(apCanonical
				.getFarthestHouseholdfromPoint());
		apDto.setHasSystemBeenDown1DayFlag(apCanonical
				.getHasSystemBeenDown1DayFlag());
		apDto.setProvideAdequateQuantity(apCanonical
				.getProvideAdequateQuantity());
		apDto.setTypeTechnologyString(apCanonical.getTypeTechnologyString());
		apDto.setSecondaryTechnologyString(apCanonical
				.getSecondaryTechnologyString());
		apDto.setWaterForPeopleProjectFlag(apCanonical
				.getWaterForPeopleProjectFlag());
		apDto.setWaterForPeopleRole(apCanonical.getWaterForPeopleRole());
		apDto.setWhoRepairsPoint(apCanonical.getWhoRepairsPoint());
		apDto.setInstitutionName(apCanonical.getInstitutionName());
		apDto.setSub1(apCanonical.getSub1());
		apDto.setSub2(apCanonical.getSub2());
		apDto.setSub3(apCanonical.getSub3());
		apDto.setSub4(apCanonical.getSub4());
		apDto.setSub5(apCanonical.getSub5());
		apDto.setSub6(apCanonical.getSub6());
		apDto.setNumberWithinAcceptableDistance(apCanonical
				.getNumberWithinAcceptableDistance());
		apDto.setNumberOutsideAcceptableDistance(apCanonical
				.getNumberOutsideAcceptableDistance());
		apDto.setCurrentProblemFlag(apCanonical.getCurrentProblemFlag());
		apDto.setCurrentTreatmentFlag(apCanonical.getCurrentTreatmentFlag());
		apDto.setCurrentTreatment(apCanonical.getCurrentTreatment());
		apDto.setNumberOfUsers(apCanonical.getNumberOfUsers());
		apDto.setNumberOfLitersPerPersonPerDay(apCanonical
				.getNumberOfLitersPerPersonPerDay());
		apDto.setCollectTariffFlag(apCanonical.getCollectTariffFlag());
		apDto.setFinancialRecordsAvailableDayOfVisitFlag(apCanonical
				.getFinancialRecordsAvailableDayOfVisitFlag());
		apDto.setEnumeratorReviewedRecord(apCanonical
				.getEnumeratorReviewedRecord());

		if (apCanonical.getCollectionDate() != null) {
			apDto.setYear(DateUtil.getYear(apCanonical.getCollectionDate()));
		}
		if (AccessPoint.Status.FUNCTIONING_HIGH == apCanonical.getPointStatus()) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_HIGH);
		} else if (AccessPoint.Status.FUNCTIONING_OK == apCanonical
				.getPointStatus()) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_OK);
		} else if (AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS == apCanonical
				.getPointStatus()) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (AccessPoint.Status.NO_IMPROVED_SYSTEM == apCanonical
				.getPointStatus()) {
			apDto.setPointStatus(AccessPointDto.Status.NO_IMPROVED_SYSTEM);
		} else {
			apDto.setPointStatus(AccessPointDto.Status.OTHER);
			apDto.setOtherStatus(apCanonical.getOtherStatus());
		}

		if (AccessPointType.WATER_POINT == apCanonical.getPointType()) {
			apDto.setPointType(AccessPointDto.AccessPointType.WATER_POINT);
		} else if (AccessPointType.SANITATION_POINT == apCanonical
				.getPointType()) {
			apDto.setPointType(AccessPointDto.AccessPointType.SANITATION_POINT);
		} else if (AccessPointType.SCHOOL == apCanonical.getPointType()) {
			apDto.setPointType(AccessPointDto.AccessPointType.SCHOOL);
		} else if (AccessPointType.PUBLIC_INSTITUTION == apCanonical
				.getPointType()) {
			apDto.setPointType(AccessPointDto.AccessPointType.PUBLIC_INSTITUTION);
		} else {
			apDto.setPointType(AccessPointDto.AccessPointType.WATER_POINT);
		}

		apDto.setLocationTypeString(apCanonical.getLocationTypeString());

		apDto.setScore(apCanonical.getScore());
		apDto.setScoreComputationDate(apCanonical.getScoreComputationDate());
		if (apCanonical.getApScoreDetailList() != null) {
			List<AccessPointScoreDetailDto> apScoreDetailList = new ArrayList<AccessPointScoreDetailDto>();
			for (AccessPointScoreDetail item : apCanonical
					.getApScoreDetailList()) {
				AccessPointScoreDetailDto dtoItem = new AccessPointScoreDetailDto();
				if (item.getKey() != null)
					dtoItem.setKeyId(item.getKey().getId());
				if (item.getAccessPointId() != null)
					dtoItem.setAccessPointId(item.getAccessPointId());
				if (item.getScore() != null)
					dtoItem.setScore(item.getScore());
				ArrayList<AccessPointScoreComputationItemDto> scoreItems = new ArrayList<AccessPointScoreComputationItemDto>();
				for (AccessPointScoreComputationItem scoreCompItem : item
						.getScoreComputationItems()) {
					Integer score = scoreCompItem.getScoreItem();
					String detailMessage = scoreCompItem
							.getScoreDetailMessage();
					if (score != null && detailMessage != null) {
						AccessPointScoreComputationItemDto apsiDto = new AccessPointScoreComputationItemDto(
								score, detailMessage);
						scoreItems.add(apsiDto);
					}
				}
				dtoItem.setScoreComputationItems(scoreItems);
				dtoItem.setStatus(item.getStatus());
				dtoItem.setComputationDate(item.getComputationDate());
				apScoreDetailList.add(dtoItem);
			}
			apDto.setApScoreDetailList(apScoreDetailList);
		}
		return apDto;
	}

	public static AccessPoint copyDtoToCanonical(AccessPointDto apDto) {
		AccessPoint accessPoint = new AccessPoint();

		// Check to see if it is an update or insert
		UnitOfMeasureDto measDto = apDto.getCostPerUnitOfMeasure();
		apDto.setCostPerUnitOfMeasure(null);
		DtoMarshaller.copyToCanonical(accessPoint, apDto);
		if (measDto != null) {
			UnitOfMeasure measDomain = new UnitOfMeasure();
			DtoMarshaller.copyToCanonical(measDomain, measDto);
			accessPoint.setCostPerUnitOfMeasure(measDomain);

		}
		if (apDto.getKeyId() != null) {
			Key key = KeyFactory.createKey(AccessPoint.class.getSimpleName(),
					apDto.getKeyId());
			accessPoint.setKey(key);
		}
		accessPoint.setAltitude(apDto.getAltitude());
		accessPoint.setLatitude(apDto.getLatitude());
		accessPoint.setLongitude(apDto.getLongitude());
		accessPoint.setCommunityCode(apDto.getCommunityCode());
		accessPoint.setCollectionDate(apDto.getCollectionDate());
		accessPoint.setConstructionDate(apDto.getConstructionDate());
		accessPoint.setCostPer(apDto.getCostPer());
		accessPoint.setCountryCode(apDto.getCountryCode());
		accessPoint.setCurrentManagementStructurePoint(apDto
				.getCurrentManagementStructurePoint());
		accessPoint.setDescription(apDto.getDescription());
		accessPoint.setFarthestHouseholdfromPoint(apDto
				.getFarthestHouseholdfromPoint());
		accessPoint.setNumberOfHouseholdsUsingPoint(apDto
				.getNumberOfHouseholdsUsingPoint());
		accessPoint.setPhotoURL(apDto.getPhotoURL());
		accessPoint.setPointPhotoCaption(apDto.getPointPhotoCaption());
		accessPoint.setSmsCode(apDto.getSmsCode());
		accessPoint.setEstimatedHouseholds(apDto
				.getNumberOfHouseholdsUsingPoint());
		accessPoint.setEstimatedPeoplePerHouse(apDto
				.getEstimatedPeoplePerHouse());
		accessPoint.setExtimatedPopulation(apDto.getEstimatedPopulation());
		accessPoint.setMeetGovtQualityStandardFlag(apDto
				.getMeetGovtQualityStandards());
		accessPoint.setMeetGovtQuantityStandardFlag(apDto
				.getMeetGovtQunatityStandardsFlag());
		accessPoint.setFarthestHouseholdfromPoint(apDto
				.getFarthestHouseholdfromPoint());
		accessPoint.setHasSystemBeenDown1DayFlag(apDto
				.getHasSystemBeenDown1DayFlag());
		accessPoint.setProvideAdequateQuantity(apDto
				.getProvideAdequateQuantity());
		accessPoint.setTypeTechnologyString(apDto.getTypeTechnologyString());
		accessPoint.setSecondaryTechnologyString(apDto
				.getSecondaryTechnologyString());
		accessPoint.setWaterForPeopleProjectFlag(apDto
				.getWaterForPeopleProjectFlag());
		accessPoint.setWaterForPeopleRole(apDto.getWaterForPeopleRole());
		accessPoint.setWhoRepairsPoint(apDto.getWhoRepairsPoint());
		accessPoint.setInstitutionName(apDto.getInstitutionName());
		accessPoint.setNumberWithinAcceptableDistance(apDto
				.getNumberWithinAcceptableDistance());
		accessPoint.setNumberOutsideAcceptableDistance(apDto
				.getNumberOutsideAcceptableDistance());
		accessPoint.setCurrentProblemFlag(apDto.getCurrentProblemFlag());
		accessPoint.setCurrentTreatmentFlag(apDto.getCurrentTreatmentFlag());
		accessPoint.setCurrentTreatment(apDto.getCurrentTreatment());
		accessPoint.setCollectTariffFlag(apDto.getCollectTariffFlag());
		accessPoint.setNumberOfUsers(apDto.getNumberOfUsers());
		accessPoint.setNumberOfLitersPerPersonPerDay(apDto
				.getNumberOfLitersPerPersonPerDay());
		accessPoint
				.setImprovedWaterPointFlag(apDto.getImprovedWaterPointFlag());
		accessPoint.setLocationTypeString(apDto.getLocationTypeString());
		accessPoint.setFinancialRecordsAvailableDayOfVisitFlag(apDto
				.getFinancialRecordsAvailableDayOfVisitFlag());
		accessPoint.setEnumeratorReviewedRecord(apDto
				.getEnumeratorReviewedRecord());
		if (AccessPointDto.Status.FUNCTIONING_HIGH == apDto.getPointStatus()) {
			accessPoint.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
		} else if (AccessPointDto.Status.FUNCTIONING_OK == apDto
				.getPointStatus()) {
			accessPoint.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
		} else if (AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS == apDto
				.getPointStatus()) {
			accessPoint
					.setPointStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (AccessPointDto.Status.NO_IMPROVED_SYSTEM == apDto
				.getPointStatus()) {
			accessPoint.setPointStatus(AccessPoint.Status.NO_IMPROVED_SYSTEM);
		} else {
			accessPoint.setPointStatus(AccessPoint.Status.OTHER);
			accessPoint.setOtherStatus(apDto.getOtherStatus());
		}

		if (Status.OTHER == accessPoint.getPointStatus()) {
			accessPoint.setOtherStatus(apDto.getOtherStatus());
		}
		if (AccessPointDto.AccessPointType.WATER_POINT == apDto.getPointType()) {
			accessPoint.setPointType(AccessPoint.AccessPointType.WATER_POINT);
		} else if (AccessPointDto.AccessPointType.SANITATION_POINT == apDto
				.getPointType()) {
			accessPoint
					.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
		} else if (AccessPointDto.AccessPointType.SCHOOL == apDto
				.getPointType()) {
			accessPoint.setPointType(AccessPoint.AccessPointType.SCHOOL);
		} else if (AccessPointDto.AccessPointType.PUBLIC_INSTITUTION == apDto
				.getPointType()) {
			accessPoint
					.setPointType(AccessPoint.AccessPointType.PUBLIC_INSTITUTION);
		} else {
			accessPoint.setPointType(AccessPoint.AccessPointType.WATER_POINT);
		}
		return accessPoint;
	}
}
