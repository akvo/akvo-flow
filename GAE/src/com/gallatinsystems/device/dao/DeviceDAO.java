package com.gallatinsystems.device.dao;

import java.util.Date;
import java.util.logging.Logger;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
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

	/**
	 * updates the device's last known position
	 * 
	 * @param phoneNumber
	 * @param lat
	 * @param lon
	 * @param accuracy
	 */
	public void updateDeviceLocation(String phoneNumber, Double lat,
			Double lon, Double accuracy) {
		Device d = get(phoneNumber);
		if (d == null) {
			// if device is null, we have to create it
			d = new Device();
			d.setCreatedDateTime(new Date());
			d.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
			d.setPhoneNumber(phoneNumber);
		}
		d.setLastKnownAccuracy(accuracy);
		d.setLastKnownLat(lat);
		d.setLastKnownLon(lon);
		d.setLastLocationBeaconTime(new Date());
		save(d);
	}

}
