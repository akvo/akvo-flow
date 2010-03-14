package com.gallatinsystems.survey.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class Survey extends BaseDomain{
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
