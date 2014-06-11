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

package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * DTO for requests to the summarization apis
 * 
 * @author Christopher Fagiani
 */
public class MappingSummarizationRequest extends RestRequest {

    private static final long serialVersionUID = 2509976216047671455L;
    private static final String REGION_UUID = "regionUUID";
    private static final String TYPE = "type";

    private String regionUUID;
    private String summarizationType;

    public String getRegionUUID() {
        return regionUUID;
    }

    public void setRegionUUID(String regionUUID) {
        this.regionUUID = regionUUID;
    }

    public String getSummarizationType() {
        return summarizationType;
    }

    public void setSummarizationType(String summarizationType) {
        this.summarizationType = summarizationType;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        regionUUID = req.getParameter(REGION_UUID);
        summarizationType = req.getParameter(TYPE);
    }

    @Override
    protected void populateErrors() {
        if (regionUUID == null) {
            String errorMsg = REGION_UUID + " is mandatory";
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
        }
    }
}
