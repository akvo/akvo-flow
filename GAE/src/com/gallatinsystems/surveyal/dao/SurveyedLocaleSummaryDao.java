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

package com.gallatinsystems.surveyal.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleSummary;

/**
 * Data access object for manipulating SurveyedLocales
 * 
 * @author Mark Westra
 * 
 */
public class SurveyedLocaleSummaryDao extends BaseDAO<SurveyedLocaleSummary> {

	public SurveyedLocaleSummaryDao() {
		super(SurveyedLocaleSummary.class);
	}
	
	/**
	 * gets a single summary object by surveyGroupId. If the number is not unique (this
	 * shouldn't happen), it returns the first instance found.
	 * 
	 * @param projectId
	 * @return
	 */
	public SurveyedLocaleSummary getBySurveyGroupId(Long surveyGroupId) {
		return super.findByProperty("surveyGroupId", surveyGroupId, "Long");
	}

}