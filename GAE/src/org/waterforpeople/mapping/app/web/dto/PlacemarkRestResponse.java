package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;

import com.gallatinsystems.framework.rest.RestResponse;

public class PlacemarkRestResponse extends RestResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2200987559778306217L;
	private List<PlacemarkDto> placemarks;
	
	private String cursor;

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public void setPlacemarks(List<PlacemarkDto> placemarks) {
		this.placemarks = placemarks;
	}

	public List<PlacemarkDto> getPlacemarks() {
		return placemarks;
	}
	
}
