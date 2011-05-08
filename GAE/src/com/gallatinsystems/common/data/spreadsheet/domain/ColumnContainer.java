package com.gallatinsystems.common.data.spreadsheet.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class ColumnContainer extends BaseDomain {

	private static final long serialVersionUID = -2474877330894380548L;
	private String colName;
	private String colContents;

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColContents() {
		return colContents;
	}

	public void setColContents(String colContents) {
		this.colContents = colContents;
	}

}
