package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * persistent class that represents a translation of some textual element.
 * Translations can be attached to many things in the system (defined by the
 * parentType and parentId). Each translation contains the language code and
 * translation text.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Translation extends BaseDomain {

	private static final long serialVersionUID = 1297504186153393251L;

	private String languageCode = null;
	private String text = null;
	private Long parentId = null;
	private ParentType parentType = null;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public ParentType getParentType() {
		return parentType;
	}

	public void setParentType(ParentType parentType) {
		this.parentType = parentType;
	}

	public enum ParentType {
		SURVEY_GROUP_NAME, SURVEY_GROUP_DESC, SURVEY_NAME, SURVEY_DESC, QUESTION_GROUP_DESC, QUESTION_GROUP_NAME, QUESTION_NAME, QUESTION_DESC, QUESTION_TEXT, QUESTION_TIP, QUESTION_OPTION, QUESTION_HELP_MEDIA_TEXT
	};

}
