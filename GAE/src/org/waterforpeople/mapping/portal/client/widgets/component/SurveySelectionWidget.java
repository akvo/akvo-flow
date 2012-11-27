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

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Reusable widget for selecting surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySelectionWidget extends Composite implements ChangeHandler {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private ListBox surveyGroupListbox;
	private ListBox surveyListbox;
	private ListBox questionGroupListbox;
	private ListBox questionListbox;
	private SurveyServiceAsync surveyService;
	private Panel contentPanel;
	private MessageDialog loadingDialog;
	private TerminalType termType;
	private Long pendingId;

	private Map<String, List<SurveyDto>> surveys;
	private Map<String, List<QuestionGroupDto>> questionGroups;
	private Map<String, List<QuestionDto>> questions;

	public enum Orientation {
		VERTICAL, HORIZONTAL
	};

	public enum TerminalType {
		SURVEY, QUESTIONGROUP, QUESTION
	};

	public enum SelectionMode {
		MULTI, SINGLE
	}

	public SurveySelectionWidget(Orientation orient, TerminalType type,
			SelectionMode mode) {
		termType = type;
		surveys = new HashMap<String, List<SurveyDto>>();
		questionGroups = new HashMap<String, List<QuestionGroupDto>>();
		questions = new HashMap<String, List<QuestionDto>>();

		loadingDialog = new MessageDialog(TEXT_CONSTANTS.loading(),
				TEXT_CONSTANTS.pleaseWait(), true);
		surveyService = GWT.create(SurveyService.class);
		surveyGroupListbox = new ListBox();
		surveyGroupListbox.addChangeHandler(this);
		questionListbox = new ListBox();
		questionListbox.addChangeHandler(this);
		if (TerminalType.SURVEY == type) {
			if (SelectionMode.MULTI == mode) {
				surveyListbox = new ListBox(true);
				surveyListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
			} else {
				surveyListbox = new ListBox();
			}
		} else {
			surveyListbox = new ListBox();
			surveyListbox.addChangeHandler(this);
			questionGroupListbox = new ListBox();
		}
		if (TerminalType.QUESTION == type) {
			questionGroupListbox.addChangeHandler(this);
		}
		if (Orientation.HORIZONTAL == orient) {
			contentPanel = new HorizontalPanel();
		} else {
			contentPanel = new VerticalPanel();
		}
		ViewUtil.installFieldRow(contentPanel, TEXT_CONSTANTS.surveyGroup(),
				surveyGroupListbox, LABEL_STYLE);
		ViewUtil.installFieldRow(contentPanel, TEXT_CONSTANTS.survey(),
				surveyListbox, LABEL_STYLE);
		if (TerminalType.QUESTIONGROUP == type || TerminalType.QUESTION == type) {
			ViewUtil.installFieldRow(contentPanel,
					TEXT_CONSTANTS.questionGroup(), questionGroupListbox,
					LABEL_STYLE);
		}
		if (TerminalType.QUESTION == type) {
			ViewUtil.installFieldRow(contentPanel, TEXT_CONSTANTS.question(),
					questionListbox, LABEL_STYLE);
		}
		initWidget(contentPanel);
		loadSurveyGroups();
	}

	/**
	 * initializes the survey selector such that it allows multiple selection in
	 * the terminal selector
	 * 
	 * @param orient
	 * @param type
	 */
	public SurveySelectionWidget(Orientation orient, TerminalType type) {
		this(orient, type, SelectionMode.MULTI);
	}

	/**
	 * loads the survey groups
	 */
	private void loadSurveyGroups() {
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyGroupDto>> response) {
						ArrayList<SurveyGroupDto> result = response
								.getPayload();
						surveyGroupListbox.addItem("", "");
						if (result != null) {
							int i = 0;
							for (SurveyGroupDto dto : result) {
								surveyGroupListbox.addItem(dto.getCode(), dto
										.getKeyId().toString());
								i++;
							}
						}
						toggleLoading(false);
					}
				});
	}

	private void getSurveys() {
		if (surveyGroupListbox.getSelectedIndex() > 0) {
			final String selectedGroupId = surveyGroupListbox
					.getValue(surveyGroupListbox.getSelectedIndex());

			if (selectedGroupId != null) {
				if (surveys.get(selectedGroupId) != null) {
					populateSurveyList(surveys.get(selectedGroupId));
				} else {
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
							toggleLoading(false);
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(),
									TEXT_CONSTANTS.errorTracePrefix() + " "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							if (result != null) {
								surveys.put(selectedGroupId, result);
								populateSurveyList(result);
								setSelectedSurvey(pendingId);
								toggleLoading(false);
							}
						}
					};
					toggleLoading(true);
					surveyService.listSurveysByGroup(selectedGroupId,
							surveyCallback);
				}
			} else {
				toggleLoading(false);
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.selectGroupFirst());
				errDia.showCentered();
			}
		} else {
			surveyListbox.clear();
		}
	}

	/**
	 * shows/hides the loading dialog box
	 * 
	 * @param show
	 */
	private void toggleLoading(boolean show) {
		if (!show) {
			loadingDialog.hide();
		} else {
			loadingDialog.showCentered();
		}
	}

	private void populateSurveyList(List<SurveyDto> surveyItems) {
		surveyListbox.clear();
		if (surveyItems != null) {
			int i = 0;
			surveyListbox.addItem("", "");
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(
						survey.getName() != null ? survey.getName()
								: TEXT_CONSTANTS.survey() + " "
										+ survey.getKeyId().toString(), survey
								.getKeyId().toString());

				i++;
			}
		}
	}

	/**
	 * calls the service to load the survey question groups
	 * 
	 * @param id
	 * @return
	 */
	private void loadSurveyQuestionGroups() {

		final String selectedId = ViewUtil.getListBoxSelection(surveyListbox,
				true);
		if (selectedId != null) {
			if (questionGroups.get(selectedId) == null) {
				toggleLoading(true);
				AsyncCallback<ArrayList<QuestionGroupDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionGroupDto>>() {
					public void onFailure(Throwable caught) {
						toggleLoading(false);
						MessageDialog errDia = new MessageDialog(
								TEXT_CONSTANTS.error(),
								TEXT_CONSTANTS.errorTracePrefix() + " "
										+ caught.getLocalizedMessage());
						errDia.showCentered();
					}

					public void onSuccess(ArrayList<QuestionGroupDto> result) {
						toggleLoading(false);
						if (result != null) {
							questionGroups.put(selectedId, result);
							populateQuestionGroupList(result);
						}
					}
				};
				surveyService.listQuestionGroupsBySurvey(selectedId,
						surveyCallback);
			} else {
				populateQuestionGroupList(questionGroups.get(selectedId));
			}
		}

	}

	private void loadQuestions() {
		final String groupId = ViewUtil.getListBoxSelection(
				questionGroupListbox, true);
		if (groupId != null) {
			if (questions.get(groupId) == null) {
				toggleLoading(true);
				AsyncCallback<ArrayList<QuestionDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionDto>>() {
					public void onFailure(Throwable caught) {
						toggleLoading(false);
						MessageDialog errDia = new MessageDialog(
								TEXT_CONSTANTS.error(),
								TEXT_CONSTANTS.errorTracePrefix() + " "
										+ caught.getLocalizedMessage());
						errDia.showCentered();
					}

					public void onSuccess(ArrayList<QuestionDto> result) {
						toggleLoading(false);
						if (result != null) {
							questions.put(groupId, result);
							populateQuestionList(result);
						}
					}
				};
				surveyService.listQuestionsByQuestionGroup(groupId, false,false,
						surveyCallback);
			} else {
				populateQuestionList(questions.get(groupId));
			}
		}
	}

	private void populateQuestionList(List<QuestionDto> questions) {
		questionListbox.clear();
		if (questions != null) {
			questionListbox.addItem("", "");
			for (QuestionDto question : questions) {
				questionListbox.addItem(
						question.getOrder() + ": " + question.getText(),
						question.getKeyId().toString());
			}
		}
	}

	private void populateQuestionGroupList(List<QuestionGroupDto> groups) {
		questionGroupListbox.clear();
		if (groups != null) {
			questionGroupListbox.addItem("", "");
			for (QuestionGroupDto group : groups) {
				questionGroupListbox.addItem(group.getDisplayName(), group
						.getKeyId().toString());
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroupListbox) {
			getSurveys();
		} else if ((TerminalType.QUESTIONGROUP == termType || TerminalType.QUESTION == termType)
				&& event.getSource() == surveyListbox) {
			loadSurveyQuestionGroups();
		} else if (TerminalType.QUESTION == termType
				&& event.getSource() == questionGroupListbox) {
			loadQuestions();
		}
	}

	public void reset() {
		surveyListbox.clear();
		surveyGroupListbox.setSelectedIndex(0);
		if (questionGroupListbox != null) {
			questionGroupListbox.clear();
		}
	}

	public Long getSelectedQuestionId() {
		String val =ViewUtil.getListBoxSelection(questionListbox, true);
		if(val != null){
			return new Long(val);
		}else{
			return null;
		}
	}

	public String getSelectedSurveyGroupName() {
		if (surveyGroupListbox.getSelectedIndex() >= 0) {
			return surveyGroupListbox.getItemText(surveyGroupListbox
					.getSelectedIndex());
		} else {
			return null;
		}
	}

	public List<String> getSelectedSurveyNames() {
		List<String> nameList = new ArrayList<String>();
		for (int i = 1; i < surveyListbox.getItemCount(); i++) {
			if (surveyListbox.isItemSelected(i)) {
				nameList.add(surveyListbox.getItemText(i));
			}
		}
		return nameList;
	}

	public List<String> getSelectedQuestionGroupNames() {
		List<String> nameList = new ArrayList<String>();
		if (questionGroupListbox != null) {
			for (int i = 1; i < questionGroupListbox.getItemCount(); i++) {
				if (questionGroupListbox.isItemSelected(i)) {
					nameList.add(questionGroupListbox.getItemText(i));
				}
			}
		}
		return nameList;
	}

	public Long getSelectedSurveyGroupId() {
		if (surveyGroupListbox.getSelectedIndex() >= 0) {
			return new Long(surveyGroupListbox.getValue(surveyGroupListbox
					.getSelectedIndex()));
		} else {
			return null;
		}
	}

	public List<Long> getSelectedSurveyIds() {
		List<Long> idList = new ArrayList<Long>();
		if (surveyListbox != null) {
			for (int i = 1; i < surveyListbox.getItemCount(); i++) {
				if (surveyListbox.isItemSelected(i)) {
					idList.add(new Long(surveyListbox.getValue(i)));
				}
			}
		}
		return idList;
	}

	public List<Long> getSelectedQuestionGroupIds() {
		List<Long> idList = new ArrayList<Long>();
		if (questionGroupListbox != null) {
			for (int i = 0; i < questionGroupListbox.getItemCount(); i++) {
				if (questionGroupListbox.isItemSelected(i)) {
					idList.add(new Long(questionGroupListbox.getValue(i)));
				}
			}
		}
		return idList;
	}

	/**
	 * sets the state of the widget to show the surveyID passed in as selected
	 * (loading the required surveys as needed)
	 * 
	 * @param surveyId
	 */
	public void setSelectedSurvey(final Long surveyId) {
		pendingId = null;
		if (surveyId != null) {
			boolean found = false;

			for (int i = 0; i < surveyListbox.getItemCount(); i++) {
				if (surveyListbox.getValue(i).equals(surveyId.toString())) {
					surveyListbox.setSelectedIndex(i);
					found = true;
				}
			}
			if (!found) {
				toggleLoading(true);
				// if it isn't in the box, it must belong to another group. Find
				// it.
				surveyService.findSurvey(surveyId,
						new AsyncCallback<SurveyDto>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}

							@Override
							public void onSuccess(SurveyDto result) {
								if (result != null) {
									setSelectedSurveyGroup(result
											.getSurveyGroupId());
									pendingId = surveyId;
									getSurveys();
								}
							}
						});
			}
		}
	}

	private void setSelectedSurveyGroup(Long id) {
		if (id != null) {
			for (int i = 0; i < surveyGroupListbox.getItemCount(); i++) {
				if (surveyGroupListbox.getValue(i).equals(id.toString())) {
					surveyGroupListbox.setSelectedIndex(i);
					break;
				}
			}
		}
	}
}
