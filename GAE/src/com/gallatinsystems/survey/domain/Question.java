package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class Question extends BaseDomain implements Comparable<Question> {

	private static final long serialVersionUID = -4708385830894435407L;

	private String referenceIndex;
	private String text;
	private String validationRule;
	private QuestionDependency dependQuestion = null;
	private Integer order;
	private String tip;
	@NotPersistent
	private OptionContainer optionContainer = null;
	private QuestionDto.QuestionType type;
	private ArrayList<QuestionHelp> questionHelpList;
	private String path = null;
	private Boolean mandatory = null;
	
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getReferenceIndex() {
		return referenceIndex;
	}

	public void setReferenceIndex(String referenceIndex) {
		this.referenceIndex = referenceIndex;
	}

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

	public void setDependQuestion(QuestionDependency dependQuestion) {
		this.dependQuestion = dependQuestion;
	}

	public QuestionDependency getDependQuestion() {
		return dependQuestion;
	}

	@Override
	public int compareTo(Question o) {
		if (o != null && o.getOrder() != null && getOrder() != null) {
			if (o.getOrder() == getOrder()) {
				return 0;
			} else if (getOrder() > o.getOrder()) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return 1;
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Boolean getMandatory() {
		return mandatory;
	}
}
