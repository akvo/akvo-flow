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

package org.waterforpeople.mapping.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.Status;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class DeviceFilesDao extends BaseDAO<DeviceFiles> {

    public DeviceFilesDao() {
        super(DeviceFiles.class);
    }

    @SuppressWarnings("unchecked")
    public List<DeviceFiles> listDeviceFilesByDate(Date startDate,
            String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceFiles.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();
        /*
         * appendNonNullParam("createdDateTime", filterString, paramString, "Date", startDate,
         * paramMap, GTE_OP); if (startDate != null) {
         * query.declareImports("import java.util.Date"); }
         */
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String dateTime = dateFormat.format(startDate);
        appendNonNullParam("processDate", filterString, paramString, "String",
                dateTime, paramMap, GTE_OP);
        // query.setOrdering("createdDateTime desc");
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        prepareCursor(cursorString, query);

        List<DeviceFiles> results = (List<DeviceFiles>) query
                .executeWithMap(paramMap);

        return results;
    }

    public DeviceFiles findByInstance(Long instanceId) {
        return findByProperty("surveyInstanceId", instanceId, "Long");
    }

    public DeviceFiles findByUri(String uri) {
        List<DeviceFiles> dfList = (List<DeviceFiles>) listByProperty("URI",
                uri, "String");
        if (dfList.size() > 0)
            return dfList.get(0);
        else
            return null;
    }

    public List<DeviceFiles> listByUri(String uri) {
        List<DeviceFiles> dfList = (List<DeviceFiles>) listByProperty("URI",
                uri, "String");
        if (dfList.size() > 0)
            return dfList;
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    public List<DeviceFiles> listDeviceFilesByStatus(Status.StatusCode status,
            String cursorString) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceFiles.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();
        appendNonNullParam("processedStatus", filterString, paramString,
                "String", status, paramMap, EQ_OP);
        query.setOrdering("createdDateTime desc URI asc");
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());

        prepareCursor(cursorString, query);

        List<DeviceFiles> results = (List<DeviceFiles>) query
                .executeWithMap(paramMap);

        return results;
    }
}
