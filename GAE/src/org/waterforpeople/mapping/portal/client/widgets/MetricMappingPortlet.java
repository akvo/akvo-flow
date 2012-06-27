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
import org.waterforpeople.mapping.portal.client.widgets.component.MetricMappingWidget;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;

/**
 * portlet wrapper for the MetricMapping widget
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricMappingPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS.metricMapping();

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;

	public MetricMappingPortlet() {
		super(NAME, true, false, false, WIDTH, HEIGHT);
		MetricMappingWidget mappingWidget = new MetricMappingWidget();
		setContent(mappingWidget);
		mappingWidget.renderInitialState();
	}

	@Override
	public String getName() {
		return NAME;
	}
}
