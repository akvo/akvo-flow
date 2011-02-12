package org.waterforpeople.mapping.surveyentry.client;

import java.util.Collection;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.surveyentry.client.component.QuestionWidget;
import org.waterforpeople.mapping.surveyentry.client.component.QuestionWidgetFactory;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Web-based client for responding to surveys. This page expects the surveyId as
 * a query parameter (sid).
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEntryClient implements EntryPoint {

	private static final String SURVEY_ID_PARAM = "sid";
	private String surveyId;
	private SurveyServiceAsync surveyService;
	private VerticalPanel containerPanel;
	private MessageDialog loadingDialog;
	private SurveyDto surveyDto;
	private TabPanel tabPanel;

	@Override
	public void onModuleLoad() {
		surveyId = Window.Location.getParameter(SURVEY_ID_PARAM);
		surveyService = GWT.create(SurveyService.class);
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");
		containerPanel = new VerticalPanel();
		loadingDialog = new MessageDialog("Loading Survey",
				"Please wait while your survey is loaded", true);
		loadSurveyXml();
		RootPanel.get().add(containerPanel);
		loadingDialog.showCentered();
	}

	protected void loadSurveyXml() {
		surveyService.getPublishedSurvey(surveyId,
				new AsyncCallback<SurveyDto>() {

					@Override
					public void onFailure(Throwable caught) {
						displayErrorMessage(caught);
					}

					@Override
					public void onSuccess(SurveyDto result) {
						surveyDto = result;
						installTabs();
						if (surveyDto.getQuestionGroupList() != null) {
							for (int i = 0; i < surveyDto
									.getQuestionGroupList().size(); i++) {
								installQuestions(i);
							}
						}
						loadComplete();
					}
				});
	}

	private void displayErrorMessage(Throwable caught) {
		loadingDialog.hide();
		MessageDialog errDia = new MessageDialog("Error",
				"Survey could not be loaded. Please double-check the "
						+ SURVEY_ID_PARAM + " parameter");
		errDia.showCentered();
	}

	protected void loadComplete() {
		loadingDialog.hide();
	}

	protected void installTabs() {
		tabPanel = new TabPanel();
		if (surveyDto != null && surveyDto.getQuestionGroupList() != null) {
			for (QuestionGroupDto group : surveyDto.getQuestionGroupList()) {
				tabPanel.add(new VerticalPanel(), group.getDisplayName());
			}
			containerPanel.add(tabPanel);
			tabPanel.selectTab(0);
		}
	}

	protected void installQuestions(int idx) {
		Panel tabContent = (Panel) tabPanel.getWidget(idx);
		Collection<QuestionDto> questions = surveyDto.getQuestionGroupList()
				.get(idx).getQuestionMap().values();
		QuestionWidgetFactory factory = new QuestionWidgetFactory();
		for (QuestionDto q : questions) {
			QuestionWidget w = factory.createQuestionWidget(q);
			if (w != null) {
				tabContent.add(w);
			}
		}
	}
}
