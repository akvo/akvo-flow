package com.gallatinsystems.common.data.spreadsheet.domain;

import java.util.ArrayList;

public class SpreadsheetContainer {
	private String spreadsheetName;
	private Integer RowContainerCount;
	private Integer colCount;
	private ArrayList<RowContainer> RowContainerList;

	public String getSpreadsheetName() {
		return spreadsheetName;
	}

	public void setSpreadsheetName(String spreadsheetName) {
		this.spreadsheetName = spreadsheetName;
	}

	public Integer getRowContainerCount() {
		return RowContainerCount;
	}

	public void setRowContainerCount(Integer RowContainerCount) {
		this.RowContainerCount = RowContainerCount;
	}

	public Integer getColCount() {
		return colCount;
	}

	public void setColCount(Integer colCount) {
		this.colCount = colCount;
	}

	public void setRowContainerList(ArrayList<RowContainer> RowContainerList) {
		this.RowContainerList = RowContainerList;
	}

	public ArrayList<RowContainer> getRowContainerList() {
		return RowContainerList;
	}
	
	public void addRowContainer(RowContainer RowContainer){
		if(RowContainerList==null){
			RowContainerList = new ArrayList<RowContainer>();
		}
		RowContainerList.add(RowContainer);
	}



	
}
