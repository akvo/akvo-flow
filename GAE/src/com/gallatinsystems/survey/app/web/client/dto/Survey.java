package com.gallatinsystems.survey.app.web.client.dto;

import java.util.ArrayList;
public class Survey {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8572907583563124756L;
	private String name;
	private Integer version;
	private String description;
	private ArrayList<QuestionGroup> questionGroupList;
	
	public void addQuestionGroup(QuestionGroup questionGroup){
		if(questionGroupList==null){
			questionGroupList = new ArrayList<QuestionGroup>();
		}
		questionGroupList.add(questionGroup);
	}
	
	public ArrayList<QuestionGroup> getQuestionGroupList() {
		return questionGroupList;
	}
	public void setQuestionGroupList(ArrayList<QuestionGroup> questionGroupList) {
		this.questionGroupList = questionGroupList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
