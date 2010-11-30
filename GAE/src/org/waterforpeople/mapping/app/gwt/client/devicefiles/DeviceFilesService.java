package org.waterforpeople.mapping.app.gwt.client.devicefiles;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
@RemoteServiceRelativePath("devicefilerpc")
public interface DeviceFilesService extends RemoteService {
	ResponseDto<ArrayList<DeviceFilesDto>> listDeviceFiles(String processingStatus, String cursor);
	String reprocessDeviceFile(Long id);

}
