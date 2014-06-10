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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * dto to transfer survey/device assignments
 * 
 * @author Christopher Fagiani
 */
public class SurveyAssignmentDto extends BaseDto {

    private static final long serialVersionUID = -5023203087864769109L;
    private ArrayList<SurveyDto> surveys;
    private ArrayList<DeviceDto> devices;
    private Date startDate;
    private Date endDate;
    private String name;
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SurveyDto> getSurveys() {
        return surveys;
    }

    public void setSurveys(ArrayList<SurveyDto> surveys) {
        this.surveys = surveys;
    }

    public ArrayList<DeviceDto> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<DeviceDto> devices) {
        this.devices = devices;
    }

    /**
     * validates the object and returns an array list of errors. If object is valid, this returns an
     * empty list
     * 
     * @return
     */
    public ArrayList<String> getErrorMessages() {
        ArrayList<String> errorMessages = new ArrayList<String>();
        if (name == null || name.trim().length() == 0) {
            errorMessages.add("Name is a mandatory field");
        }
        if (startDate == null) {
            errorMessages.add("Start date is a mandatory field");
        }
        if (endDate == null) {
            errorMessages.add("End date is a mandatory field");
        }
        if (language == null) {
            errorMessages.add("Language is a mandatory field");
        }
        if (devices == null || devices.size() == 0) {
            errorMessages.add("You must select 1 or more devices");
        }
        if (surveys == null || surveys.size() == 0) {
            errorMessages.add("You must select 1 or more surveys");
        }

        return errorMessages;
    }

}
