package com.gallatinsystems.device.dao;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.dao.BaseDAO;

public class DeviceDAO extends BaseDAO {
	private static final Logger log = Logger.getLogger(DeviceDAO.class
			.getName());

	public Device save(Device device) {
		Device deviceExists = get(device.getPhoneNumber());
		if (deviceExists == null) {
			super.save(device);
		}else{
			//update device
		}
		return device;
	}

	public Device get(String phoneNumber) {
		Device device = null;
		javax.jdo.Query query = super.getPersistenceManager().newQuery(
				Device.class);
		query.setFilter("phoneNumber == phoneNumberParam");
		query.declareParameters("String phoneNumberParam");
		List<Device> results = (List<Device>) query.execute(phoneNumber);
		if (results.size() > 0) {
			device = results.get(0);
		}
		return device;
	}

}
