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
		if (QuestionDto.QuestionType.FREE_TEXT == question.getType()) {
			q = new TextQuestionWidget(question, a, false);
		} else if (QuestionDto.QuestionType.NUMBER == question.getType()) {
			q = new TextQuestionWidget(question, a, true);
		} else if (QuestionDto.QuestionType.GEO == question.getType()) {
			q = new GeoQuestionWidget(question,a);
		} else if (QuestionDto.QuestionType.PHOTO == question.getType()
				|| QuestionDto.QuestionType.VIDEO == question.getType()) {
			q = new MediaQuestionWidget(question,a, question.getType().toString());
		} else if (QuestionDto.QuestionType.OPTION == question.getType()) {
			q = new OptionQuestionWidget(question, a,listener);
		}		
		return q;
	}
}
