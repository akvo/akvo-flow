package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

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

	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private ListBox surveyGroupListbox;
	private ListBox surveyListbox;
	private ListBox selectionListbox;
	private ListBox questionGroupListbox;
	private SurveyServiceAsync surveyService;
	private Panel contentPanel;
	private MessageDialog loadingDialog;
	private TerminalType termType;

	private Map<String, List<SurveyDto>> surveys;
	private Map<String, List<QuestionGroupDto>> questionGroups;

	public enum Orientation {
		VERTICAL, HORIZONTAL
	};

	public enum TerminalType {
		SURVEY, QUESTIONGROUP
	};

	public SurveySelectionWidget(Orientation orient, TerminalType type) {
		termType = type;
		surveys = new HashMap<String, List<SurveyDto>>();
		questionGroups = new HashMap<String, List<QuestionGroupDto>>();

		loadingDialog = new MessageDialog("Loading...", "Please wait.");
		surveyService = GWT.create(SurveyService.class);
		surveyGroupListbox = new ListBox();
		surveyGroupListbox.addChangeHandler(this);
		if (TerminalType.SURVEY == type) {
			surveyListbox = new ListBox(true);
			surveyListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		} else {
			surveyListbox = new ListBox();
			questionGroupListbox = new ListBox();
		}
		if (Orientation.HORIZONTAL == orient) {
			contentPanel = new HorizontalPanel();
		} else {
			contentPanel = new VerticalPanel();
		}
		ViewUtil.installFieldRow(contentPanel, "Survey Group",
				surveyGroupListbox, LABEL_STYLE);
		ViewUtil.installFieldRow(contentPanel, "Surveys", surveyListbox,
				LABEL_STYLE);
		if (TerminalType.QUESTIONGROUP == type) {
			ViewUtil.installFieldRow(contentPanel, "Question Groups",
					questionGroupListbox, LABEL_STYLE);
		}
		initWidget(contentPanel);
		loadSurveyGroups();
	}

	/**
	 * loads the survey groups
	 */
	private void loadSurveyGroups() {
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Application Error",
								"Cannot load survey groups");
						errDia.showCentered();
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
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
									"Cannot list surveys",
									"The application encountered an error: "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							if (result != null) {
								surveys.put(selectedGroupId, result);
								populateSurveyList(result);
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
						"Please select a group",
						"You must select a survey group first");
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
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(survey.getName() != null ? survey
						.getName() : "Survey " + survey.getKeyId().toString(),
						survey.getKeyId().toString());

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
		if (surveyListbox.getSelectedIndex() > 0) {
			final String selectedId = surveyListbox.getValue(surveyListbox
					.getSelectedIndex());
			if (selectedId != null) {
				if (questionGroups.get(selectedId) == null) {
					toggleLoading(true);
					AsyncCallback<ArrayList<QuestionGroupDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionGroupDto>>() {
						public void onFailure(Throwable caught) {
							toggleLoading(false);
							MessageDialog errDia = new MessageDialog(
									"Cannot load survey",
									"The application encountered an error: "
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
		} else if (TerminalType.QUESTIONGROUP == termType
				&& event.getSource() == surveyListbox) {
			loadSurveyQuestionGroups();
		}
	}

	public void reset() {
		selectionListbox.clear();
		surveyListbox.clear();
		surveyGroupListbox.setSelectedIndex(0);
		if (questionGroupListbox != null) {
			questionGroupListbox.clear();
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
		for (int i = 0; i < surveyListbox.getItemCount(); i++) {
			if (surveyListbox.isItemSelected(i)) {
				nameList.add(surveyListbox.getItemText(i));
			}
		}
		return nameList;
	}

	public List<String> getSelectedQuestionGroupNames() {
		List<String> nameList = new ArrayList<String>();
		if (questionGroupListbox != null) {
			for (int i = 0; i < questionGroupListbox.getItemCount(); i++) {
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
			for (int i = 0; i < surveyListbox.getItemCount(); i++) {
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

}
