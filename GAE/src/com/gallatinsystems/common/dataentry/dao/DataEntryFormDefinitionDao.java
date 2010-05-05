package com.gallatinsystems.common.dataentry.dao;

import java.io.Serializable;

import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;
import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

public class DataEntryFormDefinitionDao extends BaseDAO implements Serializable {

	public DataEntryFormDefinitionDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1551563750038657148L;
	
	public DataEntryFormDefinition save(DataEntryFormDefinition item){
		
		return (DataEntryFormDefinition) super.save(item);
	}
	
	public DataEntryFormDefinition getByKey(Long id){
		BaseDAO<DataEntryFormDefinition> dao = new BaseDAO<DataEntryFormDefinition>(DataEntryFormDefinition.class);
		return dao.getByKey(id);
	}
	
	public DataEntryFormDefinition getByName(String name){
		return (DataEntryFormDefinition) super.findByProperty("name", name,
				"String");		
	}


}
