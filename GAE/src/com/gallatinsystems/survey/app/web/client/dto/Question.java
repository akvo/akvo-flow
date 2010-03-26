package com.gallatinsystems.survey.app.web.client.dto;

import java.util.ArrayList;

public class Question {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4708385830894435407L;

	private String text;

	private QuestionType type;
	private ArrayList<QuestionOption> optionsList;
	private ArrayList<QuestionHelp> questionHelpList;

	public void addOption(QuestionOption questionOption) {
		if (optionsList == null) {
			optionsList = new ArrayList<QuestionOption>();
		}
		optionsList.add(questionOption);
	}

	public void addQuestionHelp(QuestionHelp questionHelp) {
		if (questionHelpList == null) {
			questionHelpList = new ArrayList<QuestionHelp>();
		}
		questionHelpList.add(questionHelp);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public QuestionType getType() {
		return type;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public ArrayList<QuestionOption> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(ArrayList<QuestionOption> optionsList) {
		this.optionsList = optionsList;
	}

	public ArrayList<QuestionHelp> getQuestionHelpList() {
		return questionHelpList;
	}

	public void setQuestionHelpList(ArrayList<QuestionHelp> questionHelpList) {
		this.questionHelpList = questionHelpList;
	}

	public enum QuestionType {
		FREE_TEXT, OPTION, NUMBER
	}

}
