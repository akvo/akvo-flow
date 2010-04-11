package org.waterforpeople.mapping.portal.client.widgets;

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
					AccessPointPerformancePortlet.DESCRIPTION } ,
					{ TechnologyTypeManagerPortlet.NAME,
						TechnologyTypeManagerPortlet.DESCRIPTION }
};

	public static Portlet createPortlet(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Name cannot be null when invoking PortletFactory.createPortlet");
		}
		if (name.equals(SummaryPortlet.NAME)) {
			return new SummaryPortlet();
		} else if (name.equals(ActivityChartPortlet.NAME)) {
			return new ActivityChartPortlet();
		} else if (name.equals(ActivityMapPortlet.NAME)) {
			return new ActivityMapPortlet();
		} else if (name.equals(SurveyQuestionPortlet.NAME)) {
			return new SurveyQuestionPortlet();
		} else if (name.equals(AccessPointStatusPortlet.NAME)) {
			return new AccessPointStatusPortlet();
		} else if (name.equals(AccessPointManagerPortlet.NAME)) {
			return new AccessPointManagerPortlet();
		} else if (name.equals(DeviceLocationPortlet.NAME)) {
			return new DeviceLocationPortlet();
		} else if (name.equals(AccessPointPerformancePortlet.NAME)) {
			return new AccessPointPerformancePortlet();
		} else if (name.equals(TechnologyTypeManagerPortlet.NAME)) {
			return new TechnologyTypeManagerPortlet();
		}else {
			throw new IllegalArgumentException("Unrecognized portlet name");
		}
	}

}
