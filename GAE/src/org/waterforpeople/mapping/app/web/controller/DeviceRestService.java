package org.waterforpeople.mapping.app.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;


@Controller
@RequestMapping("/device")
public class DeviceRestService {

	@Inject
	private DeviceDAO deviceDao;

	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public List<DeviceDto> listDevices() {
		List<DeviceDto> deviceList = new ArrayList<DeviceDto>();
		List<Device> devices = deviceDao.list(Constants.ALL_RESULTS);

		if (devices != null) {
			for (Device d : devices) {
				DeviceDto deviceDto = new DeviceDto();
				DtoMarshaller.copyToDto(d, deviceDto);
				deviceList.add(deviceDto);
			}
		}
		return deviceList;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public DeviceDto findDevice(@PathVariable("id") Long id) {
		Device d = deviceDao.getByKey(id);
		DeviceDto dto = null;
		if (d != null) {
			dto = new DeviceDto();
			DtoMarshaller.copyToDto(d, dto);
		}
		return dto;
	}
}
