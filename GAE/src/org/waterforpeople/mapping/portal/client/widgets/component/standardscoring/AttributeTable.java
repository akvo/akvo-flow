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

package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class AttributeTable extends Composite implements ClickHandler {

	FlexTable attributeTable = new FlexTable();
	TreeMap<String,String> attributeMap = null;
	public static Integer ATTRIBUTE_ROW_ID_COL = 0;
	public static Integer ATTRIBUTE_AND_OR_COL = 1;
	public static Integer ATTRIBUTE_COL = 2;
	public static Integer ATTRIBUTE_TYPE_COL = 3;
	public static Integer OPERATOR_COL = 4;
	public static Integer VALUE_COL = 5;
	
	public AttributeTable(){
	}
		
	
	@Override
	public void onClick(ClickEvent event) {
		
	}
	
	public void bindAttributeList(String objectType){
		//Implement actual list call to service here
		if(attributeMap==null){
			attributeMap = new TreeMap<String,String>();
		}
		//temp
		attributeMap.put("att1", "Water available in last 30 days");
		attributeMap.put("att2", "Current Problems");
	}

}
