/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.device.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain object representing handheld devices capable of running the survey application.
 */
@PersistenceCapable
public class Device extends BaseDomain {

    private static final long serialVersionUID = 4894680591207166295L;
    public static final String NO_IMEI = "NO_IMEI";

    private DeviceType deviceType;
    private String androidId;
    private String phoneNumber;
    private String esn;
    private String deviceIdentifier;
    private Date inServiceDate;
    private Date outServiceDate;
    private Date lastUpdate;
    private String osVersion;
    private String gallatinSoftwareManifest;
    private Double lastKnownLat;
    private Double lastKnownLon;
    private Double lastKnownAccuracy;
    private Date lastLocationBeaconTime;
    private String deviceGroup;
    
    public String getAndroidId() {
        return androidId;
    }
    
    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

    public Double getLastKnownLat() {
        return lastKnownLat;
    }

    public void setLastKnownLat(Double lastKnownLat) {
        this.lastKnownLat = lastKnownLat;
    }

    public Double getLastKnownLon() {
        return lastKnownLon;
    }

    public void setLastKnownLon(Double lastKnownLon) {
        this.lastKnownLon = lastKnownLon;
    }

    public Double getLastKnownAccuracy() {
        return lastKnownAccuracy;
    }

    public void setLastKnownAccuracy(Double lastKnownAccuracy) {
        this.lastKnownAccuracy = lastKnownAccuracy;
    }

    public Date getLastLocationBeaconTime() {
        return lastLocationBeaconTime;
    }

    public void setLastLocationBeaconTime(Date lastLocationBeaconTime) {
        this.lastLocationBeaconTime = lastLocationBeaconTime;
    }

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
