package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyInstance {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private Long userID;
	
	@Persistent
	private Date collectionDate;
	
	@Persistent 
	private DeviceFiles deviceFile;
	
	@Persistent(mappedBy = "surveyInstance")
	private ArrayList<QuestionAnswerStore> questionAnswersStore;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public ArrayList<QuestionAnswerStore> getQuestionAnswersStore() {
		return questionAnswersStore;
	}

	public void setQuestionAnswersStore(
			ArrayList<QuestionAnswerStore> questionAnswersStore) {
		this.questionAnswersStore = questionAnswersStore;
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
}
