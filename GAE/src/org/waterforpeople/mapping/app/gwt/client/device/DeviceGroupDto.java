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

import java.util.ArrayList;
import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class DeviceGroupDto extends BaseDto implements NamedObject {

    private static final long serialVersionUID = -2235565143615667202L;

    private String description;
    private String name;
    private String code;
    private Date createdDateTime;
    private Date lastUpdateDateTime;

    private ArrayList<DeviceDto> deviceList = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Date getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

    public void setDeviceList(ArrayList<DeviceDto> deviceList) {
        this.deviceList = deviceList;
    }

    public ArrayList<DeviceDto> getDeviceList() {
        return deviceList;
    }

    public void addDevice(DeviceDto item) {
        if (deviceList == null) {
            deviceList = new ArrayList<DeviceDto>();
        }
        deviceList.add(item);
    }

    @Override
    public String getDisplayName() {
        return getCode();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
