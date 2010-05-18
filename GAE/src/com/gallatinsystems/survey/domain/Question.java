package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Question extends BaseDomain {
	
	private static final long serialVersionUID = -4708385830894435407L;

	private String text;
	private String validationRule;
	public String getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(String validationRule) {
		this.validationRule = validationRule;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public OptionContainer getOptionContainer() {
		return optionContainer;
	}

	public void setOptionContainer(OptionContainer optionContainer) {
		this.optionContainer = optionContainer;
	}



	private String tip;
	private OptionContainer optionContainer = null;
	
	private QuestionDto.QuestionType type;
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
