package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceFileRestResponse extends RestResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -621856145150280312L;

	private String cursor = null;

	private List<? extends BaseDto> dtoList;

	public List<? extends BaseDto> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<? extends BaseDto> dtoList) {
		this.dtoList = dtoList;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public String getCursor() {
		return cursor;
	}

}
