package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * domain object for Surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class Survey {

	private String name;
	private String id;
	private Date startDate;
	private Date endDate;
	private List<QuestionGroup> questionGroups;
	private double version;
	private String type;
	private String location;
	private String fileName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public String getName() {
		return name;

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<QuestionGroup> getQuestionGroups() {
		return questionGroups;
	}

	public void setQuestionGroups(List<QuestionGroup> questionGroups) {
		this.questionGroups = questionGroups;
	}

	/**
	 * adds a new quesitonGroup to the survey at the end of the questionGroup
	 * list. If the questionGroup list is null, it is initialized before adding.
	 * 
	 * @param group
	 */
	public void addQuestionGroup(QuestionGroup group) {
		if (questionGroups == null) {
			questionGroups = new ArrayList<QuestionGroup>();
		}
		questionGroups.add(group);
	}
}
