package org.waterforpeople.mapping.app.web.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.StandardScoring;

public class StandardScoringTest {
	
	public void populateData(){
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
		ss.setScoreBucket("LevelOfService");
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
		ssDao.save(ssWaterAvail);
		
		
		StandardScoring ss2 = new StandardScoring();
		ss2.setGlobalStandard(false);
		ss2.setCountryCode("LR");
		ss2.setDisplayName("Number of Users Per Water Point");
		ss2.setPointType("WATER_POINT");
		ss2.setEvaluateField("NumberOfHouseholdsUsingPoint");
		ss2.setPositiveOperator("<");
		ss2.setPositiveCriteria("100");
		ss2.setCriteriaType("Integer");
		ss2.setPositiveScore(1);
		ss2.setNegativeOperator(">=");
		ss2.setNegativeCriteria("100");
		ss2.setNegativeScore(-1);
		ss2.setMapToObject("AccessPoint");
		ss2.setEffectiveStartDate(effectiveStartDate);
		ss2.setEffectiveEndDate(effectiveEndDate);
		ss2.setScoreBucket("LevelOfService");
		ssDao.save(ss2);
	}

}
