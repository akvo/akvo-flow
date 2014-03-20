/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
	private Long surveyId = null;
	private Long questionGroupId = null;
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

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public Long getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public enum ParentType {
		SURVEY_GROUP_NAME, SURVEY_GROUP_DESC, SURVEY_NAME, SURVEY_DESC, QUESTION_GROUP_DESC, QUESTION_GROUP_NAME, QUESTION_NAME, QUESTION_DESC, QUESTION_TEXT, QUESTION_TIP, QUESTION_OPTION, QUESTION_HELP_MEDIA_TEXT
	};

}
