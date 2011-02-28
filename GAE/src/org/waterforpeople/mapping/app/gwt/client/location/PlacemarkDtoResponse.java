package org.waterforpeople.mapping.app.gwt.client.location;

import java.io.Serializable;
import java.util.List;


public class PlacemarkDtoResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2442244967159768511L;
	
	private List<PlacemarkDto> dtoList = null;
	private String cursor = null;
	public List<PlacemarkDto> getDtoList() {
		return dtoList;
	}
	public void setDtoList(List<PlacemarkDto> dtoList) {
		this.dtoList = dtoList;
	}
	public String getCursor() {
		return cursor;
	}
	public void setCursor(String cursor) {
		this.cursor = cursor;
	}
}
