package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyInstance extends BaseDomain {

	private static final long serialVersionUID = 5840846001731305734L;

	@Persistent
	private Long userID;

	@Persistent
	private Date collectionDate;
	private Long deviceFileId;

	@NotPersistent
	private DeviceFiles deviceFile;

	@NotPersistent
	private ArrayList<QuestionAnswerStore> questionAnswersStore;

	private Long surveyId;

	private String deviceIdentifier;
	private String submitterName;
	private String approvedFlag;

	public String getApprovedFlag() {
		return approvedFlag;
	}

	public void setApprovedFlag(String approvedFlag) {
		this.approvedFlag = approvedFlag;
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

	public DeviceFiles getDeviceFile() {
		return deviceFile;
	}

	public void setDeviceFile(DeviceFiles deviceFile) {
		this.deviceFile = deviceFile;
		if (deviceFile.getKey() != null)
			deviceFileId = deviceFile.getKey().getId();
	}

	public ArrayList<QuestionAnswerStore> getQuestionAnswersStore() {
		return questionAnswersStore;
	}

	public void setQuestionAnswersStore(
			ArrayList<QuestionAnswerStore> questionAnswersStore) {
		this.questionAnswersStore = questionAnswersStore;
	}

	public void setSubmitterName(String name) {
		submitterName = name;
	}

	public String getSubmitterName() {
		return submitterName;
	}

	public void setDeviceIdentifier(String id) {
		deviceIdentifier = id;
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	public void setDeviceFileId(Long deviceFileId) {
		this.deviceFileId = deviceFileId;
	}

	public Long getDeviceFileId() {
		return deviceFileId;
	}
}
