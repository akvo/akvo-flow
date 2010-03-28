package org.waterforpeople.mapping.app.gwt.client.device;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DeviceServiceAsync {

	void listDevice(AsyncCallback<DeviceDto[]> callback);

}
