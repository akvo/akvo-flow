package com.gallatinsystems.survey.device.event;

/**
 * Interface that should be implemented by any class that wants to be notified
 * of QuestionInteractionEvent occurrences.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface QuestionInteractionListener {

    public void onQuestionInteraction(QuestionInteractionEvent event);
}
