package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeDto;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessPointManagerServiceImpl extends RemoteServiceServlet
		implements AccessPointManagerService {
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(AccessPointManagerService.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 2710084399371519003L;

	@Override
	public List<AccessPointDto> listAccessPoints(
			AccessPointSearchCriteriaDto searchCriteria, Integer startRecord,
			Integer endRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccessPointDto> listAllAccessPoints(Integer startRecord,
			Integer endRecord) {
		List<AccessPointDto> apDtoList = new ArrayList<AccessPointDto>();

		AccessPointHelper ah = new AccessPointHelper();
		for (AccessPoint apItem : ah.listAccessPoint()) {
			AccessPointDto apDto = copyCanonicalToDto(apItem);
			apDtoList.add(apDto);
		}
		return apDtoList;
	}

	private AccessPointDto copyCanonicalToDto(AccessPoint apCanonical) {
		AccessPointDto apDto = new AccessPointDto();
		apDto.setKeyId(apCanonical.getKey().getId());
		apDto.setAltitude(apCanonical.getAltitude());
		apDto.setLatitude(apCanonical.getLatitude());
		apDto.setLongitude(apCanonical.getLongitude());
		apDto.setCommunityCode(apCanonical.getCommunityCode());
		apDto.setCollectionDate(apCanonical.getCollectionDate());
		apDto.setConstructionDate(apCanonical.getConstructionDate());
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
		apDto.setPointStatus(apCanonical.getPointStatus());
		if(apCanonical.getPointType()==AccessPointType.WATER_POINT){
			apDto.setPointType(AccessPointDto.AccessPointType.WATER_POINT);
		}else{
			apDto.setPointType(AccessPointDto.AccessPointType.SANITATION_POINT);
		}

		return apDto;
	}

	private AccessPoint copyDtoToCanonical(AccessPointDto apDto) {
		AccessPoint accessPoint = new AccessPoint();
		//Check to see if it is an update or insert
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
		accessPoint.setCurrentManagementStructurePoint(apDto
				.getCurrentManagementStructurePoint());
		accessPoint.setDescription(apDto.getDescription());
		accessPoint.setFarthestHouseholdfromPoint(apDto
				.getFarthestHouseholdfromPoint());
		accessPoint.setNumberOfHouseholdsUsingPoint(apDto
				.getNumberOfHouseholdsUsingPoint());
		accessPoint.setPhotoURL(apDto.getPhotoURL());
		accessPoint.setPointPhotoCaption(apDto.getPointPhotoCaption());
		accessPoint.setPointStatus(apDto.getPointStatus());
		if(apDto.getPointType() == AccessPointDto.AccessPointType.WATER_POINT){
			accessPoint.setPointType(AccessPoint.AccessPointType.WATER_POINT);
		}else{
			accessPoint.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
		}
		return accessPoint;
	}

	@Override
	public Integer deleteAccessPoint(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessPointDto getAccessPoint(Long id) {
		AccessPointHelper aph = new AccessPointHelper();
		AccessPoint canonicalItem = aph.getAccessPoint(id);
		AccessPointDto apDto = copyCanonicalToDto(canonicalItem);
		return apDto;
	}

	@Override
	public AccessPointDto saveAccessPoint(AccessPointDto accessPointDto) {
		AccessPointHelper aph = new AccessPointHelper();
		return copyCanonicalToDto(aph
				.saveAccessPoint(copyDtoToCanonical(accessPointDto)));
	}

	@Override
	public void delete(TechnologyTypeDto item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TechnologyTypeDto getTechnologyType(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TechnologyTypeDto> list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyTypeDto save(TechnologyTypeDto item) {
		// TODO Auto-generated method stub
		return null;
	}
}
