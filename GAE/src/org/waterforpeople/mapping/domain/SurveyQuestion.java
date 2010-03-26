package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SurveyQuestion extends BaseDomain {
	
	@Persistent
	private String id;
	@Persistent
	private Long order;
	@Persistent
	private Boolean manadatory;
	@Persistent
	private QuestionAnswerType type;
	@Persistent
	private String text;
	@Persistent
	private ArrayList<SurveyQuestionOption> options;
	@Persistent
	private String optionsAllowOthers;

	public enum QuestionAnswerType {
		free, geo, photo, option
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public Boolean getManadatory() {
		return manadatory;
	}

	public void setManadatory(Boolean manadatory) {
		this.manadatory = manadatory;
	}

	public QuestionAnswerType getType() {
		return type;
	}

	public void setType(QuestionAnswerType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<SurveyQuestionOption> getOptions() {
		return options;
	}

	public void setOptions(ArrayList<SurveyQuestionOption> options) {
		this.options = options;
	}

	public String getOptionsAllowOthers() {
		return optionsAllowOthers;
	}

	public void setOptionsAllowOthers(String optionsAllowOthers) {
		this.optionsAllowOthers = optionsAllowOthers;
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
