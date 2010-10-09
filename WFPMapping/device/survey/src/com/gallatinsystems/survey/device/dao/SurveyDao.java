package com.gallatinsystems.survey.device.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.res.Resources;

import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.xml.SaxSurveyParser;

/**
 * utility to read survey xml files from wherever they may reside
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDao {

	private static final String RESOURCE_PACKAGE = "com.gallatinsystems.survey.device";
	private static final String RAW_RESOURCE = "raw";

	/**
	 * loads a survey xml depending on the survey location type in the survey
	 * object passed in. It will look at either the sd card or the resource
	 * bundle within the apk
	 */
	public static Survey loadSurvey(Survey survey, Resources res)
			throws FileNotFoundException {
		if (survey != null) {
			String tempName = survey.getName();
			SaxSurveyParser parser = new SaxSurveyParser();
			if (ConstantUtil.RESOURCE_LOCATION.equalsIgnoreCase(survey
					.getLocation())) {
				// load from resource
				survey = parser.parse(res.openRawResource(res.getIdentifier(
						survey.getFileName(), RAW_RESOURCE, RESOURCE_PACKAGE)));
			} else {
				// load from file
				survey = parser.parse(new FileInputStream(ConstantUtil.DATA_DIR
						+ survey.getFileName()));

			}
			if (survey != null
					&& (survey.getName() == null || survey.getName().trim()
							.length() == 0)) {
				survey.setName(tempName);
			}
		}
		return survey;
	}

	/**
	 * loads a survey from the file passed in
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Survey loadSurveyFromFile(String file)
			throws FileNotFoundException {
		SaxSurveyParser parser = new SaxSurveyParser();
		Survey survey = parser.parse(new FileInputStream(ConstantUtil.DATA_DIR
				+ file));
		return survey;
	}
}
