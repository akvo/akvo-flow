package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.user.UserDto;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;

/**
 * utility to create any of the portlets we know about. There are better ways to
 * do this, but since we don't have reflection on the client-side, need to
 * hard-code the list of known portlets.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PortletFactory {
	public static final Object[][] AVAILABLE_PORTLETS = {
			{ SummaryPortlet.NAME, SummaryPortlet.DESCRIPTION },
			{ ActivityChartPortlet.NAME, ActivityChartPortlet.DESCRIPTION },
			{ ActivityMapPortlet.NAME, ActivityMapPortlet.DESCRIPTION },
			{ SurveyQuestionPortlet.NAME, SurveyQuestionPortlet.DESCRIPTION },
			{ AccessPointStatusPortlet.NAME,
					AccessPointStatusPortlet.DESCRIPTION },
			{ AccessPointManagerPortlet.NAME,
					AccessPointManagerPortlet.DESCRIPTION },
			{ DeviceLocationPortlet.NAME, DeviceLocationPortlet.DESCRIPTION },
			{ AccessPointPerformancePortlet.NAME,
					AccessPointPerformancePortlet.DESCRIPTION },
			{ TechnologyTypeManagerPortlet.NAME,
					TechnologyTypeManagerPortlet.DESCRIPTION },
			{ SurveyAssignmentPortlet.NAME, SurveyAssignmentPortlet.DESCRIPTION },
			{ DisplayContentManager.NAME, DisplayContentManager.DESCRIPTION },
			{ SurveyAttributeMappingPortlet.NAME,
					SurveyAttributeMappingPortlet.DESCRIPTION } };

	public static Portlet createPortlet(String name, UserDto user) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Name cannot be null when invoking PortletFactory.createPortlet");
		}
		if (name.equals(SummaryPortlet.NAME)) {
			return new SummaryPortlet();
		} else if (name.equals(ActivityChartPortlet.NAME)) {
			return new ActivityChartPortlet(user);
		} else if (name.equals(ActivityMapPortlet.NAME)) {
			return new ActivityMapPortlet(user);
		} else if (name.equals(SurveyQuestionPortlet.NAME)) {
			return new SurveyQuestionPortlet();
		} else if (name.equals(AccessPointStatusPortlet.NAME)) {
			return new AccessPointStatusPortlet(user);
		} else if (name.equals(AccessPointManagerPortlet.NAME)) {
			return new AccessPointManagerPortlet(user);
		} else if (name.equals(DeviceLocationPortlet.NAME)) {
			return new DeviceLocationPortlet();
		} else if (name.equals(AccessPointPerformancePortlet.NAME)) {
			return new AccessPointPerformancePortlet(user);
		} else if (name.equals(TechnologyTypeManagerPortlet.NAME)) {
			return new TechnologyTypeManagerPortlet();
		} else if (name.equals(SurveyAssignmentPortlet.NAME)) {
			return new SurveyAssignmentPortlet();
		} else if (name.equals(DisplayContentManager.NAME)) {
			return new DisplayContentManager();
		} else if (name.equals(SurveyAttributeMappingPortlet.NAME)) {
			return new SurveyAttributeMappingPortlet();
		} else {
			throw new IllegalArgumentException("Unrecognized portlet name");
		}
	}
}
