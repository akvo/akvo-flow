package com.gallatinsystems.framework.gwt.component;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

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
