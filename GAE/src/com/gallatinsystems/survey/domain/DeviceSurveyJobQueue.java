package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceSurveyJobQueue {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDevicePhoneNumber() {
		return devicePhoneNumber;
	}
	public void setDevicePhoneNumber(String devicePhoneNumber) {
		this.devicePhoneNumber = devicePhoneNumber;
	}
	public Long getSurveyID() {
		return surveyID;
	}
	public void setSurveyID(Long surveyID) {
		this.surveyID = surveyID;
	}
	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}
	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}
	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}
	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}
	public String getSurveyDistrobutionStatus() {
		return surveyDistrobutionStatus;
	}
	public void setSurveyDistrobutionStatus(String surveyDistrobutionStatus) {
		this.surveyDistrobutionStatus = surveyDistrobutionStatus;
	}
	private String devicePhoneNumber;
	private Long surveyID;
	private Date effectiveStartDate;
	private Date effectiveEndDate;
	private String surveyDistrobutionStatus;
	
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
