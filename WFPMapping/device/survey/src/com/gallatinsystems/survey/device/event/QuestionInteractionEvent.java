package com.gallatinsystems.survey.device.event;

import com.gallatinsystems.survey.device.view.QuestionView;

/**
 * event to be fired when the user interacts with a question in a significant
 * way.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionInteractionEvent {

    public static final String TAKE_PHOTO_EVENT = "PHOTO";
    public static final String GEO_CHECK_EVENT = "GEO";
    public static final String QUESTION_ANSWER_EVENT = "ANS";

    private String eventType;
    private QuestionView source;    

    

    public QuestionInteractionEvent(String type, QuestionView source) {
        this.eventType = type;
        this.source = source;        
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public QuestionView getSource() {
        return source;
    }

    public void setSource(QuestionView source) {
        this.source = source;
    }
}
