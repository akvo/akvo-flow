package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

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
	 * TODO: handle unsupported question types
	 * 
	 * @param question
	 * @return
	 */
	public QuestionWidget createQuestionWidget(QuestionDto question, QuestionAnswerListener listener) {

		if (QuestionDto.QuestionType.FREE_TEXT == question.getType()
				|| QuestionDto.QuestionType.NUMBER == question.getType()) {
			return new TextQuestionWidget(question);
		} else if (QuestionDto.QuestionType.GEO == question.getType()) {
			return new GeoQuestionWidget(question);
		} else if (QuestionDto.QuestionType.PHOTO == question.getType()
				|| QuestionDto.QuestionType.VIDEO == question.getType()) {
			return new MediaQuestionWidget(question, question.getType().toString());
		} else if (QuestionDto.QuestionType.OPTION == question.getType()) {
			return new OptionQuestionWidget(question,listener);
		}
		return null;
	}
}
