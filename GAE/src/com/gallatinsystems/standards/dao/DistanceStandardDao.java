package com.gallatinsystems.standards.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping;
import com.gallatinsystems.standards.domain.Standard;

public class DistanceStandardDao extends BaseDAO<DistanceStandard> {
	
	public DistanceStandardDao(){
		super(DistanceStandard.class);
	}
	
	public DistanceStandard findDistanceStandard(Standard.StandardType type, String countryCode, AccessPoint.LocationType locationType){
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(DistanceStandard.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("standardType", filterString,paramString, "String", type.toString(), paramMap);
		appendNonNullParam("countryCode", filterString, paramString, "String", countryCode, paramMap);
		appendNonNullParam("locationType", filterString, paramString,"String", locationType, paramMap);
		
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		
		List<DistanceStandard> standardList = (List<DistanceStandard>) query
				.executeWithMap(paramMap);
		
		return standardList.get(0);
			
	}

}
