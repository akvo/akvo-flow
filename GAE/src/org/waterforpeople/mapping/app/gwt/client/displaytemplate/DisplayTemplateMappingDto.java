package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

public class DisplayTemplateMappingDto extends BaseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8172849878858010289L;
	/**
	 * The idea is that each object hold what is a row in the current velocity
	 * templates
	 */

	private String languageCode = null;
	private String rowDescription = null;
	private String attributeName = null;
	private String attributeFormattingInstructions = null;
	private Integer displayOrder = null;
	private String accessPointType = null;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getRowDescription() {
		return rowDescription;
	}

	public void setRowDescription(String rowDescription) {
		this.rowDescription = rowDescription;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeFormattingInstructions() {
		return attributeFormattingInstructions;
	}

	public void setAttributeFormattingInstructions(
			String attributeFormattingInstructions) {
		this.attributeFormattingInstructions = attributeFormattingInstructions;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getAccessPointType() {
		return accessPointType;
	}

	public void setAccessPointType(String accessPointType) {
		this.accessPointType = accessPointType;
	}

}
