/*
 *  Copyright (C) 2026 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.domain.persistent.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * A report that Flow Services accepted and never finished stays QUEUED or
 * IN_PROGRESS forever, and the dashboard draws both as "Generating". Four such
 * rows outlived the September outage, which is what this sweep is for.
 *
 * The deadline is passed in rather than derived from the clock. BaseDAO.save
 * stamps lastUpdateDateTime with the current time on every write, so a report
 * six hours in the past cannot be created through the DAO at all; naming a
 * deadline in the future puts freshly saved reports on the far side of it and
 * tests the same comparison.
 */
public class ReapStalledReportsTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private ReportDao reportDao;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        reportDao = new ReportDao();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    private Report existingReport(String state) {
        Report report = new Report();
        report.setState(state);
        report.setReportType("DATA_CLEANING");
        return reportDao.save(report);
    }

    private static Date inAnHour() {
        Calendar deadline = Calendar.getInstance();
        deadline.add(Calendar.HOUR_OF_DAY, 1);
        return deadline.getTime();
    }

    private Report reload(Report report) {
        return reportDao.getByKey(report.getKey().getId());
    }

    @Test
    public void failsReportsTheEngineNeverFinished() {
        Report inProgress = existingReport(Report.IN_PROGRESS);

        new CronCommanderServlet().reapStalledReports(inAnHour());

        Report swept = reload(inProgress);
        assertEquals(Report.FINISHED_ERROR, swept.getState());
        assertTrue(swept.getMessage() != null && swept.getMessage().length() > 0,
                "a failed report needs a message, since the dashboard shows it in place of the link");
    }

    @Test
    public void sweepsQueuedReportsToo() {
        // The dashboard renders QUEUED as "Generating" as well, so a report
        // whose start task was lost is indistinguishable to the person waiting.
        Report queued = existingReport(Report.QUEUED);

        new CronCommanderServlet().reapStalledReports(inAnHour());

        assertEquals(Report.FINISHED_ERROR, reload(queued).getState());
    }

    @Test
    public void leavesReportsStillWithinTheDeadlineAlone() {
        Report running = existingReport(Report.IN_PROGRESS);

        Calendar anHourAgo = Calendar.getInstance();
        anHourAgo.add(Calendar.HOUR_OF_DAY, -1);
        new CronCommanderServlet().reapStalledReports(anHourAgo.getTime());

        assertEquals(Report.IN_PROGRESS, reload(running).getState(),
                "a report that is merely slow must not be failed out from under its requester");
    }

    @Test
    public void leavesFinishedReportsAlone() {
        Report succeeded = existingReport(Report.FINISHED_SUCCESS);
        Report failed = existingReport(Report.FINISHED_ERROR);

        new CronCommanderServlet().reapStalledReports(inAnHour());

        assertEquals(Report.FINISHED_SUCCESS, reload(succeeded).getState());
        assertEquals(Report.FINISHED_ERROR, reload(failed).getState());
    }

    @Test
    public void listsNothingWhenEveryReportIsFinished() {
        existingReport(Report.FINISHED_SUCCESS);

        List<Report> stalled = reportDao.listStalledBefore(inAnHour());

        assertEquals(0, stalled.size());
    }
}
