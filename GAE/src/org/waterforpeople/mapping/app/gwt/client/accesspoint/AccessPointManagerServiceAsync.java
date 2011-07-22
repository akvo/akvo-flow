package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessPointManagerServiceAsync {

	void listAccessPoints(AccessPointSearchCriteriaDto searchCriteria,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>> callback);

	void getAccessPoint(Long id, AsyncCallback<AccessPointDto> callback);

	void saveAccessPoint(AccessPointDto accessPointDto,
			AsyncCallback<AccessPointDto> callback);

	void deleteAccessPoint(Long id, AsyncCallback<Integer> callback);

	void list(AsyncCallback<List<TechnologyTypeDto>> callback);

	void delete(TechnologyTypeDto item, AsyncCallback<Void> callback);

	void getTechnologyType(Long id, AsyncCallback<TechnologyTypeDto> callback);

	void save(TechnologyTypeDto item, AsyncCallback<TechnologyTypeDto> callback);

	void rotateImage(String fileName, AsyncCallback<Void> callback);

	void listAccessPointByLocation(String country, String community,
			String type, AsyncCallback<AccessPointDto[]> callback);

	void listAllAccessPoints(String cursorString,
			AsyncCallback<List<AccessPointDto>> callback);

	void getCursorString(AsyncCallback<String> callback);

	void listErrorAccessPoints(String cursorString,
			AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>> callback);

	void listCountryCodes(AsyncCallback<List<String>> callback);

	void deleteAccessPoints(AccessPointSearchCriteriaDto searchCriteria,
			AsyncCallback<Void> callback);

	void returnS3Path(AsyncCallback<String> callback);

	void scorePoint(
			AccessPointDto accessPointDto,
			AsyncCallback<ArrayList<AccessPointScoreComputationItemDto>> callback);

	void getAccessPointDtoInfo(AccessPointDto accessPointDto,
			AsyncCallback<DtoValueContainer> callback);

	void saveDtoValueContainer(DtoValueContainer dtoValue,
			AsyncCallback<DtoValueContainer> callback);
}
