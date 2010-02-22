package com.gallatinsystems.survey.domain;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceDefinition {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	private DeviceType deviceType;
	private String phoneNumber;
	private String esn;
	private Date inServiceDate;
	private Date outServiceDate;
	private Date lastUpdate;
	private String osVersion;
	private String gallatinSoftwareManifest;
	
	public enum DeviceType{
		CELL_PHONE_ANDROID,TABLET_ANDROID,TABLET_PHONE_ANDROID
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEsn() {
		return esn;
	}

	public void setEsn(String esn) {
		this.esn = esn;
	}

	public Date getInServiceDate() {
		return inServiceDate;
	}

	public void setInServiceDate(Date inServiceDate) {
		this.inServiceDate = inServiceDate;
	}

	public Date getOutServiceDate() {
		return outServiceDate;
	}

	public void setOutServiceDate(Date outServiceDate) {
		this.outServiceDate = outServiceDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getGallatinSoftwareManifest() {
		return gallatinSoftwareManifest;
	}

	public void setGallatinSoftwareManifest(String gallatinSoftwareManifest) {
		this.gallatinSoftwareManifest = gallatinSoftwareManifest;
	}
	
	

}
