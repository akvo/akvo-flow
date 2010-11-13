package com.gallatinsystems.diagnostics.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Data structure for storing remote stack traces
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class RemoteStacktrace extends BaseDomain {

	private static final long serialVersionUID = -1300539770565100348L;
	private String deviceIdentifier;
	private String phoneNumber;
	private String stackTrace;
	private String softwareVersion;
	private Boolean acknowleged = new Boolean(false);	
	private Date errorDate;

	
	public Boolean getAcknowleged() {
		return acknowleged;
	}

	public void setAcknowleged(Boolean acknowleged) {
		this.acknowleged = acknowleged;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

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

	public Date getErrorDate() {
		return errorDate;
	}

	public void setErrorDate(Date errorDate) {
		this.errorDate = errorDate;
	}

}
