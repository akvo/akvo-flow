package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessPointManagerServiceAsync {

	void listAccessPoints(AccessPointSearchCriteriaDto searchCriteria,
			Integer startRecord, Integer endRecord,
			AsyncCallback<List<AccessPointDto>> callback);

	void listAllAccessPoints(Integer startRecord, Integer endRecord,
			AsyncCallback<List<AccessPointDto>> callback);

	void getAccessPoint(Long id, AsyncCallback<AccessPointDto> callback);

	void saveAccessPoint(AccessPointDto accessPointDto,
			AsyncCallback<AccessPointDto> callback);

	void deleteAccessPoint(Long id, AsyncCallback<Integer> callback);

	void list(AsyncCallback<List<TechnologyTypeDto>> callback);

	void delete(TechnologyTypeDto item, AsyncCallback<Void> callback);

	void getTechnologyType(Long id, AsyncCallback<TechnologyTypeDto> callback);

	void save(TechnologyTypeDto item, AsyncCallback<TechnologyTypeDto> callback);

}
