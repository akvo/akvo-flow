package org.waterforpeople.mapping.app.gwt.client.formdefinition;

import java.io.Serializable;
import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

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
