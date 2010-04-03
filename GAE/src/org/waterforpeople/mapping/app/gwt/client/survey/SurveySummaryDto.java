package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.Date;

/**
 * transfer object for various types of survey summarizations. Instances will
 * most likely be partially populated based on the type of summarization
 * queried.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySummaryDto implements Serializable {

	private static final long serialVersionUID = -1966405747413629647L;
	private String questionId;
	private String responseText;
	private Long count;
	private String countryCode;
	private String communityCode;
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}