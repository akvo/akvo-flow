package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * data structure for grouping questions under a common heading.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionGroup {

    private int order;
    private String heading;
    private List<Question> questions;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question q) {
        if (questions == null) {
            questions = new ArrayList<Question>();
        }
        questions.add(q);
    }
}
