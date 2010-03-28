package com.gallatinsystems.device.dao;

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
	
	public Device get(String phoneNumber) {
		return super.findByProperty("phoneNumber", phoneNumber, STRING_TYPE);
	}

}
