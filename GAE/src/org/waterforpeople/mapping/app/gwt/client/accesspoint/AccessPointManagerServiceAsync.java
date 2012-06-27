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

import java.util.ArrayList;
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

	void scorePointDynamic(AccessPointDto accessPointDto,
			AsyncCallback<ArrayList<AccessPointScoreDetailDto>> callback);
}
