package org.waterforpeople.mapping.surveyentry.client;

import org.waterforpeople.mapping.surveyentry.client.component.SurveyEntryWidget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Web-based client for responding to surveys. This page expects the surveyId as
 * a query parameter (sid).
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEntryClient implements EntryPoint {

	private static final String SURVEY_ID_PARAM = "sid";
	private SurveyEntryWidget entryWidget;

	@Override
	public void onModuleLoad() {

		String surveyId = Window.Location.getParameter(SURVEY_ID_PARAM);
		entryWidget = new SurveyEntryWidget(surveyId);
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");
		RootPanel.get().add(entryWidget);
		entryWidget.initialize();
	}
}
