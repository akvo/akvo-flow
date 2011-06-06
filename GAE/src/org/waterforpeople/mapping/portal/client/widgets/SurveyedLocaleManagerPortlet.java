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
