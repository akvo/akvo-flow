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
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyedLocaleManager;

import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;

public class SurveyedLocaleManagerPortlet extends UserAwarePortlet {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS.surveyedLocaleManager();

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;

	private SurveyedLocaleManager mgrWidget;

	public SurveyedLocaleManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user);
		mgrWidget = new SurveyedLocaleManager(user);
		setContent(mgrWidget);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
