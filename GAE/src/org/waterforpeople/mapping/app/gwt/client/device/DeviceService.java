package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("devicerpcservice")
public interface DeviceService extends RemoteService {

	public DeviceDto[] listDevice();
	/**
	 * lists all devices and groups them by group name
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<DeviceDto>> listDeviceByGroup();
	
	/**
	 * finds a device by its phone number
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public DeviceDto findDeviceByPhoneNumber(String phoneNumber);

}
