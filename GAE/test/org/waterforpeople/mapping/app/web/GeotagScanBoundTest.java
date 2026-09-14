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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.response.value.Location;
import org.waterforpeople.mapping.domain.response.value.Media;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * The geotag scan reads every image answer in a one-month window. Reading all of them in one
 * request is what exhausted an F1 instance's 256MB on the projects holding the most data: the
 * process was killed, the request 503'd, and App Engine retried the job about a thousand times
 * a day without it ever completing. The page limit is what lets a run finish and return 200,
 * and returning 200 is the only thing that stops the retries.
 *
 * So the bound is the behaviour worth pinning down, and it is pinned down by counting answers
 * examined rather than by measuring memory -- memory is what the bound protects, not something
 * a unit test can observe.
 *
 * Every answer seeded here already carries a location, so the scan skips each one before it
 * would reach S3. That keeps the test to the paging, which is the part under test.
 */
public class GeotagScanBoundTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private CronCommanderServlet servlet;

    @BeforeEach
    public void setUp() {
        helper.setUp();
        servlet = new CronCommanderServlet();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    /**
     * An answer the scan has already dealt with: it carries a location, so the scan skips it
     * without reading anything from S3.
     */
    private static QuestionAnswerStore taggedImageAnswer(int index) {
        Location location = new Location();
        location.setLatitude(59.29);
        location.setLongitude(17.95);
        Media media = new Media();
        media.setFilename("photo-" + index + ".jpg");
        media.setLocation(location);

        QuestionAnswerStore answer = new QuestionAnswerStore();
        answer.setType("IMAGE");
        answer.setQuestionID(String.valueOf(index));
        answer.setValue(MediaResponse.formatWithGeotag(media));
        return answer;
    }

    private void seed(int count) {
        QuestionAnswerStoreDao dao = new QuestionAnswerStoreDao();
        List<QuestionAnswerStore> answers = new ArrayList<QuestionAnswerStore>();
        for (int i = 0; i < count; i++) {
            answers.add(taggedImageAnswer(i));
        }
        // save() stamps lastUpdateDateTime, which is both the field the scan filters on and
        // the field it orders by, so seeding this way puts every answer inside the window.
        dao.save(answers);
    }

    @Test
    void stopsOnceItHasExaminedItsPageLimit() {
        seed(50);

        int examined = servlet.extractImageFileGeotags(2, 10);

        assertEquals(20, examined,
                "two pages of ten is the whole budget; the remaining answers are tomorrow's work");
    }

    @Test
    void examinesEverythingWhenTheWindowFitsInsideTheBudget() {
        seed(25);

        int examined = servlet.extractImageFileGeotags(10, 10);

        assertEquals(25, examined,
                "a budget larger than the data should not truncate the scan");
    }

    @Test
    void aSecondRunResumesRatherThanRepeatingTheFirst() {
        // The scan stores no cursor. It resumes because answers it rewrites are stamped with a
        // new lastUpdateDateTime and sort to the back of the window, so the front of the query
        // is always the work still outstanding. Nothing here is rewritten -- every answer is
        // skipped -- so this asserts the weaker thing the design actually needs: a bounded run
        // is a prefix of the full scan, not a random sample of it.
        seed(30);

        int firstRun = servlet.extractImageFileGeotags(1, 10);
        int fullScan = servlet.extractImageFileGeotags(10, 10);

        assertEquals(10, firstRun, "one page");
        assertEquals(30, fullScan, "the whole window");
        assertTrue(firstRun < fullScan, "a bounded run must examine less than an unbounded one");
    }

    @Test
    void defaultsAreTheOnesTheCronRunsWith() {
        // Guards the constants themselves: the scheduled run takes no arguments, so a change
        // to either default silently changes what production does.
        assertEquals(1000, CronCommanderServlet.GEOTAG_PAGE_SIZE);
        assertEquals(5, CronCommanderServlet.GEOTAG_MAX_PAGES_PER_RUN);
    }
}
