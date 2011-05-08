package com.gallatinsystems.framework.gwt.component;

import java.util.Map;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;

/**
 * datastructure used to render breadcrumb navigation. Each breadcrumb can have
 * a contextBundle associated with it that is used to restore the correct state
 * when the user clicks a breadcrumb. Breadcrumbs use CSS to change their style
 * when the user moves his mouse over a breadcrumb link.
 * 
 * @author Christopher Fagiani
 * 
 */
public class Breadcrumb extends Label implements MouseOverHandler,
		MouseOutHandler {
	private static final String BREADCRUMB_STYLE = "breadcrumb";
	private static final String HOVER_STYLE = "red-hover";
	private String targetNode;
	private Map<String, Object> bundle;

	public Breadcrumb(String text, String targetNode, Map<String, Object> bundle) {
		super();
		setText(text);
		setStylePrimaryName(BREADCRUMB_STYLE);
		addMouseOutHandler(this);
		addMouseOverHandler(this);
		this.targetNode = targetNode;
		this.bundle = bundle;
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

	public int hashCode() {
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

	public Map<String, Object> getBundle() {
		return bundle;
	}

	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}
}
