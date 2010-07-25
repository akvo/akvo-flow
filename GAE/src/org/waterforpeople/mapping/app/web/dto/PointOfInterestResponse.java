package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.location.PointOfInterestDto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * response for point of interest service
 * 
 * @author Christopher Fagiani
 * 
 */
public class PointOfInterestResponse extends RestResponse {
	private static final long serialVersionUID = 1548249617327473969L;
	private List<PointOfInterestDto> pointsOfInterest;
	private String cursor;

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public List<PointOfInterestDto> getPointsOfInterest() {
		return pointsOfInterest;
	}

	public void setPointsOfInterest(List<PointOfInterestDto> pointsOfInterest) {
		this.pointsOfInterest = pointsOfInterest;
	}
}
