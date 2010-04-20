package org.waterforpeople.mapping.app.util;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.common.util.DateUtil;
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
		apDto.setKeyId(apCanonical.getKey().getId());
		apDto.setAltitude(apCanonical.getAltitude());
		apDto.setLatitude(apCanonical.getLatitude());
		apDto.setLongitude(apCanonical.getLongitude());
		apDto.setCommunityCode(apCanonical.getCommunityCode());
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
			apDto
					.setPointStatus(AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (AccessPoint.Status.NO_IMPROVED_SYSTEM == apCanonical
				.getPointStatus()) {
			apDto.setPointStatus(AccessPointDto.Status.NO_IMPROVED_SYSTEM);
		} else {
			apDto.setPointStatus(AccessPointDto.Status.OTHER);
			apDto.setOtherStatus(apCanonical.getOtherStatus());
		}

		if (AccessPointType.WATER_POINT == apCanonical.getPointType()) {
			apDto.setPointType(AccessPointDto.AccessPointType.WATER_POINT);
		} else {
			apDto.setPointType(AccessPointDto.AccessPointType.SANITATION_POINT);
		}

		return apDto;
	}

	public static AccessPoint copyDtoToCanonical(AccessPointDto apDto) {
		AccessPoint accessPoint = new AccessPoint();
		// Check to see if it is an update or insert
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
		} else {
			accessPoint
					.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
		}
		return accessPoint;
	}
}
