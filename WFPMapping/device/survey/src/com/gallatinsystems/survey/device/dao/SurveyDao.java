package com.gallatinsystems.survey.device.dao;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.xml.SaxSurveyParser;

/**
 * utility to read survey xml files from wherever they may reside
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDao {

	/**
	 * loads a survey xml depending on the survey location type in the survey
	 * object passed in. It will look at either the sd card or the resource
	 * bundle within the apk
	 */
	public static Survey loadSurvey(Survey survey, InputStream in)
			throws FileNotFoundException {
		if (survey != null) {
			String tempName = survey.getName();
			SaxSurveyParser parser = new SaxSurveyParser();
			survey = parser.parse(in);
			
			if (survey != null
					&& (survey.getName() == null || survey.getName().trim()
							.length() == 0)) {
				survey.setName(tempName);
			}
		}
		return survey;
	}
}
