package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * response class for the survey rest servlet. This response object can contain
 * a list of objects that are somehow related to the survey tree. The list will
 * always be homogeneous but the actual type of the objects in the list depend on
 * the method called.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyRestResponse extends RestResponse {

	private static final long serialVersionUID = -3851323551471422767L;
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
