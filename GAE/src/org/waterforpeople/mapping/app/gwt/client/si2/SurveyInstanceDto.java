package org.waterforpeople.mapping.app.gwt.client.si2;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.Persistent;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class SurveyInstanceDto extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8484584703637564931L;
	@Persistent
	private Long userID;

	@Persistent
	private Date collectionDate;

	@Persistent
	private DeviceFiles deviceFile;

	@Persistent(mappedBy = "surveyInstance")
	private ArrayList<QuestionAnswerStoreDto> questionAnswersStore;

	private Long surveyId;

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

	public DeviceFiles getDeviceFile() {
		return deviceFile;
	}

	public void setDeviceFile(DeviceFiles deviceFile) {
		this.deviceFile = deviceFile;
	}

	public ArrayList<QuestionAnswerStoreDto> getQuestionAnswersStore() {
		return questionAnswersStore;
	}

	public void setQuestionAnswersStore(
			ArrayList<QuestionAnswerStoreDto> questionAnswersStore) {
		this.questionAnswersStore = questionAnswersStore;
	}
	public void addQuestionAnswerStore(QuestionAnswerStoreDto item){
		if(questionAnswersStore==null)
			questionAnswersStore = new ArrayList<QuestionAnswerStoreDto>();
		questionAnswersStore.add(item);
	}
}
