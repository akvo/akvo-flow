package org.waterforpeople.mapping.app.gwt.client.device;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("devicerpcservice")
public interface DeviceService extends RemoteService {

	public DeviceDto[] listDevice();
}
