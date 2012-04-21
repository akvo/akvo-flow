package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SuperAdminWidget;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class SuperAdminPortlet extends Portlet implements ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	public static final String NAME = TEXT_CONSTANTS.adminOnly();

	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private SuperAdminWidget widget;

	public SuperAdminPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		widget = new SuperAdminWidget();
		setContent(widget);
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return NAME;
	}

}
