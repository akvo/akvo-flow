
package org.waterforpeople.mapping.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.DeviceApplication;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class DeviceApplicationDao extends BaseDAO<DeviceApplication> {

    public DeviceApplicationDao() {
        super(DeviceApplication.class);
    }

    @SuppressWarnings("unchecked")
    public List<DeviceApplication> listByDeviceTypeAndAppCode(
            String deviceType, String appCode, int maxResults) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceApplication.class);
        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

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

}
