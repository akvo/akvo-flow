package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * encapsulates responses to requests for survey data
 * 
 * @author Christopher Fagiani
 * 
 */
public class SummaryDataResponse extends RestResponse {

	private static final long serialVersionUID = -1314828732205727971L;
	private List<? extends BaseDto> dtoList;

	public List<? extends BaseDto> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<? extends BaseDto> dtoList) {
		this.dtoList = dtoList;
	}

}
