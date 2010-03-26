package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MappingSpreadsheetColumnToAttribute extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2232937366226902644L;

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
