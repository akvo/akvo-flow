package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import com.gallatinsystems.framework.rest.RestResponse;

public class OGRFeatureRestResponse extends RestResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8152034309500974004L;
	private List<OGRFeatureDto> ogrFeatures;

	private String cursor;

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public void setOgrFeatures(List<OGRFeatureDto> ogrFeatures) {
		this.ogrFeatures = ogrFeatures;
	}

	public List<OGRFeatureDto> getOgrFeatures() {
		return ogrFeatures;
	}


}
