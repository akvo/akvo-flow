package com.gallatinsystems.survey.device.domain;

import java.util.ArrayList;

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
	private String tip;
	private ValidationRule validationRule;
	private String renderType;
	private String video;
	private ArrayList<String> images;

	private ArrayList<String> imageCaptions;

	private boolean mandatory;
	private String type;
	private ArrayList<Option> options;
	private boolean allowOther;
	private boolean allowMultiple;

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	private ArrayList<Dependency> dependencies;

	public ArrayList<String> getImages() {
		return images;
	}

	public ArrayList<String> getImageCaptions() {
		return imageCaptions;
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

	public void addImage(String img) {
		if (images == null) {
			images = new ArrayList<String>();
		}
		images.add(img);
	}

	public void addImageCaption(String cap) {
		if (imageCaptions == null) {
			imageCaptions = new ArrayList<String>();
		}
		imageCaptions.add(cap);
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

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	/**
	 * counts the number of non-null help tips
	 * 
	 * @return
	 */
	public int getTipCount() {
		int count = 0;
		if (tip != null) {
			count++;
		}
		if (video != null) {
			count++;
		}
		if (images != null && images.size() > 0) {
			count++;
		}
		return count;
	}

	public String toString() {
		return text;
	}
}
