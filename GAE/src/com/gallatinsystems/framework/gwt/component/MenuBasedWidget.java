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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

/**
 * base class to be used for any widget that needs to present a grid of buttons
 * with descriptions
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class MenuBasedWidget extends Composite implements ClickHandler {
	private static final String DESC_CSS = "description-text";
	private static final String BUTTON_CSS = "admin-button";

	
	/**
	 * constructs a new button, sets its style and adds this class as a click
	 * handler
	 * 
	 * @param buttonText
	 * @return
	 */
	protected Button initButton(String text) {
		Button button = new Button(text);
		button.addClickHandler(this);
		button.setStylePrimaryName(BUTTON_CSS);
		return button;
	}

	/**
	 * constructs a new label containing the text passed in and sets the style
	 * to the description css
	 * 
	 * @param text
	 * @return
	 */
	protected Label createDescription(String text) {
		Label desc = new Label();
		desc.setStylePrimaryName(DESC_CSS);
		desc.setText(text);
		return desc;
	}
}
