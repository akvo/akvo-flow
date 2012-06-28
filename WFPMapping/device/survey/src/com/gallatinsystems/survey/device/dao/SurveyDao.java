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
