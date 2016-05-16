/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.device.dao;

import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * data access object for devices.
 * 
 * @author Christopher Fagiani
 */
public class DeviceDAO extends BaseDAO<Device> {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(DeviceDAO.class
            .getName());

    public DeviceDAO() {
        super(Device.class);
    }

    /**
     * gets a single device by phoneNumber. If phone number is not unique (this shouldn't happen),
     * it returns the first instance found.
     * 
     * @param phoneNumber
     * @return
     */
    public Device get(String phoneNumber) {
        return super.findByProperty("phoneNumber", phoneNumber, STRING_TYPE);
    }

    /**
     * gets a single device by imei/esn. If phone number is not unique (this shouldn't happen), it
     * returns the first instance found.
     * 
     * @param imei
     * @return
     */
    public Device getByImei(String imei) {
        if (Device.NO_IMEI.equals(imei)) {
            // WiFi only devices could have "NO_IMEI" as value
            // We want to fall back to search by `phoneNumber` (MAC address)
            return null;
        }
        return super.findByProperty("esn", imei, STRING_TYPE);
    }

    /**
     * Create or update device
     * 
     * @param phoneNumber
     * @param lat
     * @param lon
     * @param accuracy
     * @param version
     * @param deviceIdentifier
     * @param imei
     */
    public void updateDevice(String phoneNumber, Double lat,
            Double lon, Double accuracy, String version,
            String deviceIdentifier, String imei, String osVersion) {
        if (StringUtils.isEmpty(imei) && StringUtils.isEmpty(phoneNumber)) {
            return;
        }
        
        Device d = null;
        if (imei != null) { // New Apps from 1.10.0 and on provide IMEI/ESN
            d = getByImei(imei);
        }

        if (d == null) {
            d = get(phoneNumber); // Fall back to less-stable ID
        }
        if (d == null) {
            // if device is null, we have to create it
            d = new Device();
            d.setCreatedDateTime(new Date());
            d.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
            d.setPhoneNumber(phoneNumber);
        }
        if (lat != null && lon != null) {
            d.setLastKnownLat(lat);
            d.setLastKnownLon(lon);
            d.setLastKnownAccuracy(accuracy);
        }
        if (deviceIdentifier != null) {
            d.setDeviceIdentifier(deviceIdentifier);
        }
        if (imei != null && !Device.NO_IMEI.equals(imei)) {
            d.setEsn(imei);
        }
        if (osVersion != null) {
            d.setOsVersion(osVersion);
        }
        d.setLastLocationBeaconTime(new Date());
        d.setGallatinSoftwareManifest(version);
        save(d);
    }
}
