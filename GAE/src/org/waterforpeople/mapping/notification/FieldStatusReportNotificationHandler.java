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

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

/**
 * report notification handler that can generate the field status report (a simple report consisting
 * only of the list of instances for a given survey). The format of the report is currently a
 * tab-delimited text file with the following columns:<br>
 * Collection Date, Country Code, Community Name
 * 
 * @author Christopher Fagiani
 */
public class FieldStatusReportNotificationHandler extends
        AbstractReportNotificationHandler {

    public static final String TYPE = "fieldStatusReport";
    private static final String EMAIL_TITLE = "FLOW Field Status Report for survey: ";
    private static final String EMAIL_BODY = "Please see the latest field status report here (Recommend you open in Microsoft Excel) for Survey : ";
    private static final int PAGE_SIZE = 100;
    private static final String REPORT_DATE_FORMAT = "MM-dd-yyyy";

    private SurveyInstanceDAO instanceDao;
    private DateFormat df = new SimpleDateFormat(DATE_DISPLAY_FORMAT);
    private DateFormat reportDateFormat = new SimpleDateFormat(
            REPORT_DATE_FORMAT);

    public FieldStatusReportNotificationHandler() {
        super();
        instanceDao = new SurveyInstanceDAO();
    }

    @Override
    protected void writeReport(Long entityId, String serverBase, PrintWriter pw) {
        if (entityId != null) {

            String cursor = null;
            pw.print("Date\tCountry\tCommunity\n");
            do {
                List<SurveyInstance> tempList = instanceDao
                        .listSurveyInstanceBySurvey(entityId, PAGE_SIZE, cursor);
                if (tempList == null) {
                    cursor = null;
                } else {
                    for (SurveyInstance inst : tempList) {
                        pw.print(reportDateFormat.format(inst
                                .getCollectionDate())
                                + "\t"
                                + (inst.getCountryCode() != null ? inst
                                        .getCountryCode() : "")
                                + "\t"
                                + (inst.getCommunity() != null ? inst
                                        .getCommunity() : "") + "\n");
                    }
                    if (tempList.size() < PAGE_SIZE) {
                        cursor = null;
                    } else {
                        cursor = SurveyInstanceDAO.getCursor(tempList);
                    }
                }
            } while (cursor != null);
            pw.flush();
        }
    }

    @Override
    protected String getEmailBody() {
        return EMAIL_BODY;
    }

    @Override
    protected String getEmailSubject() {
        return EMAIL_TITLE;
    }

    @Override
    protected String getFileName(String entityId) {
        return TYPE + "-" + entityId + "-" + df.format(new Date()) + ".txt";
    }

}
