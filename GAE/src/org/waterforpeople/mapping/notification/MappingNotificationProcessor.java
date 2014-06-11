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

package org.waterforpeople.mapping.notification;

import java.util.HashMap;

import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.notification.NotificationProcessor;

/**
 * handles notifications specific to surveys
 * 
 * @author Christopher Fagiani
 */
public class MappingNotificationProcessor extends NotificationProcessor {

    private static final long serialVersionUID = -9055657567284631932L;

    @Override
    protected void initializeTypeMapping() {
        notificationTypeMap = new HashMap<String, String>();
        notificationTypeMap.put(RawDataReportNotificationHandler.TYPE,
                RawDataReportNotificationHandler.class.getCanonicalName());
        notificationTypeMap.put(FieldStatusReportNotificationHandler.TYPE,
                FieldStatusReportNotificationHandler.class.getCanonicalName());
        for (int i = 0; i < SurveyEventNotificationHandler.EVENTS.length; i++) {
            notificationTypeMap.put(SurveyEventNotificationHandler.EVENTS[i],
                    SurveyEventNotificationHandler.class.getCanonicalName());
        }
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // no-op

    }
}
