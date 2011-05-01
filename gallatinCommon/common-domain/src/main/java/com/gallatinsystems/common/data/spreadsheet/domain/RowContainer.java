package com.gallatinsystems.common.data.spreadsheet.domain;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class RowContainer extends BaseDomain {

	private static final long serialVersionUID = -5434469059154017308L;

	private ArrayList<ColumnContainer> ColumnContainersList;

	public void setColumnContainersList(
			ArrayList<ColumnContainer> ColumnContainersList) {
		this.ColumnContainersList = ColumnContainersList;
	}

	public ArrayList<ColumnContainer> getColumnContainersList() {
		return ColumnContainersList;
	}

	public void addColumnContainer(ColumnContainer col) {
		if (ColumnContainersList == null) {
			ColumnContainersList = new ArrayList<ColumnContainer>();
		}
		ColumnContainersList.add(col);
	}

}
