/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest.dto;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DataPointAssignmentDto extends BaseDto {

    private static final long serialVersionUID = -2656272454009393589L;
    private Long surveyAssignmentId; //Which survey assignment this is part of
    private Long deviceId;
    private List<Long> dataPointIds;


    public Long getSurveyAssignmentId() {
        return surveyAssignmentId;
    }
    public void setSurveyAssignmentId(Long surveyAssignmentId) {
        this.surveyAssignmentId = surveyAssignmentId;
    }
    public Long getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    public List<Long> getDataPointIds() {
        return dataPointIds;
    }
    public void setDataPointIds(List<Long> dataPoints) {
        this.dataPointIds = dataPoints;
    }

}
