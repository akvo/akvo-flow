package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Represents a rule used for scoring question responses (i.e. translating from
 * a submitted value to some other enumerated value).
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class ScoringRule extends BaseDomain {

	private static final long serialVersionUID = -3935333809912657757L;
	private String type;
	private Long questionId;
	private String rangeMin;
	private String rangeMax;
	private String value;

	public ScoringRule(Long questionId, String type, String min, String max,
			String val) {
		this(type, min, max, val);
		this.questionId = questionId;
	}

	public ScoringRule(String type, String min, String max, String val) {
		setType(type);
		setRangeMin(min);
		setRangeMax(max);
		this.value = val;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null) {
			this.type = type;
		} else {
			this.type = "NUMERIC";
		}
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getRangeMin() {
		return rangeMin;
	}

	public void setRangeMin(String rangeMin) {
		if (rangeMin != null && rangeMin.trim().length() > 0) {
			this.rangeMin = rangeMin;
		} else {
			this.rangeMin = null;
		}
	}

	public String getRangeMax() {
		return rangeMax;
	}

	public void setRangeMax(String rangeMax) {
		if (rangeMax != null && rangeMax.trim().length() > 0) {
			this.rangeMax = rangeMax;
		} else {
			this.rangeMax = null;
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
