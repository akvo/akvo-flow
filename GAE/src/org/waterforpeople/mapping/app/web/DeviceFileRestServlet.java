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

package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.DeviceFileFindRestResponse;
import org.waterforpeople.mapping.app.web.dto.DeviceFileRestRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceFileRestResponse;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceFileRestServlet extends AbstractRestApiServlet {
    @SuppressWarnings("unused")
    private static final Logger log = Logger
            .getLogger(DeviceFileRestServlet.class.getName());
    DeviceFilesDao dfDao = null;

    public DeviceFileRestServlet() {
        super();
        setMode(JSON_MODE);
        dfDao = new DeviceFilesDao();
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -3626408824020380901L;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DeviceFileRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {

        DeviceFileRestRequest importReq = (DeviceFileRestRequest) req;
        if (DeviceFileRestRequest.LIST_DEVICE_FILES_ACTION.equals(importReq
                .getAction())) {
            List<DeviceFilesDto> dtoList = new ArrayList<DeviceFilesDto>();
            String cursor = importReq.getCursor();
            List<DeviceFiles> dfList = dfDao.listDeviceFilesByStatus(
                    StatusCode.valueOf(importReq.getProcessedStatus()), cursor);
            for (DeviceFiles instance : dfList) {
                DeviceFilesDto dto = new DeviceFilesDto();
                DtoMarshaller.copyToDto(instance, dto);
                dtoList.add(dto);
            }
            DeviceFileRestResponse response = new DeviceFileRestResponse();
            cursor = BaseDAO.getCursor(dfList);
            response.setDtoList(dtoList);
            response.setCursor(cursor);
            return response;
        } else if (DeviceFileRestRequest.FIND_DEVICE_FILE_ACTION
                .equals(importReq.getAction())) {
            String deviceFileFullPath = importReq.getDeviceFullPath().trim();
            Boolean foundFlag = false;
            if (deviceFileFullPath != null && !deviceFileFullPath.equals("")) {
                DeviceFiles df = dfDao.findByUri(deviceFileFullPath);
                DeviceFileFindRestResponse response = new DeviceFileFindRestResponse();
                DeviceFilesDto dto = null;
                if (df != null) {
                    dto = new DeviceFilesDto();
                    DtoMarshaller.copyToDto(df, dto);
                    foundFlag = true;
                }
                response.setFoundFlag(foundFlag);
                response.setDeviceFile(dto);
                return response;
            }
        }
        return null;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
        getResponse().getWriter().println(new JSONObject(resp).toString());
    }

}
