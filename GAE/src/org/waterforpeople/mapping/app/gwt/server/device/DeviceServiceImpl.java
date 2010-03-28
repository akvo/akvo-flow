package org.waterforpeople.mapping.app.gwt.server.device;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DeviceServiceImpl extends RemoteServiceServlet implements
		DeviceService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3606845978482271221L;
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	@Override
	public DeviceDto[] listDevice() {

		DeviceDAO deviceDao = new DeviceDAO();
		List<Device> devices = deviceDao.list();
		DeviceDto[] deviceDtos = null;
		if (devices != null) {
			deviceDtos = new DeviceDto[devices.size()];
			for (int i = 0; i < devices.size(); i++) {
				DeviceDto dto = new DeviceDto();
				Device d = devices.get(i);

				dto.setPhoneNumber(d.getPhoneNumber());
				dto.setEsn(d.getEsn());				
				deviceDtos[i] = dto;
			}
		}
		return deviceDtos;
	}

}
