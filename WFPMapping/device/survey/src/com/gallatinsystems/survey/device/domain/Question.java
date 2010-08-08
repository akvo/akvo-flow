package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;
import java.util.HashMap;

import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * data structure for individual survey questions. Questions have a type which
 * can be any one of:
 * <ul>
 * <li>option - radio-button like selection</li>
 * <li>free - free text</li>
 * <li>video - video capture</li>
 * <li>photo - photo capture</li>
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
	private ValidationRule validationRule;
	private String renderType;
	private ArrayList<QuestionHelp> questionHelp;
	private boolean mandatory;
	private String type;
	private ArrayList<Option> options;
	private boolean allowOther;
	private boolean allowMultiple;
	private boolean locked;
	private HashMap<String, AltText> altTextMap = new HashMap<String, AltText>();
	private ArrayList<Dependency> dependencies;

	public ArrayList<QuestionHelp> getQuestionHelp() {
		return questionHelp;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public HashMap<String, AltText> getAltTextMap() {
		return altTextMap;
	}

	public AltText getAltText(String lang) {
		return altTextMap.get(lang);
	}

	public void addAltText(AltText altText) {
		altTextMap.put(altText.getLanguage(), altText);
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	

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

	public ArrayList<QuestionHelp> getHelpByType(String type) {
		ArrayList<QuestionHelp> help =  new ArrayList<QuestionHelp>();;
		if (questionHelp != null && type != null) {			
			for (int i = 0; i < questionHelp.size(); i++) {
				if (type.equalsIgnoreCase(questionHelp.get(i).getType())) {
					help.add(questionHelp.get(i));
				}
			}
		}
		return help;
	}

	public void addQuestionHelp(QuestionHelp help) {
		if (questionHelp == null) {
			questionHelp = new ArrayList<QuestionHelp>();
		}
		questionHelp.add(help);
	}

	public ValidationRule getValidationRule() {
		return validationRule;
	}

	public void setValidationRule(ValidationRule validationRule) {
		this.validationRule = validationRule;
	}

	/**
	 * counts the number of non-empty help tip types
	 * 
	 * @return
	 */
	public int getHelpTypeCount() {
		int count = 0;
		if (getHelpByType(ConstantUtil.IMAGE_HELP_TYPE).size() > 0) {
			count++;
		}
		if (getHelpByType(ConstantUtil.TIP_HELP_TYPE).size() > 0) {
			count++;
		}
		if (getHelpByType(ConstantUtil.VIDEO_HELP_TYPE).size() > 0) {
			count++;
		}
		if (getHelpByType(ConstantUtil.ACTIVITY_HELP_TYPE).size() > 0) {
			count++;
		}
		return count;
	}

	public String toString() {
		return text;
	}
}
