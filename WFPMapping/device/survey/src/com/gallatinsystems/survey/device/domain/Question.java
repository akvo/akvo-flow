package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;
import java.util.List;

public class Question {
	private String id;
	private String text;
	private int order;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	private boolean mandatory;
	private String type;
	private List<Option> options;
	private boolean allowOther;
	private List<Dependency> dependencies;

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
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

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
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
	
	public String toString(){
		return text;
	}
}
