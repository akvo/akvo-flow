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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("apmanagerrpcservice")
public interface AccessPointManagerService extends RemoteService {
	List<AccessPointDto> listAllAccessPoints(String cursorString);

	AccessPointDto getAccessPoint(Long id);

	AccessPointDto saveAccessPoint(AccessPointDto accessPointDto);

	Integer deleteAccessPoint(Long id);

	
	void rotateImage(String fileName);

	AccessPointDto[] listAccessPointByLocation(String country,
			String community, String type);

	ResponseDto<ArrayList<AccessPointDto>> listAccessPoints(
			AccessPointSearchCriteriaDto searchCriteria, String cursorString);
	
	String getCursorString();
	ResponseDto<ArrayList<AccessPointDto>> listErrorAccessPoints(String cursorString);
	List<String> listCountryCodes();

	void deleteAccessPoints(AccessPointSearchCriteriaDto searchCriteria);
	String returnS3Path();
	
	ArrayList<AccessPointScoreComputationItemDto> scorePoint(AccessPointDto accessPointDto);
	ArrayList<AccessPointScoreDetailDto> scorePointDynamic(AccessPointDto accessPointDto); 
	DtoValueContainer getAccessPointDtoInfo(AccessPointDto accessPointDto);
	DtoValueContainer saveDtoValueContainer(DtoValueContainer dtoValue);
}
