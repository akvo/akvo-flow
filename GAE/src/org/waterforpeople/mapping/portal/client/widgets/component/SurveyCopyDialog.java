package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box used to copy an entire survey to a new survey
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyCopyDialog extends DialogBox {
	private static final String TITLE = "Copy Survey";
	private HorizontalPanel controlPanel;
	private VerticalPanel mainPanel;
	private ListBox surveyGroupList;
	private TextBox surveyName;
	private SurveyServiceAsync surveyService;
	private CompletionListener listener;
	private Label statusLabel;
	private DockPanel contentPane;
	private SurveyDto surveyDto;
	private Button okButton;
	private Button cancelButton;
	private boolean enabled;
	private SurveyDto newSurveyDto;
	private List<QuestionDto> origQuestionsWithDeps;

	private Map<Long, Long> questionIdMap;

	/**
	 * instantiates and displays the dialog box using the survey in the dto
	 * argument as the survey to copy. Upon load, the list of destination survey
	 * groups will be displayed.
	 * 
	 * @param dto
	 * @param listener
	 */
	public SurveyCopyDialog(SurveyDto dto, CompletionListener listener) {
		setText(TITLE);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		surveyDto = dto;
		this.listener = listener;
		questionIdMap = new HashMap<Long, Long>();
		origQuestionsWithDeps = new ArrayList<QuestionDto>();
		mainPanel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		statusLabel = new Label("Loading...");
		contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		contentPane.add(statusLabel, DockPanel.CENTER);

		controlPanel = new HorizontalPanel();
		surveyGroupList = new ListBox();
		surveyName = new TextBox();
		mainPanel.add(controlPanel);
		ViewUtil.installFieldRow(controlPanel, "Destination Group",
				surveyGroupList, null);
		ViewUtil.installFieldRow(controlPanel, "New Survey name", surveyName,
				null);
		HorizontalPanel buttonPanel = new HorizontalPanel();

		okButton = new Button("Copy");
		cancelButton = new Button("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				performCopy();
			}
		});
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
		loadData();
	}

	private void loadData() {
		surveyService.listSurveyGroups(null, false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						if (result != null) {
							for (SurveyGroupDto group : result) {
								surveyGroupList.addItem(group.getDisplayName(),
										group.getKeyId().toString());
								statusLabel.removeFromParent();
								contentPane.add(mainPanel, DockPanel.CENTER);
							}
						} else {
							statusLabel.setText("No survey groups");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						statusLabel
								.setText("Could not load survey groups. Please close this dialog and try again: "
										+ caught.getLocalizedMessage());

					}
				});
	}

	/**
	 * starts the copy operation. this operation will consist of many server
	 * calls to load the selected survey's details and save the new one
	 */
	private void performCopy() {
		disableEnableAll(false);
		statusLabel.setText("Copying Survey...");
		mainPanel.add(statusLabel);
		newSurveyDto = new SurveyDto();
		newSurveyDto.setCode(surveyName.getText());
		newSurveyDto.setName(surveyName.getText());
		newSurveyDto.setDescription(surveyDto.getDescription());
		newSurveyDto.setPath(surveyGroupList.getItemText(surveyGroupList
				.getSelectedIndex()));
		newSurveyDto.setSurveyGroupId(new Long(surveyGroupList
				.getValue(surveyGroupList.getSelectedIndex())));
		surveyService.saveSurvey(newSurveyDto, newSurveyDto.getSurveyGroupId(),
				new AsyncCallback<SurveyDto>() {

					@Override
					public void onFailure(Throwable caught) {
						displayErrorMessage(caught);
					}

					@Override
					public void onSuccess(SurveyDto result) {
						newSurveyDto = result;
						if (surveyDto.getQuestionGroupList() != null
								&& surveyDto.getQuestionGroupList().size() > 0) {
							saveQuestionGroups();
						} else {
							loadQuestionGroups();
						}
					}
				});
	}

	private void loadQuestionGroups() {
		statusLabel.setText("Loading question groups to copy");
		surveyService.listQuestionGroupsBySurvey(surveyDto.getKeyId()
				.toString(), new AsyncCallback<ArrayList<QuestionGroupDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				displayErrorMessage(caught);
			}

			@Override
			public void onSuccess(ArrayList<QuestionGroupDto> result) {
				surveyDto.setQuestionGroupList(result);
				saveQuestionGroups();
			}
		});
	}

	private void saveQuestionGroups() {
		statusLabel.setText("Saving new question groups");
		if (surveyDto.getQuestionGroupList() != null) {
			List<QuestionGroupDto> dtoList = new ArrayList<QuestionGroupDto>();
			for (QuestionGroupDto existingGroup : surveyDto
					.getQuestionGroupList()) {
				QuestionGroupDto newGroup = new QuestionGroupDto();
				newGroup.setCode(existingGroup.getCode());
				newGroup.setDescription(existingGroup.getDescription());
				newGroup.setName(existingGroup.getName());
				newGroup.setOrder(existingGroup.getOrder());
				newGroup.setPath(newSurveyDto.getPath() + "/"
						+ newSurveyDto.getName());
				newGroup.setSurveyId(newSurveyDto.getKeyId());
				dtoList.add(newGroup);
			}
			surveyService.saveQuestionGroups(dtoList,
					new AsyncCallback<List<QuestionGroupDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							displayErrorMessage(caught);
						}

						@Override
						public void onSuccess(List<QuestionGroupDto> result) {
							newSurveyDto.setQuestionGroupList(result);
							loadQuestionList(0);
						}
					});
		} else {
			//if there are no questions, we're done.
			notifyListeners();
			hide();
		}
	}

	private void loadQuestionList(final int groupIndex) {
		statusLabel.setText("Loading questions to copy for group "
				+ (groupIndex + 1));
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
			// if there are no groups, we're done
			notifyListeners();
			hide();
		}
	}

	private void loadQuestionDetails(final int groupIndex,
			final int questionIndex) {
		statusLabel.setText("Loading details for group " + (groupIndex + 1)
				+ ", question " + (questionIndex + 1));
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
				loadQuestionDetails(groupIndex + 1, 0);
			}
		} else {
			// at this point, all questions should be fully loaded so we can
			// start saving them one at a time
			copyQuestion(0, 0);
		}
	}

	/**
	 * iterates over all the questions with dependencies and remaps the ids to
	 * point to the corresponding copy
	 * 
	 * @param index
	 */
	private void correctDependencies(final int index) {
		if (index < origQuestionsWithDeps.size()) {
			QuestionDependencyDto dep = origQuestionsWithDeps.get(index)
					.getQuestionDependency();
			dep.setQuestionId(questionIdMap.get(dep.getQuestionId()));
			surveyService.updateQuestionDependency(questionIdMap
					.get(origQuestionsWithDeps.get(index).getKeyId()), dep,
					new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							displayErrorMessage(caught);
						}

						@Override
						public void onSuccess(Void result) {
							correctDependencies(index + 1);

						}
					});
		} else {
			notifyListeners();
			hide();
		}
	}

	private void copyQuestion(final int groupIndex, final int questionIndex) {
		statusLabel.setText("Saving details for group " + (groupIndex + 1)
				+ ", question " + (questionIndex + 1));
		if (groupIndex < surveyDto.getQuestionGroupList().size()) {
			final QuestionGroupDto group = surveyDto.getQuestionGroupList()
					.get(groupIndex);
			if (group.getQuestionMap() != null
					&& questionIndex < group.getQuestionMap().size()) {
				final QuestionDto existingQuestion = group.getQuestionMap()
						.get(questionIndex + 1);

				surveyService.copyQuestion(existingQuestion, newSurveyDto
						.getQuestionGroupList().get(groupIndex),
						new AsyncCallback<QuestionDto>() {

							@Override
							public void onFailure(Throwable caught) {
								displayErrorMessage(caught);
							}

							@Override
							public void onSuccess(QuestionDto result) {
								questionIdMap.put(existingQuestion.getKeyId(),
										result.getKeyId());
								if (existingQuestion.getQuestionDependency() != null) {
									origQuestionsWithDeps.add(existingQuestion);
								}
								copyQuestion(groupIndex, questionIndex + 1);
							}
						});

			} else {
				// if questionIndex is > size then save next group's questions
				copyQuestion(groupIndex + 1, 0);
			}
		} else {
			correctDependencies(0);
		}
	}

	private void displayErrorMessage(Throwable caught) {
		statusLabel.setText("Could not copy survey: "
				+ caught.getLocalizedMessage());
		disableEnableAll(true);
	}

	private void disableEnableAll(boolean isEnabled) {
		enabled = isEnabled;
		cancelButton.setEnabled(isEnabled);
		okButton.setEnabled(isEnabled);
	}

	/**
	 * allow the user to press escape to close
	 */
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		if (enabled) {
			switch (key) {
			case KeyCodes.KEY_ESCAPE:
				hide();
				return true;
			}
		}
		return false;
	}

	private void notifyListeners() {
		if (listener != null) {
			listener.operationComplete(true, null);
		}
	}
}
