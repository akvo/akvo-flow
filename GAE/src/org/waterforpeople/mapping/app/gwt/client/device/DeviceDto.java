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

package org.waterforpeople.mapping.app.gwt.client.device;

import java.util.Date;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DeviceDto extends BaseDto {

    private static final long serialVersionUID = 3197857074399585732L;
    private String phoneNumber;
    private String esn;
    private Double lastKnownLat;
    private Double lastKnownLon;
    private Double lastKnownAccuracy;
    private Date lastPositionDate;
    private String gallatinSoftwareManifest;
    private String deviceGroup;
    private String deviceGroupName;
    private String deviceIdentifier;
    private Device device;

    public DeviceDto() {

    }

    public DeviceDto(Device device) {
        this.device = device;
    }

    public String getGallatinSoftwareManifest() {
        if (this.device != null) {
            return device.getGallatinSoftwareManifest();
        }
        return gallatinSoftwareManifest;
    }

    public void setGallatinSoftwareManifest(String gallatinSoftwareManifest) {
        this.gallatinSoftwareManifest = gallatinSoftwareManifest;
    }

    public String getDeviceIdentifier() {
        if (this.device != null) {
            return device.getDeviceIdentifier();
        }
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceGroup() {
        if (this.device != null) {
            return device.getDeviceGroup();
        }
        return deviceGroup;
    }

    public void setDeviceGroup(String deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

    public Double getLastKnownLat() {
        if (this.device != null) {
            return device.getLastKnownLat();
        }
        return lastKnownLat;
    }

    public void setLastKnownLat(Double lastKnownLat) {
        this.lastKnownLat = lastKnownLat;
    }

    public Double getLastKnownLon() {
        if (this.device != null) {
            return device.getLastKnownLon();
        }
        return lastKnownLon;
    }

    public void setLastKnownLon(Double lastKnownLon) {
        this.lastKnownLon = lastKnownLon;
    }

    public Double getLastKnownAccuracy() {
        if (this.device != null) {
            return device.getLastKnownAccuracy();
        }
        return lastKnownAccuracy;
    }

    public void setLastKnownAccuracy(Double lastKnownAccuracy) {
        this.lastKnownAccuracy = lastKnownAccuracy;
    }

    public Date getLastPositionDate() {
        if (this.device != null) {
            return device.getLastLocationBeaconTime();
        }
        return lastPositionDate;
    }

    public void setLastPositionDate(Date lastPositionDate) {
        this.lastPositionDate = lastPositionDate;
    }

    public String getEsn() {
        if (this.device != null) {
            return device.getEsn();
        }
        return esn;
    }

    public void setEsn(String esn) {
        this.esn = esn;
    }

    public String getPhoneNumber() {
        if (this.device != null) {
            return device.getPhoneNumber();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }

    public String getDeviceGroupName() {
        return deviceGroupName;
    }
}
