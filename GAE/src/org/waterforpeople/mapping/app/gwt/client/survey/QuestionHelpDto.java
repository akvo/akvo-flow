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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.Map;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionHelpDto extends BaseDto {

	private static final long serialVersionUID = 1563528591253495401L;
	private String text;
	private String resourceUrl;
	private Type type;
	private Long questionId;
	private Map<String, TranslationDto> translationMap;

	public Map<String, TranslationDto> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(Map<String, TranslationDto> translationMap) {
		this.translationMap = translationMap;
	}

	public void addTranslation(TranslationDto trans) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, TranslationDto>();
		}
		translationMap.put(trans.getLangCode(), trans);
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public enum Type {
		PHOTO, VIDEO, TEXT, ACTIVITY
	}

	/**
	 * returns the translated version of the text for the locale specified (if
	 * present). If no translation exists, it will return the default text.
	 * 
	 * @param locale
	 * @return
	 */
	public String getLocalizedText(String locale) {
		if (locale != null && translationMap != null) {
			TranslationDto trans = translationMap.get(locale);
			String txt = null;
			if (trans != null) {
				txt = trans.getText();
			}
			if (txt != null && txt.trim().length() > 0) {
				return txt;
			} else {
				return this.text;
			}
		} else {
			return this.text;
		}
	}

}
