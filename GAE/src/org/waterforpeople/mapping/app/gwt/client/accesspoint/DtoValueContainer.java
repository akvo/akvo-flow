package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DtoValueContainer extends BaseDto {
	private static final long serialVersionUID = -5214719338768195921L;
	private ArrayList<Row> rows = new ArrayList<Row>();
	
	

	public void addRow(String fieldName, String fieldDisplayName,
			Integer order, String fieldType, String fieldValue) {
		Row row = new Row(fieldName, fieldDisplayName, order, fieldType,
				fieldValue);
		getRows().add(row);
	}



	public void setRows(ArrayList<Row> rows) {
		this.rows = rows;
	}



	public ArrayList<Row> getRows() {
		return rows;
	}
}
