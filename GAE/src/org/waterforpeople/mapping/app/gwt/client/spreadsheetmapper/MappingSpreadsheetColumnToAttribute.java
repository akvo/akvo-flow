package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.io.Serializable;

public class MappingSpreadsheetColumnToAttribute implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2232937366226902644L;
	private Long keyId;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	private String spreadsheetColumn;
	private String objectAttribute;
	private String formattingRule;

	public String getSpreadsheetColumn() {
		return spreadsheetColumn;
	}

	public void setSpreadsheetColumn(String spreadsheetColumn) {
		this.spreadsheetColumn = spreadsheetColumn;
	}

	public String getObjectAttribute() {
		return objectAttribute;
	}

	public void setObjectAttribute(String objectAttribute) {
		this.objectAttribute = objectAttribute;
	}

	public String getFormattingRule() {
		return formattingRule;
	}

	public void setFormattingRule(String formattingRule) {
		this.formattingRule = formattingRule;
	}

}
