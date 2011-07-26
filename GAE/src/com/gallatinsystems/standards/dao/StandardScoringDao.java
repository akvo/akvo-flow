package com.gallatinsystems.standards.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.StandardScoring;

public class StandardScoringDao extends BaseDAO<StandardScoring> {

	public StandardScoringDao() {
		super(StandardScoring.class);
	}

	public List<StandardScoring> listStandardScoring(AccessPoint ap) {
		List<StandardScoring> ssList = new ArrayList<StandardScoring>();
		ssList = super.listByProperty("pointType", ap.getPointType().toString(), "String");
		return ssList;
	}

	public List<StandardScoring> listStandardScoringByBucketForAccessPoint(
			Long scoreBucketId, AccessPoint ap) {
		List<StandardScoring> supersetList = new ArrayList<StandardScoring>();

		List<StandardScoring> globalList = listGlobalStandardScoringForAccessPoint(
				scoreBucketId, ap);
		List<StandardScoring> localList = listLocalStandardScoringForAccessPoint(scoreBucketId, ap);
		Collections.copy(supersetList, globalList);
		Collections.copy(supersetList, localList);
		return supersetList;
	}

	public List<StandardScoring> listStandardScoring(Long scoreBucketId) {
		List<StandardScoring> ssList = new ArrayList<StandardScoring>();
		ssList = super.listByProperty("scoreBucketId", scoreBucketId, "Long");
		return ssList;
	}

	public List<StandardScoring> listGlobalStandardScoringForAccessPoint(
			Long scoreBucketId, AccessPoint ap) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("pointType", filterString, paramString, "String",
				ap.getPointType(), paramMap);
		appendNonNullParam("scoreBucketId", filterString, paramString, "Long",
				scoreBucketId, paramMap);
		appendNonNullParam("mapToObject", filterString, paramString, "String",
				"AccessPoint", paramMap);
		appendNonNullParam("scopeType", filterString, paramString, "String",
				"GLOBAL", paramMap);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<StandardScoring> results = (List<StandardScoring>) query
				.executeWithMap(paramMap);
		return results;
	}
	
	public List<StandardScoring> listLocalDistanceStandardScoringForAccessPoint(
			AccessPoint ap) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("pointType", filterString, paramString, "String",
				ap.getPointType(), paramMap);
		appendNonNullParam("mapToObject", filterString, paramString, "String",
				"AccessPoint", paramMap);
		appendNonNullParam("criteriaType", filterString, paramString, "String",
				"Distance", paramMap);
		appendNonNullParam("countryCode", filterString, paramString, "String",
				ap.getCountryCode(), paramMap);
//		ToDo: need to think about how to use the subvalue	
//		appendNonNullParam("subValue", filterString, paramString, "String",
//				ap.getSub1(), paramMap);

		
		
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<StandardScoring> results = (List<StandardScoring>) query
				.executeWithMap(paramMap);
		return results;
	}

	public List<StandardScoring> listLocalStandardScoringForAccessPoint(
			Long scoreBucketId, AccessPoint ap) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(AccessPoint.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("pointType", filterString, paramString, "String",
				ap.getPointType(), paramMap);
		appendNonNullParam("scoreBucketId", filterString, paramString, "Long",
				scoreBucketId, paramMap);
		appendNonNullParam("mapToObject", filterString, paramString, "String",
				"AccessPoint", paramMap);
		appendNonNullParam("scopeType", filterString, paramString, "String",
				"LOCAL", paramMap);
		appendNonNullParam("countryCode", filterString, paramString, "String",
				ap.getCountryCode(), paramMap);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<StandardScoring> results = (List<StandardScoring>) query
				.executeWithMap(paramMap);
		return results;
	}

}
