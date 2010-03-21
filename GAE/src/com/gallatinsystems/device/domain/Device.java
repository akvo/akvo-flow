package com.gallatinsystems.device.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Device extends BaseDomain {
	private DeviceType deviceType;
	private String phoneNumber;
	private String esn;
	private Date inServiceDate;
	private Date outServiceDate;
	private Date lastUpdate;
	private String osVersion;
	private String gallatinSoftwareManifest;

	public enum DeviceType {
		CELL_PHONE_ANDROID, TABLET_ANDROID, TABLET_PHONE_ANDROID
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

}
