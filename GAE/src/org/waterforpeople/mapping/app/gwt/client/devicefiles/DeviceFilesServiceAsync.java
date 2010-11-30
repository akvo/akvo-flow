package org.waterforpeople.mapping.app.gwt.client.devicefiles;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DeviceFilesServiceAsync {

	void listDeviceFiles(String processingStatus, String cursor,
			AsyncCallback<ResponseDto<ArrayList<DeviceFilesDto>>> callback);

	void reprocessDeviceFile(String uri, AsyncCallback<String> callback);

}
