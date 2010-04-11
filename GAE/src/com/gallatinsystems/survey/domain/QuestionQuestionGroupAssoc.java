package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseAssocDomain;

@PersistenceCapable
@Inheritance(customStrategy = "complete-table")
public class QuestionQuestionGroupAssoc extends BaseAssocDomain{
	
	private static final long serialVersionUID = 7578071601713731890L;
	private Question question;
	private QuestionGroup questionGroup;
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public QuestionGroup getQuestionGroup() {
		return questionGroup;
	}
	public void setQuestionGroup(QuestionGroup questionGroup) {
		this.questionGroup = questionGroup;
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
