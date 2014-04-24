/*
 *  Copyright (C) 2013-2014 Stichting Akvo (Akvo Foundation)
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
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;

/**
 * Data access object for manipulating SurveyedLocalesClusters
 * 
 */
public class SurveyedLocaleClusterDao extends BaseDAO<SurveyedLocaleCluster> {

	public SurveyedLocaleClusterDao() {
		super(SurveyedLocaleCluster.class);
	}

	/**
	 * returns a single localeCluster that correspond to the geocell passed in
	 * 
	 * @param geocell
	 * @param showOnPublicMap 
	 * @return
	 */	
	@SuppressWarnings("unchecked")
	public SurveyedLocaleCluster getExistingCluster(String geocell, Boolean showOnPublicMap) {
		PersistenceManager pm = PersistenceFilter.getManager();
		String queryString = "clusterGeocell == :p1 && showOnPublicMap == :p2";
		javax.jdo.Query query = pm.newQuery(SurveyedLocaleCluster.class,queryString);
		List<SurveyedLocaleCluster> result = (List<SurveyedLocaleCluster>) query.execute(geocell, showOnPublicMap);

		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	/**
	 * lists all localeClusters that lie within the geocells and on the level passed in
	 * Level should be 2, 3, 4 or 5.
	 * 
	 * @param geocells
	 * @param gcLevel
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List<SurveyedLocaleCluster> listLocaleClustersByGeocell(List<String> geocells, Integer gcLevel) {
		PersistenceManager pm = PersistenceFilter.getManager();
		String queryString = ":p1.contains(geocells) && level == :p2";		
		javax.jdo.Query query = pm.newQuery(SurveyedLocaleCluster.class,queryString);
		List<SurveyedLocaleCluster> results = (List<SurveyedLocaleCluster>) query.execute(geocells,gcLevel);
		return results;
	}	

	/**
	 * lists all localeClusters that lie within the geocells and on the level passed in
	 * and that have showOnPublicMap = true
	 * Level should be 2, 3, 4 or 5.
	 * 
	 * @param geocells
	 * @param gcLevel
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List<SurveyedLocaleCluster> listPublicLocaleClustersByGeocell(List<String> geocells, Integer gcLevel) {
		PersistenceManager pm = PersistenceFilter.getManager();
		String queryString = ":p1.contains(geocells) && level == :p2 && showOnPublicMap == true";
		javax.jdo.Query query = pm.newQuery(SurveyedLocaleCluster.class,queryString);
		List<SurveyedLocaleCluster> results = (List<SurveyedLocaleCluster>) query.execute(geocells,gcLevel);
		return results;
	}
}
