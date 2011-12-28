package com.gallatinsystems.standards.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.google.appengine.api.datastore.Key;

public class LevelOfServiceScoreDao extends BaseDAO<LevelOfServiceScore> {
	public LevelOfServiceScoreDao() {
		super(LevelOfServiceScore.class);
	}
	
	public List<LevelOfServiceScore> listByAccessPoint(Key accessPointKey){
		return super.listByProperty("objectKey", accessPointKey, "com.google.appengine.api.datastore.Key");
	}
	
}
