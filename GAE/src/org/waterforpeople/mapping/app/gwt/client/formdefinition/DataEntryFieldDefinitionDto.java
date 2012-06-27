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

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DataEntryFieldDefinitionDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1093541478235011915L;

	public enum DISPLAY_FIELD_TYPE {
		DROPDOWNLIST, CALENDAR, TEXTBOX, IMAGE
	}

	private DISPLAY_FIELD_TYPE fieldType = null;

	private String fieldName = null;
	private String fieldLabel = null;
	private String regexValidationMask = null;
	private String fieldDataType = null;
	private Integer displayOrder = null;
	private Integer fieldWidth = null;
	private Integer fieldHeight = null;
	private Boolean scrollable = null;
	private String bindingSourceObject = null;
	private String bindingAttributeName = null;

	public DISPLAY_FIELD_TYPE getFieldType() {
		return fieldType;
	}

	public void setFieldType(DISPLAY_FIELD_TYPE fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public String getRegexValidationMask() {
		return regexValidationMask;
	}

	public void setRegexValidationMask(String regexValidationMask) {
		this.regexValidationMask = regexValidationMask;
	}

	public String getFieldDataType() {
		return fieldDataType;
	}

	public void setFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(Integer fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public Integer getFieldHeight() {
		return fieldHeight;
	}

	public void setFieldHeight(Integer fieldHeight) {
		this.fieldHeight = fieldHeight;
	}

	public Boolean getScrollable() {
		return scrollable;
	}

	public void setScrollable(Boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setBindingSourceObject(String bindingSourceObject) {
		this.bindingSourceObject = bindingSourceObject;
	}

	public String getBindingSourceObject() {
		return bindingSourceObject;
	}

	public void setBindingAttributeName(String bindingAttributeName) {
		this.bindingAttributeName = bindingAttributeName;
	}

	public String getBindingAttributeName() {
		return bindingAttributeName;
	}
}
