package com.gallatinsystems.common.dataentry.dao;

import java.io.Serializable;

import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

public class DataEntryFormDefinitionDao extends
		BaseDAO<DataEntryFormDefinition> implements Serializable {

	private static final long serialVersionUID = -1551563750038657148L;

	public DataEntryFormDefinitionDao() {
		super(DataEntryFormDefinition.class);
	}

	public DataEntryFormDefinition getByName(String name) {
		return findByProperty("name", name, "String");
	}

}
