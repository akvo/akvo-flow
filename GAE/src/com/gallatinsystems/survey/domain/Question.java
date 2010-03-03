package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Question extends BaseDomain {
	private String text;
	
	private QuestionType type;
	private ArrayList<QuestionOption> optionsList;
	private ArrayList<QuestionHelp> questionHelpList;
	
	public void addOption(QuestionOption questionOption){
		if(optionsList==null){
			optionsList = new ArrayList<QuestionOption>();
		}
		optionsList.add(questionOption);
	}
	
	public void addQuestionHelp(QuestionHelp questionHelp){
		if(questionHelpList==null){
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
