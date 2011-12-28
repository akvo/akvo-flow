package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

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
	
	private FlexTable getFlexTable(){
		return attributeTable;
	}
	
	private void addAttributeRow(String currentSelectedAttribute){
		Integer row = attributeTable.getRowCount();
		
		attributeTable.setWidget(row+1,ATTRIBUTE_ROW_ID_COL , new TextBox());
		//attributeTable.setWidget(row+1, column, widget) 
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
