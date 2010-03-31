package org.waterforpeople.mapping.dao;

import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;

public class MappingSpreadsheetDefintionDAO extends BaseDAO<MappingSpreadsheetDefinition> {

	public MappingSpreadsheetDefintionDAO(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public <E extends BaseDomain> E save(E obj) {
		MappingSpreadsheetDefinition mapDefExisting = findBySpreadsheetURL(((MappingSpreadsheetDefinition)obj).getSpreadsheetURL());
		if(mapDefExisting!=null){
			//merge objects
			((MappingSpreadsheetDefinition)obj).setKey(mapDefExisting.getKey());
			
		}
		return super.save(obj);
	}
	
	public MappingSpreadsheetDefinition findBySpreadsheetURL(String propertyValue){
		return super.findByProperty("spreadsheetURL", propertyValue, "String");
	}
	

}
