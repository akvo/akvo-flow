package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.MetricManagerWidget;

import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;

public class MetricManagerPortlet extends UserAwarePortlet {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS.metricManager();

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;

	private MetricManagerWidget mgrWidget;

	public MetricManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user);
		mgrWidget = new MetricManagerWidget(user);
		setContent(mgrWidget);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
