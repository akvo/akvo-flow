package org.waterforpeople.mapping.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

public class DisplayTemplateMapping extends BaseDomain{

	/**
	 *  The idea is that each object hold what is a row in the current velocity templates
	 */
	
	private static final long serialVersionUID = -6938123285373225024L;
	private String languageCode = null;
	private String rowDescription = null;
	private String attributeName = null;
	private String attributeFormattingInstructions = null;
	private Integer displayOrder = null;
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
}
