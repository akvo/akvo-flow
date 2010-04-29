package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DeviceServiceAsync {

	void listDevice(AsyncCallback<DeviceDto[]> callback);

	void listDeviceByGroup(
			AsyncCallback<HashMap<String, ArrayList<DeviceDto>>> callback);

}
