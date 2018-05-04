/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import org.waterforpeople.mapping.domain.DeviceApplication;

import javax.jdo.PersistenceManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceApplicationDao extends BaseDAO<DeviceApplication> {

    private static final String LATEST_SUPPORTED_VERSION = "2.4.8";

    public DeviceApplicationDao() {
        super(DeviceApplication.class);
    }

    @SuppressWarnings("unchecked")
    public List<DeviceApplication> listByDeviceTypeAndAppCode(
            String deviceType, String appCode, int maxResults) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceApplication.class);
        Map<String, Object> paramMap;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<>();

        appendNonNullParam("deviceType", filterString, paramString, "String",
                deviceType, paramMap);
        appendNonNullParam("appCode", filterString, paramString, "String",
                appCode, paramMap);
        query.setFilter(filterString.toString());
        query.setOrdering("createdDateTime desc");
        query.declareParameters(paramString.toString());
        query.setRange(0, maxResults);
        return (List<DeviceApplication>) query.executeWithMap(paramMap);
    }

    @SuppressWarnings("unchecked")
    public DeviceApplication listAppVersionForUnsupportedDevices(String deviceType,
            String appCode) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceApplication.class);
        Map<String, Object> paramMap;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<>();
        appendNonNullParam("deviceType", filterString, paramString, "String",
                deviceType, paramMap);
        appendNonNullParam("appCode", filterString, paramString, "String",
                appCode, paramMap);
        appendNonNullParam("version", filterString, paramString, "String",
                LATEST_SUPPORTED_VERSION, paramMap);
        query.setFilter(filterString.toString());
        query.setOrdering("createdDateTime desc");
        query.declareParameters(paramString.toString());
        query.setRange(0, 1);
        List<DeviceApplication> deviceApplications = (List<DeviceApplication>) query
                .executeWithMap(paramMap);

        if (deviceApplications == null || deviceApplications.size() == 0) {
            return null;
        }
        return deviceApplications.get(0);
    }
}
