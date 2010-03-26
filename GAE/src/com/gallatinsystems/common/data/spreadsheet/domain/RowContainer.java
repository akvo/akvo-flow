package com.gallatinsystems.common.data.spreadsheet.domain;

import java.util.ArrayList;

public class RowContainer {
	
		private ArrayList<ColumnContainer> ColumnContainersList;

		public void setColumnContainersList(ArrayList<ColumnContainer> ColumnContainersList) {
			this.ColumnContainersList = ColumnContainersList;
		}

		public ArrayList<ColumnContainer> getColumnContainersList() {
			return ColumnContainersList;
		}
		
		public void addColumnContainer(ColumnContainer col){
			if(ColumnContainersList==null){
				ColumnContainersList = new ArrayList<ColumnContainer>();
			}
			ColumnContainersList.add(col);
		}

	
}
