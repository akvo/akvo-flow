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

package org.waterforpeople.mapping.dataexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;
import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.google.appengine.api.datastore.Text;

public class DeviceFilesReplicationImporter {
    private final static Logger log = Logger.getLogger(DeviceFilesReplicationImporter.class
            .getName());

    public void executeImport(String sourceBase, String serverBase) {
        DeviceFilesDao dfDao = new DeviceFilesDao();
        int i = 0;
        List<DeviceFiles> dflist = fetchDeviceFiles(
                StatusCode.PROCESSED_WITH_ERRORS, sourceBase);
        log.log(Level.INFO, "fetched " + dflist.size() + " devicefiles");
        for (DeviceFiles df : dflist) {
            DeviceFiles dfLocal = new DeviceFiles();
            dfLocal.setURI(df.getURI());
            dfLocal.setPhoneNumber(df.getPhoneNumber());
            dfLocal.setProcessedStatus(df.getProcessedStatus());
            dfLocal.setProcessingMessageText(df.getProcessingMessageText());
            dfLocal.setProcessDate(df.getProcessDate());
            dfDao.save(dfLocal);
            i++;
            log.log(Level.INFO, "Saved devicefiles record: " + i);

        }

    }

    private List<DeviceFiles> fetchDeviceFiles(StatusCode statusCode,
            String serverBase) {
        List<DeviceFilesDto> dtoList = null;
        List<DeviceFiles> canonicalList = new ArrayList<DeviceFiles>();
        try {
            dtoList = BulkDataServiceClient.fetchDeviceFiles(
                    statusCode.toString(), serverBase);
        } catch (IOException iex) {
            iex.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
        if (dtoList != null)
            return copyAndCreateList(canonicalList, dtoList);
        else
            return null;
    }

    public static List<DeviceFiles> copyAndCreateList(
            List<DeviceFiles> canonicalList, List<DeviceFilesDto> dtoList) {

        for (DeviceFilesDto dto : dtoList) {
            DeviceFiles canonical = null;
            canonical = new DeviceFiles();
            canonical.setChecksum(dto.getChecksum());
            canonical.setPhoneNumber(dto.getPhoneNumber());
            canonical.setProcessDate(dto.getProcessDate());
            canonical.setProcessingMessageText(new Text(dto
                    .getProcessingMessage()));
            if (dto.getProcessedStatus() != null)
                canonical.setProcessedStatus(StatusCode.valueOf(dto
                        .getProcessedStatus()));
            else
                canonical.setProcessedStatus(null);
            canonical.setURI(dto.getURI());
            canonicalList.add(canonical);
        }
        return canonicalList;
    }

}
