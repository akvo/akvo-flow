/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class DeviceNotificationRequest extends LocationBeaconRequest {

    private static final long serialVersionUID = 287773188257035166L;
    private static final String SURVEYS_PARAM = "formId";
    
    private Set<Long> surveyIds;
    
    public Set<Long> getSurveyIds() {
        return surveyIds;
    }
    
    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        super.populateFields(req);
        
        surveyIds = new HashSet<>();
        String[] args = req.getParameterValues(SURVEYS_PARAM);
        if (args != null) {
            for (String sid : args) {
                try {
                    surveyIds.add(Long.parseLong(sid));
                } catch (NumberFormatException e) {
                    // ignore non-valid keys
                }
            }
        }
    }
    

    @Override
    protected void populateErrors() {
        // intentionally overriding LocationBeaconRequest checks
    }

}
