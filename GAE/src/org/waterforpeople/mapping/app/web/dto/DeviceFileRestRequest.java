package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class DeviceFileRestRequest extends RestRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1363635676106322333L;
	public static final String LIST_DEVICE_FILES_ACTION = "listDeviceFiles";
	public static final String FIND_DEVICE_FILE_ACTION = "findDeviceFile";
	public static final String CURSOR_PARAM = "cursor";
	public static final String PROCESSED_STATUS_PARAM = "processedStatus";
	public static final String DEVICE_FULL_PATH="deviceFullPath";

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public String getProcessedStatus() {
		return processedStatus;
	}

	public void setProcessedStatus(String status) {
		this.processedStatus = status;
	}

	private String cursor = null;
	private String processedStatus = null;
	private String deviceFullPath = null;

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(CURSOR_PARAM) != null) {
			setCursor(req.getParameter(CURSOR_PARAM));
		}
		if (req.getParameter(PROCESSED_STATUS_PARAM) != null) {
			setProcessedStatus(req.getParameter(PROCESSED_STATUS_PARAM));
		}
		if(req.getParameter(DEVICE_FULL_PATH)!=null){
			setDeviceFullPath(req.getParameter(DEVICE_FULL_PATH));
		}

	}

	@Override
	protected void populateErrors() {
		// TODO Auto-generated method stub

	}

	public void setDeviceFullPath(String deviceFullPath) {
		this.deviceFullPath = deviceFullPath;
	}

	public String getDeviceFullPath() {
		return deviceFullPath;
	}

}
