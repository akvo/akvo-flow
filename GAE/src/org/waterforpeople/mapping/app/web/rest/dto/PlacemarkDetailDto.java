package org.waterforpeople.mapping.app.web.rest.dto;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class PlacemarkDetailDto extends BaseDto {

	private static final long serialVersionUID = -1118635482884407223L;
	private Date collectionDate;
	private String questionText;
	private String metricName;
	private String stringValue;
	private Long placemarkId;

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Long getPlacemarkId() {
		return placemarkId;
	}

	public void setPlacemarkId(Long placemarkId) {
		this.placemarkId = placemarkId;
	}
}
