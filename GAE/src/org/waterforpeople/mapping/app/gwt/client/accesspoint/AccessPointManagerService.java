package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface AccessPointManagerService extends RemoteService {
	List<AccessPointDto> listAllAccessPoints(Integer startRecord, Integer endRecord);
	List<AccessPointDto> listAccessPoints(AccessPointSearchCriteriaDto searchCriteria, Integer startRecord, Integer endRecord);
	AccessPointDto getAccessPoint(Long id);
	AccessPointDto saveAccessPoint(AccessPointDto accessPointDto);
	Integer deleteAccessPoint(Long id);
	List<TechnologyTypeDto> list();
	TechnologyTypeDto getTechnologyType(Long id);
	void delete(TechnologyTypeDto item);
	TechnologyTypeDto save(TechnologyTypeDto item);
}
