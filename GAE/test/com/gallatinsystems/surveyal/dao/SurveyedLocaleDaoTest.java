/*
 *  Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.dao;

import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SurveyedLocaleDaoTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    private Long randomLong(long limit) {
        return ThreadLocalRandom.current().nextLong(0, (limit == -1 ? 999999999 : limit));
    }

    private void generateData(long count, long surveyId) {

        List<SurveyedLocale> data = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            SurveyedLocale dataPoint = new SurveyedLocale();
            long id = randomLong(-1);
            dataPoint.setKey(KeyFactory.createKey("SurveyedLocale", id));
            dataPoint.setSurveyGroupId(surveyId);
            dataPoint.setDisplayName("Data point: " + id);
            dataPoint.setIdentifier(SurveyedLocale.generateBase32Uuid());
            data.add(dataPoint);
        }
        new SurveyedLocaleDao().save(data);
    }

    @Test
    public void testDataPointCount() {

        long surveyId = randomLong(1000);
        long count = randomLong(500);

        generateData(count, surveyId);
        generateData(count, surveyId + 1);

        SurveyedLocaleDao dao = new SurveyedLocaleDao();
        long actual = dao.countBySurveyGroupId(surveyId);

        Assertions.assertEquals(count, actual);
    }
}
