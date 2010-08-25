package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;
import java.util.List;


import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("apmanagerrpcservice")
public interface AccessPointManagerService extends RemoteService {
	List<AccessPointDto> listAllAccessPoints(String cursorString);

	AccessPointDto getAccessPoint(Long id);

	AccessPointDto saveAccessPoint(AccessPointDto accessPointDto);

	Integer deleteAccessPoint(Long id);

	List<TechnologyTypeDto> list();

	TechnologyTypeDto getTechnologyType(Long id);

	void delete(TechnologyTypeDto item);

	TechnologyTypeDto save(TechnologyTypeDto item);

	Boolean rotateImage(String fileName);

	AccessPointDto[] listAccessPointByLocation(String country,
			String community, String type);

	ResponseDto<ArrayList<AccessPointDto>> listAccessPoints(
			AccessPointSearchCriteriaDto searchCriteria, String cursorString);
	
	String getCursorString();
	ResponseDto<ArrayList<AccessPointDto>> listErrorAccessPoints(String cursorString);
	
}
