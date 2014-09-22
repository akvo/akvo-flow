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

package org.waterforpeople.mapping.app.web.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.StandardScoreBucket;
import com.gallatinsystems.standards.domain.StandardScoring;

public class StandardScoringTest {

    public void populateData() {
        ArrayList<StandardScoreBucket> sbucketlist = populateScoreBuckets();

        Calendar calendar = new GregorianCalendar(2010, Calendar.JANUARY, 1);
        Date effectiveStartDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 5000);
        Date effectiveEndDate = calendar.getTime();

        StandardScoringDao ssDao = new StandardScoringDao();
        StandardScoring ss = new StandardScoring();
        ss.setGlobalStandard(true);
        ss.setDisplayName("Improved Water Point Evalutation");
        ss.setPointType("WATER_POINT");
        ss.setEvaluateField("TypeTechnologyString");
        ss.setPositiveOperator("!=");
        ss.setPositiveCriteria("NO_IMPROVED");
        ss.setCriteriaType("String");
        ss.setPositiveScore(1);
        ss.setNegativeOperator("==");
        ss.setNegativeCriteria("NO_IMPROVED");
        ss.setNegativeScore(-1);
        ss.setMapToObject("AccessPoint");
        ss.setEffectiveStartDate(effectiveStartDate);
        ss.setEffectiveEndDate(effectiveEndDate);
        ss.setScoreBucketId(sbucketlist.get(0).getKey().getId());
        ssDao.save(ss);

        StandardScoring ssWaterAvail = new StandardScoring();
        ssWaterAvail.setGlobalStandard(true);
        ssWaterAvail.setDisplayName("Water Available Day of Visit");
        ssWaterAvail.setPointType("WATER_POINT");
        ssWaterAvail.setEvaluateField("WaterAvailableDayVisitFlag");
        ssWaterAvail.setPositiveOperator("==");
        ssWaterAvail.setPositiveCriteria("true");
        ssWaterAvail.setNegativeOperator("!=");
        ssWaterAvail.setNegativeCriteria("true");
        ssWaterAvail.setCriteriaType("Boolean");
        ssWaterAvail.setScoreBucketId(sbucketlist.get(0).getKey().getId());
        ssDao.save(ssWaterAvail);

        StandardScoring ss2 = new StandardScoring();
        ss2.setGlobalStandard(false);
        ss2.setCountryCode("LR");
        ss2.setDisplayName("Number of Users Per Water Point");
        ss2.setPointType("WATER_POINT");
        ss2.setEvaluateField("NumberOfHouseholdsUsingPoint");
        ss2.setPositiveOperator("<");
        ss2.setPositiveCriteria("100");
        ss2.setCriteriaType("Number");
        ss2.setPositiveScore(1);
        ss2.setNegativeOperator(">=");
        ss2.setNegativeCriteria("100");
        ss2.setNegativeScore(-1);
        ss2.setMapToObject("AccessPoint");
        ss2.setEffectiveStartDate(effectiveStartDate);
        ss2.setEffectiveEndDate(effectiveEndDate);
        ss2.setScoreBucketId(sbucketlist.get(0).getKey().getId());
        ssDao.save(ss2);

        StandardScoring ss4 = new StandardScoring();
        ss4.setGlobalStandard(false);
        ss4.setCountryCode("BO");
        ss4.setDisplayName("Number of Users Per Water Point");
        ss4.setPointType("WATER_POINT");
        ss4.setEvaluateField("NumberOfHouseholdsUsingPoint");
        ss4.setPositiveOperator("<");
        ss4.setPositiveCriteria("100");
        ss4.setCriteriaType("Number");
        ss4.setPositiveScore(1);
        ss4.setNegativeOperator(">=");
        ss4.setNegativeCriteria("100");
        ss4.setNegativeScore(-1);
        ss4.setMapToObject("AccessPoint");
        ss4.setEffectiveStartDate(effectiveStartDate);
        ss4.setEffectiveEndDate(effectiveEndDate);
        ss4.setScoreBucketId(sbucketlist.get(1).getKey().getId());
        ssDao.save(ss4);
    }

    public ArrayList<StandardScoreBucket> populateScoreBuckets() {
        ArrayList<StandardScoreBucket> scoreBucketsList = new ArrayList<StandardScoreBucket>();
        BaseDAO<StandardScoreBucket> scDao = new BaseDAO<StandardScoreBucket>(
                StandardScoreBucket.class);
        ArrayList<String> scoreBuckets = new ArrayList<String>();
        scoreBuckets.add("WATERPOINTLEVELOFSERVICE");
        scoreBuckets.add("WATERPOINTSUSTAINABILITY");
        scoreBuckets.add("PUBLICINSTITUTIONLEVELOFSERVICE");
        scoreBuckets.add("PUBLICINSTITUTIONSUSTAINABILITY");
        for (String item : scoreBuckets) {
            StandardScoreBucket sbucket = new StandardScoreBucket();
            sbucket.setName(item);
            scDao.save(sbucket);
            scoreBucketsList.add(sbucket);
        }
        return scoreBucketsList;
    }

}
