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

package org.waterforpeople.mapping.app.gwt.client.formdefinition;

import java.io.Serializable;
import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

@Deprecated
public class DataEntryFormDefinitionDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 897095339564552900L;
	private String sourceObject = null;
	private String name = null;
	public String getSourceObject() {
		return sourceObject;
	}
	public void setSourceObject(String sourceObject) {
		this.sourceObject = sourceObject;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<DataEntryFieldDefinitionDto> getFieldDefList() {
		return fieldDefList;
	}
	public void setFieldDefList(ArrayList<DataEntryFieldDefinitionDto> fieldDefList) {
		this.fieldDefList = fieldDefList;
	}
	private ArrayList<DataEntryFieldDefinitionDto> fieldDefList = null;

}
