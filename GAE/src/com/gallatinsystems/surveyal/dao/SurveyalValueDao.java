/*  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.surveyal.domain.SurveyalValue;


/**
 * Data access object for manipulating SurveyalValues
 * 
 */
public class SurveyalValueDao extends BaseDAO<SurveyalValue> {

	public SurveyalValueDao() {
		super(SurveyalValue.class);
	}
	
	/**
	 * lists all surveyalValues
	 * 
	 * @param cursor
	 * @param pagesize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyalValue> listAll(String cursor, Integer pageSize) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyalValue.class);
		prepareCursor(cursor, pageSize, query);
		List<SurveyalValue> results = (List<SurveyalValue>) query
				.execute();
		return results;
	}
}