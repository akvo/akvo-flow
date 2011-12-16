package com.gallatinsystems.editorial.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * 
 * represents a single row within a section of a map balloon. When evaluated the
 * variableFieldName will be used to look up the field within the object being
 * mapped and its value will be used when forming the final content.
 * 
 * @deprecated
 */
@PersistenceCapable
public class MapBalloonRowDefinition extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6248223990450178547L;

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
}
