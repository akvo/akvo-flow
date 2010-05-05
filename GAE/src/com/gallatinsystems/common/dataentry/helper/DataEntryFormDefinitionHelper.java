package com.gallatinsystems.common.dataentry.helper;

import com.gallatinsystems.common.dataentry.dao.DataEntryFormDefinitionDao;
import com.gallatinsystems.common.dataentry.domain.DataEntryFieldDefinition;
import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;

public class DataEntryFormDefinitionHelper {
	
	public DataEntryFormDefinition save(DataEntryFormDefinition item){
		DataEntryFormDefinitionDao dao = new DataEntryFormDefinitionDao(DataEntryFieldDefinition.class);
		return dao.save(item);
	}
	
	public DataEntryFormDefinition get(Long id){
		DataEntryFormDefinitionDao dao = new DataEntryFormDefinitionDao(DataEntryFieldDefinition.class);
		return dao.getByKey(id);
	}
	
	public DataEntryFormDefinition getByName(String name){
		DataEntryFormDefinitionDao dao = new DataEntryFormDefinitionDao(DataEntryFieldDefinition.class);
		return dao.getByName(name);
	}

}
