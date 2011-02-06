package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;


public class MapBalloonRowDefinitionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8849968013347279532L;
	/**
	 * 
	 */
	private Long keyId = null;
	private String variableFieldName = null;
	private String objectName = null;
	private String objectAttribute = null;
	private String text = null;
	private String formatString = null;
	private Long mappingBalloonDefinitionId = null;
	private Integer displayOrder = null;
	private RowType rowType = null;
	private Long parentId = null;

	public enum RowType {
		HEADER, TITLE, DETAIL_LEFT, DETAIL_RIGHT, MAP_ICON,
	};

	public String getVariableFieldName() {
		return variableFieldName;
	}

	public void setVariableFieldName(String variableFieldName) {
		this.variableFieldName = variableFieldName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectAttribute() {
		return objectAttribute;
	}

	public void setObjectAttribute(String objectAttribute) {
		this.objectAttribute = objectAttribute;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFormatString() {
		return formatString;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	public Long getMappingBalloonDefinitionId() {
		return mappingBalloonDefinitionId;
	}

	public void setMappingBalloonDefinitionId(Long mappingBalloonDefinitionId) {
		this.mappingBalloonDefinitionId = mappingBalloonDefinitionId;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public void setRowType(RowType rowType) {
		this.rowType = rowType;
	}

	public RowType getRowType() {
		return rowType;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Long getKeyId() {
		return keyId;
	}
}
