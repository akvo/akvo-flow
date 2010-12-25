package com.gallatinsystems.framework.gwt.component;

import com.google.gwt.user.client.ui.Label;


public class Breadcrumb extends Label{
	private static final String BREADCRUMB_STYLE = "breadcrumb";
	private String targetNode;
	public Breadcrumb(String text, String targetNode){
		super();
		setText(text);
		setStylePrimaryName(BREADCRUMB_STYLE);
		this.targetNode = targetNode;
	}

	public String getTargetNode(){
		return targetNode;
	}
}
