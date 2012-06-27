/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
