/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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
package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

public class QuestionAnswerStorePayload implements Serializable {

	private static final long serialVersionUID = -1111440035804928338L;
	QuestionAnswerStoreDto question_answer_store= null;

	public QuestionAnswerStoreDto getQuestion_answer_store() {
		return question_answer_store;
	}

	public void setQuestion_answer_store(QuestionAnswerStoreDto question_answer_store) {
		this.question_answer_store = question_answer_store;
	}
}
