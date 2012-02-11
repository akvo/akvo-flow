package org.waterforpeople.mapping.app.gwt.server.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service allowing listing/saving of Device objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class DeviceServiceImpl extends RemoteServiceServlet implements
		DeviceService {

	private static final long serialVersionUID = -3606845978482271221L;
	private static final String UNASSIGNED_GROUP = "Unassigned";

	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(DeviceServiceImpl.class.getName());

	@Override
	public DeviceDto[] listDevice() {

		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.list(Constants.ALL_RESULTS);
		DeviceDto[] deviceDtos = null;
		if (devices != null) {
			deviceDtos = new DeviceDto[devices.size()];
			for (int i = 0; i < devices.size(); i++) {
				deviceDtos[i] = marshalDevice(devices.get(i));
			}
		}
		return deviceDtos;
	}

	/**
	 * converts a device domain object to a dto
	 * 
	 * @param d
	 * @return
	 */
	private DeviceDto marshalDevice(Device d) {
		DeviceDto dto = new DeviceDto();
		dto.setPhoneNumber(d.getPhoneNumber());
		dto.setEsn(d.getEsn());
		dto.setLastKnownAccuracy(d.getLastKnownAccuracy());
		dto.setLastKnownLat(d.getLastKnownLat());
		dto.setLastKnownLon(d.getLastKnownLon());
		dto.setLastPositionDate(d.getLastLocationBeaconTime());
		dto.setDeviceGroup(d.getDeviceGroup());
		dto.setKeyId(d.getKey().getId());
		dto.setDeviceIdentifier(d.getDeviceIdentifier());
		return dto;
	}

	/**
	 * lists all devices and groups them by group name
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<DeviceDto>> listDeviceByGroup() {
		HashMap<String, ArrayList<DeviceDto>> groupedDevices = new HashMap<String, ArrayList<DeviceDto>>();
		DeviceDto[] dtos = listDevice();
		if (dtos != null) {
			for (int i = 0; i < dtos.length; i++) {
				String groupName = dtos[i].getDeviceGroup();
				if (groupName == null) {
					groupName = UNASSIGNED_GROUP;
				}
				ArrayList<DeviceDto> dtoList = groupedDevices.get(groupName);
				if (dtoList == null) {
					dtoList = new ArrayList<DeviceDto>();
					groupedDevices.put(groupName, dtoList);
				}
				dtoList.add(dtos[i]);
			}
		}

		return groupedDevices;

	}

	/**
	 * finds a device by its phone number
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public DeviceDto findDeviceByPhoneNumber(String phoneNumber) {
		DeviceDAO dao = new DeviceDAO();
		Device d = dao.get(phoneNumber);
		if (d != null) {
			return marshalDevice(d);
		} else {
			return null;
		}
	}
}
