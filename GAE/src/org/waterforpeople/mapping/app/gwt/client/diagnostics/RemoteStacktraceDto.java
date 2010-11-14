package org.waterforpeople.mapping.app.gwt.client.diagnostics;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * dto for use with the remote exception service
 * 
 * @author Christopher Fagiani
 * 
 */
public class RemoteStacktraceDto extends BaseDto {

	private static final long serialVersionUID = 8104104669231012653L;
	
	private String deviceIdentifier;
	private String phoneNumber;
	private String stackTrace;
	private String softwareVersion;
	private Boolean acknowleged;	
	private Date errorDate;

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public Boolean getAcknowleged() {
		return acknowleged;
	}

	public void setAcknowleged(Boolean acknowleged) {
		this.acknowleged = acknowleged;
	}

	public Date getErrorDate() {
		return errorDate;
	}

	public void setErrorDate(Date errorDate) {
		this.errorDate = errorDate;
	}

}
