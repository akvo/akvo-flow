package org.waterforpeople.mapping.dao;

import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;

import com.gallatinsystems.framework.dao.BaseDAO;

public class MappingSpreadsheetDefintionDAO extends
		BaseDAO<MappingSpreadsheetDefinition> {

	public MappingSpreadsheetDefintionDAO() {
		super(MappingSpreadsheetDefinition.class);
	}
	

	public MappingSpreadsheetDefinition save(MappingSpreadsheetDefinition obj) {
		MappingSpreadsheetDefinition mapDefExisting = findBySpreadsheetURL(obj.getSpreadsheetURL());
		if (mapDefExisting != null) {
			// merge objects
			obj.setKey(mapDefExisting.getKey());
		}
		return super.save(obj);
	}

	public MappingSpreadsheetDefinition findBySpreadsheetURL(
			String propertyValue) {
		return super.findByProperty("spreadsheetURL", propertyValue, "String");
	}

}
