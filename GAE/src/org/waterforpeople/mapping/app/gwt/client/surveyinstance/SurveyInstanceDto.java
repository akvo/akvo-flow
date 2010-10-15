package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;


public class SurveyInstanceDto extends BaseDto {

	private static final long serialVersionUID = 8484584703637564931L;

	private Long userID;

	private Date collectionDate;

	private List<QuestionAnswerStoreDto> questionAnswersStore;

	private Long surveyId;

	private String submitterName;
	private String deviceIdentifier;

	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public List<QuestionAnswerStoreDto> getQuestionAnswersStore() {
		return questionAnswersStore;
	}

	public void setQuestionAnswersStore(
			List<QuestionAnswerStoreDto> questionAnswersStore) {
		this.questionAnswersStore = questionAnswersStore;
	}

	public void addQuestionAnswerStore(QuestionAnswerStoreDto item) {
		if (questionAnswersStore == null)
			questionAnswersStore = new ArrayList<QuestionAnswerStoreDto>();
		questionAnswersStore.add(item);
	}
}
