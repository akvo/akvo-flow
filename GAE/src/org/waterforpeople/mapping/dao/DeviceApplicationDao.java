
package org.waterforpeople.mapping.dao;

import java.util.List;

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
            String deviceType, String[] appCode, int maxResults) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(DeviceApplication.class);

        StringBuilder filter = new StringBuilder();
        filter.append("deviceType == '").append(deviceType).append("'");
        filter.append(" && (");
        for (int i=0; i < appCode.length; i++) {
            filter.append("appCode == '").append(appCode[i]).append("'");
            if (i < appCode.length - 1) {
                filter.append(" || ");
            }
        }
        filter.append(")");
        query.setFilter(filter.toString());
        query.setOrdering("createdDateTime desc");
        query.setRange(0, maxResults);
        return (List<DeviceApplication>) query.execute();
    }

}
