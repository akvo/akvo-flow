package com.gallatinsystems.standards.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.standards.domain.CompoundStandard;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.Standard.StandardType;

public class StandardDao extends BaseDAO<Standard> {
	public StandardDao(){
		super(Standard.class);
	}
	
	
	public List<Standard> listByAccessPointType(AccessPointType accessPointType){
		return super.listByProperty("accessPointType", accessPointType, "String");
	}
	
	public List<Standard> listByAccessPointTypeAndStandardType(AccessPointType accessPointType, StandardType standardType){
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Standard.class);
		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("accessPointType", filterString, paramString, "String", accessPointType,
				paramMap);
		appendNonNullParam("standardType", filterString, paramString,
				"String", standardType, paramMap);
		//appendNonNullParam("partOfCompoundRule", filterString, paramString, "Boolean", false, paramMap);
		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<Standard> standardList = (List<Standard>) query
				.executeWithMap(paramMap);
		return standardList;
	}
	
	private void delete(Long id){
		Standard standard = this.getByKey(id);
		if(standard!=null){
			if(standard.getPartOfCompoundRule()){
				CompoundStandardDao csDao = new CompoundStandardDao();
				List<CompoundStandard> csList = csDao.listByChildStandard(id);
				for(CompoundStandard csItem : csList){
					csDao.delete(csItem);
				}
				super.delete(standard);
			}
		}
	}
}
