package com.gallatinsystems.common.data.spreadsheet.dao;

import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.gallatinsystems.framework.dao.BaseDAO;

public class SpreadsheetDao extends BaseDAO<SpreadsheetContainer> {

	public SpreadsheetDao() {
		super(SpreadsheetContainer.class);
	}

	public SpreadsheetContainer save(SpreadsheetContainer sheet) {
		SpreadsheetContainer oldContainer = findByName(sheet
				.getSpreadsheetName());
		if (oldContainer != null) {
			delete(oldContainer);
		}
		return super.save(sheet);
	}

	public SpreadsheetContainer findByName(String name) {
		return findByProperty("spreadsheetName", name, "String");
	}
}
