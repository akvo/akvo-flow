package com.gallatinsystems.standards.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping;

public class LOSScoreToStatusMappingDao extends BaseDAO<LOSScoreToStatusMapping> {
	public LOSScoreToStatusMappingDao(){
		super(LOSScoreToStatusMapping.class);
	}
	
	public LOSScoreToStatusMapping findByScoreAndType(){
		return null;
	}
}
