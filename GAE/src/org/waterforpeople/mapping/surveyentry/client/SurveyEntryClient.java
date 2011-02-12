package org.waterforpeople.mapping.surveyentry.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
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
		loadSurvey();
		RootPanel.get().add(containerPanel);
		loadingDialog.showCentered();
	}

	protected void loadSurvey() {
		surveyService.listQuestionGroupsBySurvey(surveyId,
				new AsyncCallback<ArrayList<QuestionGroupDto>>() {

					@Override
					public void onSuccess(ArrayList<QuestionGroupDto> result) {
						surveyDto = new SurveyDto();
						if (result != null && result.size() > 0) {
							surveyDto.setQuestionGroupList(result);
							surveyDto.setKeyId(result.get(0).getSurveyId());
							installTabs();
							loadQuestionList(0);
						} else {
							loadComplete();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						displayErrorMessage(caught);
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

	private void loadQuestionList(final int groupIndex) {
		if (surveyDto.getQuestionGroupList() != null) {
			if (groupIndex < surveyDto.getQuestionGroupList().size()) {
				if (surveyDto.getQuestionGroupList().get(groupIndex)
						.getQuestionMap() == null
						|| surveyDto.getQuestionGroupList().get(groupIndex)
								.getQuestionMap().size() == 0) {
					surveyService.listQuestionsByQuestionGroup(surveyDto
							.getQuestionGroupList().get(groupIndex).getKeyId()
							.toString(), false,
							new AsyncCallback<ArrayList<QuestionDto>>() {
								@Override
								public void onFailure(Throwable caught) {
									displayErrorMessage(caught);
								}

								@Override
								public void onSuccess(
										ArrayList<QuestionDto> result) {
									TreeMap<Integer, QuestionDto> questionMap = new TreeMap<Integer, QuestionDto>();
									if (result != null) {
										int i = 1;
										for (QuestionDto q : result) {
											if (q.getOrder() != null) {
												questionMap
														.put(q.getOrder(), q);
											} else {
												questionMap.put(i, q);
											}
											i++;
										}
									}
									surveyDto.getQuestionGroupList().get(
											groupIndex).setQuestionMap(
											questionMap);
									loadQuestionList(groupIndex + 1);
								}

							});
				} else {
					// if the questions are already loaded, try to load the next
					// group
					loadQuestionList(groupIndex + 1);
				}
			} else {
				loadQuestionDetails(0, 0);
			}
		} else {
			loadComplete();
		}
	}

	private void loadQuestionDetails(final int groupIndex,
			final int questionIndex) {
		if (groupIndex < surveyDto.getQuestionGroupList().size()) {
			final QuestionGroupDto group = surveyDto.getQuestionGroupList()
					.get(groupIndex);
			if (group.getQuestionMap() != null
					&& questionIndex < group.getQuestionMap().size()) {

				// since, after deletions, question order may not be contiguous,
				// we need to do this to get the order index
				List<Integer> questionOrderList = new ArrayList<Integer>(group
						.getQuestionMap().keySet());
				surveyService.loadQuestionDetails(group.getQuestionMap().get(
						questionOrderList.get(questionIndex)).getKeyId(),
						new AsyncCallback<QuestionDto>() {
							@Override
							public void onFailure(Throwable caught) {
								displayErrorMessage(caught);
							}

							@Override
							public void onSuccess(QuestionDto result) {
								group.getQuestionMap().put(questionIndex + 1,
										result);
								loadQuestionDetails(groupIndex,
										questionIndex + 1);
							}
						});
			} else {
				// if questionIndex is > size then load next group's questions
				installQuestions(groupIndex);
				loadQuestionDetails(groupIndex + 1, 0);
			}
		} else {
			// at this point, all questions should be fully loaded
			loadComplete();
		}
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
		Collection<QuestionDto> questions = surveyDto.getQuestionGroupList().get(idx).getQuestionMap().values();
		for(QuestionDto q: questions){
			tabContent.add(new Label(q.getText()));
		}
	}
}
