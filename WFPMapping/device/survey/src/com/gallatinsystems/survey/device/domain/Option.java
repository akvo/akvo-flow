package com.gallatinsystems.survey.device.domain;

/**
 * simple data structure for representing question options.
 * 
 * @author Christopher Fagiani
 * 
 */
public class Option {

    private String text;

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

    private String value;
}
