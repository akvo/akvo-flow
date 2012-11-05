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

import org.waterforpeople.mapping.portal.client.widgets.component.RunReportWidget;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;

/**
 * Portlet that allows the user to select a report to run.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RunReportsPortlet extends Portlet {

	public static final String NAME = "Run Reports";
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;
	private RunReportWidget reportWidget;

	public RunReportsPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		reportWidget = new RunReportWidget();
		setContent(reportWidget);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
