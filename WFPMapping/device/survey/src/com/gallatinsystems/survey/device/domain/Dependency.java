package com.gallatinsystems.survey.device.domain;

/**
 * data structure representing a dependency between questions. When a dependency
 * exists, the question with the dependency object will not be displayed unless
 * the option selected for the referenced question matches the answer indicated
 * in the dependency object.
 * 
 *TODO: replace the comment above with something that is clearer
 * 
 * @author Christopher Fagiani
 * 
 */
public class Dependency {
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
