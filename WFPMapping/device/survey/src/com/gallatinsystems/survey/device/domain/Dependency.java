package com.gallatinsystems.survey.device.domain;

/**
 * data structure representing a dependency between questions. A dependency
 * consists of two values: a question ID and an answer value. When a question
 * contains a dependency, it will not be shown unless the question referenced by
 * the dependency's questionID has an answer that matches the answerValue in the
 * A question can have 0 or 1 dependencies.
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class Dependency {
	private String question;
	private String answer;

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
