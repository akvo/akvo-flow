package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Survey {

	private String name;
	private String id;
	private Date startDate;
	private Date endDate;
	private List<QuestionGroup> questionGroups;

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

	public void addQuestionGroup(QuestionGroup group) {
		if (questionGroups == null) {
			questionGroups = new ArrayList<QuestionGroup>();
		}
		questionGroups.add(group);
	}
}
