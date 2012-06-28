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

package com.gallatinsystems.survey.device.domain;

import java.util.HashMap;

/**
 * domain object for help media. If the type == tip, then value is undefined.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionHelp {

	private HashMap<String, AltText> altTextMap = new HashMap<String, AltText>();
	private String type;
	private String text;
	private String value;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HashMap<String, AltText> getAltTextMap() {
		return altTextMap;
	}

	public AltText getAltText(String lang) {
		return altTextMap.get(lang);
	}

	public void addAltText(AltText altText) {
		altTextMap.put(altText.getLanguage(), altText);
	}

	/**
	 * 
	 * checks whether this help object is well formed
	 */
	public boolean isValid() {
		if (text == null || text.trim().length() == 0) {
			// if text is null, then value must be populated for this to be
			// valid
			if (value == null || value.trim().length() == 0) {
				return false;
			}
		} else {
			// if text is not null, then it can't be the string "null"
			if ("null".equalsIgnoreCase(text.trim())) {
				return false;
			}
		}

		return true;

	}
}
