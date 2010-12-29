package com.gallatinsystems.framework.gwt.component;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;

public class Breadcrumb extends Label implements MouseOverHandler, MouseOutHandler {
	private static final String BREADCRUMB_STYLE = "breadcrumb";
	private static final String HOVER_STYLE = "red-hover";
	private String targetNode;

	public Breadcrumb(String text, String targetNode) {
		super();
		setText(text);
		setStylePrimaryName(BREADCRUMB_STYLE);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
		this.targetNode = targetNode;
	}

	public String getTargetNode() {
		return targetNode;
	}

	public boolean equals(Object other) {
		boolean isSame = false;
		if (other instanceof Breadcrumb) {
			if (((Breadcrumb) other).getText().equals(getText())) {
				isSame = true;
			}

		}
		return isSame;
	}
	
	public int hashCode(){
		return getText().hashCode();
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		removeStyleName(HOVER_STYLE);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		addStyleName(HOVER_STYLE);
	}
}
