package com.gallatinsystems.standards.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping;
import com.gallatinsystems.standards.domain.Standard.StandardType;

public class LOSScoreToStatusMappingDao extends
		BaseDAO<LOSScoreToStatusMapping> {
	public LOSScoreToStatusMappingDao() {
		super(LOSScoreToStatusMapping.class);
	}

	public List<LOSScoreToStatusMapping> ListByLevelOfServiceScoreType(
			StandardType type) {
		// List mappings by LevelOfServiceScoreType
		return super.listByProperty("levelOfServiceScoreType", type, "String");
	}

	public LOSScoreToStatusMapping findByLOSScoreTypeAndScore(
			StandardType type,Integer score) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(LOSScoreToStatusMapping.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("levelOfServiceScoreType", filterString,
				paramString, "String", type.toString(), paramMap);
		super.appendNonNullParam("floor",filterString, paramString, "Integer", score, paramMap,
				LTE_OP);
//		super.appendNonNullParam("ceiling",filterString, paramString, "Integer", score, paramMap,
//				LTE_OP);
		

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		query.setOrdering("floor asc");

		List<LOSScoreToStatusMapping> standardList = (List<LOSScoreToStatusMapping>) query
				.executeWithMap(paramMap);
		LOSScoreToStatusMapping currentItem = null;
		if (standardList != null){
			for(LOSScoreToStatusMapping item: standardList){
				if(item.getCeiling()>=score && currentItem==null){
					currentItem = item;
				}else if(item.getCeiling()>=score && item.getCeiling()<currentItem.getCeiling()){
					currentItem = item;
				}
			}
			return currentItem;
		}
		
		return null;
	}
}
