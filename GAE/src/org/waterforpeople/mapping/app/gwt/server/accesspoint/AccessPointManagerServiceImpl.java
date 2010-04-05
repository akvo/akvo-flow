package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AccessPointManagerServiceImpl extends RemoteServiceServlet
		implements AccessPointManagerService {

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
		apDto.setCurrentManagementStructurePoint(apCanonical.getCurrentManagementStructurePoint());
		apDto.setDescription(apCanonical.getDescription());
		apDto.setFarthestHouseholdfromPoint(apCanonical.getFarthestHouseholdfromPoint());
		apDto.setNumberOfHouseholdsUsingPoint(apCanonical.getNumberOfHouseholdsUsingPoint());
		apDto.setPhotoURL(apCanonical.getPhotoURL());
		apDto.setPointPhotoCaption(apCanonical.getPointPhotoCaption());
		apDto.setPointStatus(apCanonical.getPointStatus());
		
		return apDto;
	}

	@Override
	public Integer deleteAccessPoint(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessPointDto getAccessPoint(Long id) {
		AccessPointHelper aph =new AccessPointHelper();
		AccessPoint canonicalItem = aph.getAccessPoint(id);
		AccessPointDto apDto = copyCanonicalToDto(canonicalItem);
		return apDto;
	}

	@Override
	public AccessPointDto saveAccessPoint(AccessPointDto accessPointDto) {
		// TODO Auto-generated method stub
		return null;
	}
}
