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

	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
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
