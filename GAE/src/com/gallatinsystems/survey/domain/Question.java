package com.gallatinsystems.survey.domain;

public class Question {
	private String text;
	
	private QuestionType type;
	
	public enum QuestionType {
		FREE_TEXT, OPTION, NUMBER
	}
	
}
