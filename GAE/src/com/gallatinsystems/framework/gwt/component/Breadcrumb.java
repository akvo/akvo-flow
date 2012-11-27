/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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

	/**
	 * checks for logical equality of a breadcrumb. Two breadcrumbs are equal if
	 * they share the same text.
	 * 
	 * @param other
	 * @return
	 */
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

	/**
	 * removes the hover style
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOut(MouseOutEvent event) {
		removeStyleName(HOVER_STYLE);
	}

	/**
	 * sets the hover style
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOver(MouseOverEvent event) {
		addStyleName(HOVER_STYLE);
	}

	/**
	 * gets the bundle (map of name/value pairs representing state) associated
	 * with this breadcrumb. The actual underlying map is returned so any
	 * modifications to the map in the client code will be reflected here as
	 * well.
	 * 
	 * @return
	 */
	public Map<String, Object> getBundle() {
		return bundle;
	}

	/**
	 * sets the bundle (map of name/value pairs representing state) associated
	 * with this breadcrumb. This will overwrite any existing bundle already
	 * saved with the breadcrumb.
	 * 
	 * @param bundle
	 */
	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}
}
