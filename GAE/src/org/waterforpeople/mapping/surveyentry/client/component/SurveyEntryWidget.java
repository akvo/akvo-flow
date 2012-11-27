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

package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * widget that supports survey entry and submission via a web form
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEntryWidget extends Composite implements
		QuestionAnswerListener, SelectionHandler<Integer> {

	protected static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String PAYLOAD_KEY = "payload";
	private String surveyId;
	private SurveyServiceAsync surveyService;
	private SurveyInstanceServiceAsync surveyInstanceService;
	private VerticalPanel containerPanel;
	private MessageDialog loadingDialog;
	private SurveyDto surveyDto;
	private TabPanel tabPanel;
	private Map<Long, QuestionWidget> questionWidgetMap;
	private Panel submissionPanel;
	private Map<String, QuestionAnswerStoreDto> existingAnswers;
	private Long existingInstanceId;
	private CompletionListener listener;
	private String submitter;

	public SurveyEntryWidget(String surveyId,
			List<QuestionAnswerStoreDto> answers, String submitter) {
		this.surveyId = surveyId;
		this.submitter = submitter;
		existingAnswers = new HashMap<String, QuestionAnswerStoreDto>();
		if (answers != null) {
			for (QuestionAnswerStoreDto a : answers) {
				existingAnswers.put(a.getQuestionID(), a);
				if (existingInstanceId == null
						&& a.getSurveyInstanceId() != null) {
					existingInstanceId = a.getSurveyInstanceId();
				}
			}
		}
		submissionPanel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		surveyInstanceService = GWT.create(SurveyInstanceService.class);
		containerPanel = new VerticalPanel();
		loadingDialog = new MessageDialog(TEXT_CONSTANTS.loading(),
				TEXT_CONSTANTS.pleaseWait(), true);
		initWidget(containerPanel);
	}

	public SurveyEntryWidget(String surveyId, String submitter) {
		this(surveyId, null,submitter);
	}

	public void initialize() {
		loadSurveyXml();
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
						if (surveyDto != null) {
							installTabs();
							questionWidgetMap = new HashMap<Long, QuestionWidget>();
							if (surveyDto.getQuestionGroupList() != null) {
								for (int i = 0; i < surveyDto
										.getQuestionGroupList().size(); i++) {
									installQuestions(i);
								}
							}
						} else {
							displayErrorMessage(null);
						}
						loadComplete();
					}
				});
	}

	private void displayErrorMessage(Throwable caught) {
		loadingDialog.hide();
		MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+(caught!=null?caught.getLocalizedMessage():""));				
		errDia.showCentered();
	}

	protected void loadComplete() {
		loadingDialog.hide();
	}

	protected void installTabs() {
		tabPanel = new TabPanel();
		tabPanel.addSelectionHandler(this);
		if (surveyDto != null && surveyDto.getQuestionGroupList() != null) {
			for (QuestionGroupDto group : surveyDto.getQuestionGroupList()) {
				tabPanel.add(new VerticalPanel(), group.getDisplayName());
			}
			tabPanel.add(submissionPanel, TEXT_CONSTANTS.submitReview());
			containerPanel.add(tabPanel);
			tabPanel.selectTab(0);
		}
	}

	protected void installQuestions(final int idx) {
		Panel tabContent = (Panel) tabPanel.getWidget(idx);
		Collection<QuestionDto> questions = surveyDto.getQuestionGroupList()
				.get(idx).getQuestionMap().values();
		QuestionWidgetFactory factory = new QuestionWidgetFactory();
		for (QuestionDto q : questions) {
			QuestionWidget w = factory.createQuestionWidget(q, existingAnswers
					.get(q.getKeyId().toString()), this);
			if (w != null) {
				questionWidgetMap.put(q.getKeyId(), w);
				tabContent.add(w);
				if (q.getQuestionDependency() != null && !w.isAnswered()) {
					w.setVisible(false);
				}				
			}
		}
		Button nextButton = new Button(TEXT_CONSTANTS.next());
		tabContent.add(nextButton);
		nextButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {	
				tabPanel.selectTab(idx+1);				
			}});
	}

	/**
	 * hides/shows responses based on if the dependencies are satisfied.
	 * 
	 * @param questionId
	 * @param value
	 */
	protected void updateDependencies(Long questionId, String value) {

		for (QuestionWidget w : questionWidgetMap.values()) {
			QuestionDependencyDto dep = w.getQuestion().getQuestionDependency();
			if (dep != null && dep.getQuestionId().equals(questionId)) {
				if (value != null) {
					boolean isMatch = false;
					String[] tokens = value.split("\\|");
					String[] depVals = dep.getAnswerValue().trim().split(QuestionDto.ANS_DELIM_REGEX);
					for (int i = 0; i < tokens.length; i++) {
						for(int j=0; j < depVals.length; j++){
							if(tokens[i].trim().equalsIgnoreCase(depVals[j])){
								isMatch = true;
								break;
							}
						}						
					}
					w.setVisible(isMatch);
				}
			}
		}
	}

	/**
	 * Checks that all mandatory questions are answered. if not, populates the
	 * submission panel with the list of pending questions
	 */
	private void populateSubmissionPanel() {
		submissionPanel.clear();
		boolean isApprox = false;
		final List<QuestionAnswerStoreDto> answers = new ArrayList<QuestionAnswerStoreDto>();
		List<QuestionDto> missingItems = new ArrayList<QuestionDto>();
		for (QuestionWidget w : questionWidgetMap.values()) {
			if (w.isVisible() && !w.isAnswered() && w.isMandatory()) {
				missingItems.add(w.getQuestion());
			} else if (w.isVisible()) {
				answers.add(w.getAnswer());
				if (w instanceof GeoQuestionWidget) {
					if (((GeoQuestionWidget) w).isApproximate()) {
						isApprox = true;
					}
				}
			}
		}
		if (missingItems.size() == 0) {
			String lblText = TEXT_CONSTANTS.submitSurvey();

			if (existingAnswers.size() > 0) {
				// if we already have answers, we're approving, not creating
				lblText = TEXT_CONSTANTS.saveAndApprove();
			}

			Button submitButton = new Button(lblText);
			final boolean isApproximateLocation = isApprox;

			submitButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					SurveyInstanceDto instance = new SurveyInstanceDto();
					if (isApproximateLocation) {
						instance.setApproximateLocationFlag("True");
					} else {
						instance.setApproximateLocationFlag("False");
					}
					instance.setApprovedFlag("False");
					instance.setSurveyId(new Long(surveyId));
					instance.setCollectionDate(new Date());
					instance.setDeviceIdentifier("WEB FORM");
					instance.setSubmitterName(submitter);
					instance.setQuestionAnswersStore(answers);
					
					if (existingAnswers.size() == 0) {
						surveyInstanceService.submitSurveyInstance(instance,
								new AsyncCallback<SurveyInstanceDto>() {

									@Override
									public void onFailure(Throwable caught) {
										MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());												
										errDia.showCentered();

									}

									@Override
									public void onSuccess(
											SurveyInstanceDto result) {
										MessageDialog success = new MessageDialog(TEXT_CONSTANTS.surveySubmitted(),TEXT_CONSTANTS.surveySubmittedMessage());												
										success.showCentered();
										resetForm();
										if (listener != null) {
											Map<String, Object> payload = new HashMap<String, Object>();
											payload.put(PAYLOAD_KEY, result);
											listener.operationComplete(true,
													payload);
										}

									}
								});
					} else {
						// we only want to send in the changed questions in this
						// case, so filter them
						List<QuestionAnswerStoreDto> changeQList = new ArrayList<QuestionAnswerStoreDto>();
						for (QuestionAnswerStoreDto newQ : answers) {
							QuestionAnswerStoreDto existingAns = existingAnswers
									.get(newQ.getQuestionID());
							if (existingAns != null) {
								if (newQ.getValue() == null
										&& existingAns.getValue() != null) {
									changeQList.add(newQ);

								} else if (newQ.getValue() != null
										&& existingAns.getValue() == null) {
									changeQList.add(newQ);
								} else if (!newQ.getValue().equals(
										existingAns.getValue())) {
									changeQList.add(newQ);
								}
							} else {
								changeQList.add(newQ);
							}
						}

						surveyInstanceService.approveSurveyInstance(
								existingInstanceId, changeQList,
								new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());
										errDia.showCentered();

									}

									@Override
									public void onSuccess(Void v) {
										MessageDialog success = new MessageDialog(TEXT_CONSTANTS.surveyApproved(),TEXT_CONSTANTS.surveyApprovedMessage());
										success.showCentered();
										resetForm();
									}
								});
					}
				}
			});
			submissionPanel.add(submitButton);
		} else {
			submissionPanel
					.add(new Label(TEXT_CONSTANTS.pleaseAnswerMandatory()));
			for (QuestionDto q : missingItems) {
				submissionPanel.add(new Label(q.getText()));
			}
		}
	}

	private void resetForm() {
		for (QuestionWidget w : questionWidgetMap.values()) {
			w.reset();
		}
		existingAnswers.clear();
		tabPanel.getTabBar().selectTab(0);
	}

	@Override
	public void answerUpdated(Long questionId, String value) {
		updateDependencies(questionId, value);
	}

	@Override
	public void onSelection(SelectionEvent<Integer> event) {
		if (tabPanel.getTabBar().getSelectedTab() == tabPanel.getTabBar()
				.getTabCount() - 1) {
			populateSubmissionPanel();
		}
	}

	public void setListener(CompletionListener l) {
		listener = l;
	}
}
