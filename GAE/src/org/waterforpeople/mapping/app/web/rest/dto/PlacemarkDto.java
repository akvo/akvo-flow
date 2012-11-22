package org.waterforpeople.mapping.app.web.rest.dto;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class PlacemarkDto extends BaseDto {
	private static final long serialVersionUID = 7506078448656852101L;
	private String markType = null;
	private Double latitude = null;
	private Double longitude = null;
	private Date collectionDate = null;

	public String getMarkType() {
		return markType;
	}

	public void setMarkType(String markType) {
		this.markType = markType;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}
}