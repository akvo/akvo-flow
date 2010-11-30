package org.waterforpeople.mapping.app.gwt.server.devicefiles;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.store.appengine.query.JDOCursorHelper;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.dao.DeviceFilesDao;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DeviceFilesServiceImpl extends RemoteServiceServlet implements
		DeviceFilesService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5012633874057293188L;

	@Override
	public String reprocessDeviceFile(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDto<ArrayList<DeviceFilesDto>>  listDeviceFiles(
			String processingStatus, String cursor) {
		DeviceFilesDao dfDao = new DeviceFilesDao();
		List<DeviceFiles> dfList = (List<DeviceFiles>) dfDao
				.list(cursor);
		ArrayList<DeviceFilesDto> dfDtoList = new ArrayList<DeviceFilesDto>();
		for (DeviceFiles canonical : dfList) {
			DeviceFilesDto dto = new DeviceFilesDto();
			DtoMarshaller.copyToDto(canonical, dto);
			dfDtoList.add(dto);
		}
		ResponseDto<ArrayList<DeviceFilesDto>> responseDto = new ResponseDto<ArrayList<DeviceFilesDto>>();
		responseDto.setCursorString(JDOCursorHelper.getCursor(dfList)
				.toWebSafeString());
		responseDto.setPayload(dfDtoList);
		return responseDto;
	}

}
