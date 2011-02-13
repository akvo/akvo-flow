package org.waterforpeople.mapping.surveyentry.client.component;

/**
 * listener for responding to changes in question responses
 * 
 * @author Christopher Fagiani
 * 
 */
public interface QuestionAnswerListener {
	public void answerUpdated(Long questionId, String value);
}
