package com.gallatinsystems.device.dao;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.dao.BaseDAO;

public class DeviceDAO extends BaseDAO<Device> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DeviceDAO.class
			.getName());

	public DeviceDAO() {
		super(Device.class);
	}

	@SuppressWarnings("unchecked")
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
