package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.user.app.gwt.client.UserDto;

/**
 * utility to create any of the portlets we know about. There are better ways to
 * do this, but since we don't have reflection on the client-side, need to
 * hard-code the list of known portlets.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PortletFactory {

	private static final String LOCALE_DOMAIN_TYPE = "locale";

	public static final Object[][] AVAILABLE_PORTLETS = {
			{ SummaryPortlet.NAME, SummaryPortlet.DESCRIPTION },
			{ ActivityChartPortlet.NAME, ActivityChartPortlet.DESCRIPTION },
			// { ActivityMapPortlet.NAME, ActivityMapPortlet.DESCRIPTION },
			{ SurveyQuestionPortlet.NAME, SurveyQuestionPortlet.DESCRIPTION },
			/*
			 * { AccessPointStatusPortlet.NAME,
			 * AccessPointStatusPortlet.DESCRIPTION },
			 */
			{ DeviceLocationPortlet.NAME, DeviceLocationPortlet.DESCRIPTION },
			/*
			 * { AccessPointPerformancePortlet.NAME,
			 * AccessPointPerformancePortlet.DESCRIPTION },
			 */
			{ AccessPointMetricChartPortlet.NAME,
					AccessPointMetricChartPortlet.DESCRIPTION },
	/* { RecentPointsPortlet.NAME, RecentPointsPortlet.DESCRIPTION } */
	};

	public static Portlet createPortlet(String name, UserDto user,
			String domainType) {
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
			if (LOCALE_DOMAIN_TYPE.equalsIgnoreCase(domainType)) {
				return new SurveyedLocaleManagerPortlet(user);
			} else {
				return new AccessPointManagerPortlet(user);
			}
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
			if (LOCALE_DOMAIN_TYPE.equalsIgnoreCase(domainType)) {
				return new MetricMappingPortlet();
			} else {
				return new SurveyAttributeMappingPortlet();
			}
		} else if (name.equals(DataUploadPortlet.NAME)) {
			return new DataUploadPortlet();
		} else if (name.equals(SurveyLoaderPortlet.NAME)) {
			return new SurveyLoaderPortlet();
		} else if (name.equals(RawDataViewPortlet.NAME)) {
			return new RawDataViewPortlet(user);
		} else if (name.equals(MappingAttributeManager.NAME)) {
			return new MappingAttributeManager();
		} else if (name.equals(RecentPointsPortlet.NAME)) {
			return new RecentPointsPortlet();
		} else if (name.equals(UserManagerPortlet.NAME)) {
			return new UserManagerPortlet(user);
		} else if (name.equals(RemoteExceptionPortlet.NAME)) {
			return new RemoteExceptionPortlet();
		} else if (name.equals(DeviceFileManagerPortlet.NAME)) {
			return new DeviceFileManagerPortlet(user);
		} else if (name.equals(AdminWizardPortlet.NAME)) {
			return new AdminWizardPortlet(user, domainType);
		} else if (name.equals(RunReportsPortlet.NAME)) {
			return new RunReportsPortlet();
		} else if (name.equals(AccessPointMetricChartPortlet.NAME)) {
			return new AccessPointMetricChartPortlet(user);
		} else if (name.equals(StandardScoringManagerPortlet.NAME)) {
			return new StandardScoringManagerPortlet(user);
		} else if (name.equals(MessageViewPortlet.NAME)) {
			return new MessageViewPortlet(user);
		} else if (name.equals(SurveyedLocaleManagerPortlet.NAME)) {
			return new SurveyedLocaleManagerPortlet(user);
		} else if (name.equals(MetricManagerPortlet.NAME)) {
			return new MetricManagerPortlet(user);
		} else {
			throw new IllegalArgumentException("Unrecognized portlet name");
		}
	}
}
