package com.gallatinsystems.standards.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

public class LevelOfServiceScoreDao extends BaseDAO<LevelOfServiceScore> {
	public LevelOfServiceScoreDao() {
		super(LevelOfServiceScore.class);
	}
	
	public List<LevelOfServiceScore> listByAccessPoint(Key accessPointKey){
		return super.listByProperty("objectKey", accessPointKey, "com.google.appengine.api.datastore.Key");
	}
	
	@SuppressWarnings("unchecked")
	public LevelOfServiceScore findByAccessPoint(Key accessPointKey, StandardType standardType){
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(LevelOfServiceScore.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("scoreType", filterString,paramString, "String", standardType.toString(), paramMap);
		appendNonNullParam("objectKey", filterString, paramString, "com.google.appengine.api.datastore.Key", accessPointKey, paramMap);
		
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		
		List<LevelOfServiceScore> standardList = (List<LevelOfServiceScore>) query
				.executeWithMap(paramMap);
		
		return standardList.get(0);
	}
	
}
