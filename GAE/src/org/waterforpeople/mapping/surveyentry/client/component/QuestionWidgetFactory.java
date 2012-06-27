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

package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

/**
 * simple factory for creation of QuestionWidgets
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionWidgetFactory {

	/**
	 * creates the correct widget based on the type of the question passed in.
	 * 
	 * 
	 * @param question
	 * @return
	 */
	public QuestionWidget createQuestionWidget(QuestionDto question,
			QuestionAnswerStoreDto a, QuestionAnswerListener listener) {
		QuestionWidget q = null;
		QuestionAnswerStoreDto answerCopy = null;
		if(a != null){
			answerCopy = new QuestionAnswerStoreDto();
			answerCopy.setArbitratyNumber(a.getArbitratyNumber());
			answerCopy.setCollectionDate(a.getCollectionDate());
			answerCopy.setKeyId(a.getKeyId());
			answerCopy.setQuestionID(a.getQuestionID());
			answerCopy.setQuestionText(a.getQuestionText());
			answerCopy.setSurveyId(a.getSurveyId());
			answerCopy.setSurveyInstanceId(a.getSurveyInstanceId());
			answerCopy.setType(a.getType());
			answerCopy.setValue(a.getValue());
		}
		if (QuestionDto.QuestionType.FREE_TEXT == question.getType()) {
			q = new TextQuestionWidget(question, answerCopy, false);
		} else if (QuestionDto.QuestionType.NUMBER == question.getType()) {
			q = new TextQuestionWidget(question, answerCopy, true);
		} else if (QuestionDto.QuestionType.GEO == question.getType()) {
			q = new GeoQuestionWidget(question,answerCopy);
		} else if (QuestionDto.QuestionType.PHOTO == question.getType()
				|| QuestionDto.QuestionType.VIDEO == question.getType()) {
			q = new MediaQuestionWidget(question,answerCopy, question.getType().toString());
		} else if (QuestionDto.QuestionType.OPTION == question.getType()) {
			q = new OptionQuestionWidget(question, answerCopy,listener);
		}else if (QuestionDto.QuestionType.DATE == question.getType()){
			q = new DateQuestionWidget(question, answerCopy);
		}
		return q;
	}
}
