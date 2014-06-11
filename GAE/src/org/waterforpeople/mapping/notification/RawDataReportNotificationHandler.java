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
import java.util.logging.Logger;

import org.waterforpeople.mapping.dataexport.RawDataExporter;

/**
 * notifier that is capable of generating the raw data report for a survey and emailing it to a list
 * of recipients
 * 
 * @author Christopher Fagiani
 */
public class RawDataReportNotificationHandler extends
        AbstractReportNotificationHandler {
    public static final String TYPE = "rawDataReport";
    private static final String EMAIL_TITLE = "FLOW Raw Data Report for survey: ";
    private static final String EMAIL_BODY = "Please see the latest raw data report here (Recommend you open in Microsoft Excel) for Survey : ";

    private DateFormat df = new SimpleDateFormat(DATE_DISPLAY_FORMAT);

    @SuppressWarnings("unused")
    private static final Logger log = Logger
            .getLogger(RawDataReportNotificationHandler.class.getName());

    public RawDataReportNotificationHandler() {
        super();
    }

    @Override
    protected void writeReport(Long entityId, String serverBase, PrintWriter pw) {
        RawDataExporter exporter = new RawDataExporter();
        exporter.export(serverBase, entityId, pw);
        pw.flush();
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
        return "rawDataReport-" + entityId + "-" + df.format(new Date())
                + ".txt";
    }
}
