package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;

public class Row implements Serializable {
	
	private static final long serialVersionUID = 7286747363278243558L;
	private String fieldName = null;
	private String fieldDisplayName = null;
	private Integer order = null;
	private String fieldType = null;
	private String fieldValue = null;

	public Row(String fieldName, String fieldDisplayName, Integer order,
			String fieldType, String fieldValue) {
		this.fieldName = fieldName;
		this.fieldDisplayName = fieldDisplayName;
		this.order = order;
		this.fieldType = fieldType;
		this.fieldValue = fieldValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
