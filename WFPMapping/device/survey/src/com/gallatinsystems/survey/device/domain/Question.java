package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;

/**
 * data structure for individual survey questions. Questions have a type which
 * can be any one of:
 * <ul>
 * <li>option - radio-button like selection</li>
 * <li>free - free text</li>
 * <li>date - date selector</li>
 * <li>geo - geographic detection (GPS)</li>
 * </ul>
 * 
 * @author Christopher Fagiani
 * 
 */
public class Question {
    private String id;
    private String text;
    private int order;
    private String tip;
    private ValidationRule validationRule;
    private String renderType;
    

    public static final String FREE_TYPE = "free";
    public static final String OPTION_TYPE = "option";
    public static final String GEO_TYPE = "geo";
    public static final String DATE_TYPE = "date";
    public static final String TEXTONLY_TYPE = "textonly";
    public static final String PHOTO_TYPE = "photo";
    public static final String SPINNER_TYPE = "spinner";

    public String getRenderType() {
		return renderType;
	}

	public void setRenderType(String renderType) {
		this.renderType = renderType;
	}

	public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    private boolean mandatory;
    private String type;
    private ArrayList<Option> options;
    private boolean allowOther;
    private ArrayList<Dependency> dependencies;

    public ArrayList<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }

    public boolean isAllowOther() {
        return allowOther;
    }

    public void setAllowOther(boolean allowOther) {
        this.allowOther = allowOther;
    }

    public void addDependency(Dependency dep) {
        if (dependencies == null) {
            dependencies = new ArrayList<Dependency>();
        }
        dependencies.add(dep);
    }

    public ValidationRule getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(ValidationRule validationRule) {
        this.validationRule = validationRule;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String toString() {
        return text;
    }
}
