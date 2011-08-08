package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget used to assign Survey Questions to metrics
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricMappingWidget extends Composite implements ContextAware,
		ChangeHandler {

	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private VerticalPanel contentPanel;
	private VerticalPanel gridPanel;

	private SurveyDto currentSurveySelection;
	private Map<String, Object> bundle;

	private CaptionPanel selectorPanel;
	private ListBox surveyGroup;
	private ListBox questionGroupListbox;
	private ListBox surveyListbox;

	private SurveyServiceAsync surveyService;
	private MetricServiceAsync metricService;

	private Label statusLabel;

	private HashMap<String, ListBox> attributeListboxes;

	private List<MetricDto> metrics;
	private List<QuestionGroupDto> currentQuestionGroupDtoList;
	private ArrayList<QuestionDto> currentQuestionList;
	private QuestionGroupDto currentQuestionGroupSelection;
	private List<SurveyDto> currentSurveyDtoList;
	private HashMap<String, ArrayList<SurveyDto>> surveys;
	private SurveyMetricMappingServiceAsync mappingService;

	public MetricMappingWidget() {
		contentPanel = new VerticalPanel();

		selectorPanel = new CaptionPanel();
		HorizontalPanel tempPanel = new HorizontalPanel();
		statusLabel = new Label();
		contentPanel.add(statusLabel);
		surveyGroup = new ListBox();
		surveyListbox = new ListBox();
		questionGroupListbox = new ListBox();
		contentPanel.add(selectorPanel);
		addFieldPair(surveyGroup, TEXT_CONSTANTS.surveyGroup(), tempPanel);
		addFieldPair(surveyListbox, TEXT_CONSTANTS.survey(), tempPanel);
		addFieldPair(questionGroupListbox, TEXT_CONSTANTS.questionGroup(),
				tempPanel);
		gridPanel = new VerticalPanel();
		selectorPanel.add(tempPanel);
		mappingService = GWT.create(SurveyMetricMappingService.class);
		surveyService = GWT.create(SurveyService.class);
		metricService = GWT.create(MetricService.class);
		surveys = new HashMap<String, ArrayList<SurveyDto>>();
		contentPanel.add(gridPanel);

		loadMetrics();
		initWidget(contentPanel);

	}

	private void addFieldPair(ListBox list, String labelText, HasWidgets target) {
		Label l = new Label(labelText);
		target.add(l);
		target.add(list);
		list.addChangeHandler(this);
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		return bundle;
	}

	@Override
	public void flushContext() {
		// no-op
	}

	@Override
	public void persistContext(String buttonText,CompletionListener listener) {
		saveMapping(false, listener);
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentSurveySelection = (SurveyDto) bundle
				.get(BundleConstants.SURVEY_KEY);
		renderInitialState();

	}

	public void renderInitialState() {
		toggleLoading(true);
		loadSurveyGroups();

	}

	protected void toggleLoading(boolean isLoading) {
		if (isLoading) {
			statusLabel.setText(TEXT_CONSTANTS.loading());
			statusLabel.setVisible(true);
		} else {
			statusLabel.setVisible(false);
		}

	}

	/**
	 * loads the survey gruops
	 */
	private void loadSurveyGroups() {
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS
								.couldNotLoadSurveyGroups());
						errDia.showCentered();
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyGroupDto>> response) {
						ArrayList<SurveyGroupDto> result = response
								.getPayload();
						surveyGroup.addItem("", "");
						boolean done = true;
						if (result != null) {
							int i = 0;
							for (SurveyGroupDto dto : result) {
								surveyGroup.addItem(dto.getCode(), dto
										.getKeyId().toString());
								if (currentSurveySelection != null
										&& dto.getKeyId().equals(
												currentSurveySelection
														.getSurveyGroupId())) {
									surveyGroup.setSelectedIndex(i);
									done = false;
									getSurveys();
								}
								i++;
							}
						}
						if (done) {
							toggleLoading(false);
						}
					}
				});
	}

	private void getSurveys() {
		if (surveyGroup.getSelectedIndex() > 0) {
			final String selectedGroupId = surveyGroup.getValue(surveyGroup
					.getSelectedIndex());

			if (selectedGroupId != null) {
				if (surveys.get(selectedGroupId) != null) {
					populateSurveyList(surveys.get(selectedGroupId));
				} else {
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
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
							}
						}
					};
					surveyService.listSurveysByGroup(selectedGroupId,
							surveyCallback);
				}
			} else {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.mustSelectSurveyGroup());
				errDia.showCentered();
			}
		} else {
			surveyListbox.clear();
		}
	}

	/**
	 * calls the service to load the survey question groups
	 * 
	 * @param id
	 * @return
	 */
	private void loadSurveyQuestionGroups(final Long id) {
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		AsyncCallback<ArrayList<QuestionGroupDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionGroupDto>>() {
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();
			}

			public void onSuccess(ArrayList<QuestionGroupDto> result) {
				statusLabel.setVisible(false);
				if (result != null) {
					currentQuestionGroupDtoList = result;
					populateQuestionGroupList(result);
				}
			}
		};
		surveyService.listQuestionGroupsBySurvey(id.toString(), surveyCallback);
	}

	private void loadQuestions(final Long groupId) {
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		AsyncCallback<ArrayList<QuestionDto>> questionCallback = new AsyncCallback<ArrayList<QuestionDto>>() {
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();
			}

			public void onSuccess(ArrayList<QuestionDto> result) {
				statusLabel.setVisible(false);
				if (result != null) {
					currentQuestionList = result;
					loadExistingMappings(currentSurveySelection.getKeyId());
					updateDataGrid(result);
				}
			}
		};
		surveyService.listQuestionsByQuestionGroup(groupId.toString(), false,
				questionCallback);

	}

	private void updateDataGrid(ArrayList<QuestionDto> questions) {
		gridPanel.clear();
		if (questions != null) {

			attributeListboxes = new HashMap<String, ListBox>();
			Grid grid = new Grid(questions.size() + 1, 2);
			// build headers
			grid.setText(0, 0, TEXT_CONSTANTS.questionText());
			grid.setText(0, 1, TEXT_CONSTANTS.metric());
			setGridRowStyle(grid, 0, false);

			int count = 1;

			if (questions != null) {
				for (QuestionDto q : questions) {

					grid.setWidget(count, 0, new Label(q.getText()));
					ListBox attrListbox = new ListBox();
					attrListbox.addItem("", "");
					for (MetricDto metric : metrics) {
						attrListbox.addItem(metric.getName(), metric.getKeyId()
								.toString());
					}
					grid.setWidget(count, 1, attrListbox);
					attributeListboxes.put(currentSurveySelection.getKeyId()
							+ ":" + q.getKeyId(), attrListbox);
					setGridRowStyle(grid, count, false);
					count++;
				}
			}

			gridPanel.add(grid);
		} else {
			gridPanel.add(new Label(TEXT_CONSTANTS.noQuestions()));
		}
	}

	private void loadMetrics() {
		// TODO: must send in organization here
		metricService.listMetrics(null, null, null, null, "all",
				new AsyncCallback<ResponseDto<ArrayList<MetricDto>>>() {

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<MetricDto>> result) {
						if (result != null && result.getPayload() != null) {
							metrics = result.getPayload();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();

					}
				});
	}

	private void populateSurveyList(ArrayList<SurveyDto> surveyItems) {
		surveyListbox.clear();
		currentSurveyDtoList = surveyItems;
		if (surveyItems != null) {
			surveyListbox.addItem("", "");
			int i = 0;
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(
						survey.getName() != null ? survey.getName() : "Survey "
								+ survey.getKeyId().toString(), survey
								.getKeyId().toString());
				if (currentSurveySelection != null
						&& survey.getKeyId().equals(
								currentSurveySelection.getKeyId())) {
					surveyListbox.setSelectedIndex(i);
				}
				i++;
			}
		}
	}

	private void populateQuestionGroupList(ArrayList<QuestionGroupDto> groups) {
		questionGroupListbox.clear();
		currentQuestionGroupDtoList = groups;
		if (groups != null) {
			questionGroupListbox.addItem("", "");
			for (QuestionGroupDto group : groups) {
				questionGroupListbox.addItem(group.getDisplayName(), group
						.getKeyId().toString());
			}
		}
	}

	/**
	 * sets the css for a row in a grid. the top row will get the header style
	 * and other rows get either the even or odd style. If selected is true and
	 * row > 0 then it will set the selected style
	 * 
	 * @param grid
	 * @param row
	 * @param selected
	 */
	private void setGridRowStyle(Grid grid, int row, boolean selected) {
		// if we already had a selection, unselect it
		String style = "";
		if (selected) {
			style = SELECTED_ROW_CSS;
		} else {
			if (row > 0) {
				if (row % 2 == 0) {
					style = EVEN_ROW_CSS;
				} else {
					style = ODD_ROW_CSS;
				}
			} else {
				style = GRID_HEADER_CSS;
			}
		}
		for (int i = 0; i < grid.getColumnCount(); i++) {
			grid.getCellFormatter().setStyleName(row, i, style);
		}
	}

	/**
	 * populates a dto using the values currently set in the UI widgets and
	 * persists it to the server
	 */
	private void saveMapping(final boolean isGroupChange,
			final CompletionListener listener) {

		if (currentSurveySelection != null && currentQuestionList != null) {
			ArrayList<SurveyMetricMappingDto> mappingDtoList = new ArrayList<SurveyMetricMappingDto>();

			for (QuestionDto q : currentQuestionList) {
				SurveyMetricMappingDto dto = new SurveyMetricMappingDto();
				dto.setQuestionGroupId(currentQuestionGroupSelection.getKeyId());
				dto.setSurveyId(currentSurveySelection.getKeyId());
				dto.setSurveyQuestionId(q.getKeyId());
				ListBox attrBox = findListBox(dto, attributeListboxes);
				if (attrBox != null) {
					String val = attrBox.getValue(attrBox.getSelectedIndex());
					if (val != null && val.trim().length() > 0) {
						dto.setMetricId(new Long(val));
						mappingDtoList.add(dto);
					}
				}
			}

			mappingService.saveMappings(
					currentQuestionGroupSelection.getKeyId(), mappingDtoList,
					new AsyncCallback<List<SurveyMetricMappingDto>>() {

						@Override
						public void onSuccess(
								List<SurveyMetricMappingDto> result) {
							statusLabel.setText(TEXT_CONSTANTS.saveComplete());
							statusLabel.setVisible(true);
							if (isGroupChange) {
								currentQuestionGroupSelection = currentQuestionGroupDtoList
										.get(questionGroupListbox
												.getSelectedIndex() - 1);
								loadQuestions(currentQuestionGroupSelection
										.getKeyId());
							}
							if (listener != null) {
								listener.operationComplete(true,
										getContextBundle(true));
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							statusLabel.setText(TEXT_CONSTANTS
									.errorTracePrefix()
									+ " "
									+ caught.getLocalizedMessage());
							statusLabel.setVisible(true);
						}
					});
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroup) {
			getSurveys();
		} else if (event.getSource() == surveyListbox) {
			currentSurveySelection = currentSurveyDtoList.get(surveyListbox
					.getSelectedIndex() - 1);
			loadSurveyQuestionGroups(currentSurveySelection.getKeyId());
		} else if (event.getSource() == questionGroupListbox) {
			if (currentQuestionGroupSelection != null) {
				saveMapping(true, null);
			} else {
				currentQuestionGroupSelection = currentQuestionGroupDtoList
						.get(questionGroupListbox.getSelectedIndex() - 1);
				loadQuestions(currentQuestionGroupSelection.getKeyId());
			}

		} /*
		 * else if (event.getSource() == saveButton) { saveMapping(); } else if
		 * (event.getSource() == resetButton) { reset(); }
		 */
	}

	private void loadExistingMappings(final Long id) {
		mappingService.listMappingsBySurvey(id,
				new AsyncCallback<List<SurveyMetricMappingDto>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();
					}

					@Override
					public void onSuccess(List<SurveyMetricMappingDto> result) {
						if (result != null) {
							for (SurveyMetricMappingDto dto : result) {
								ListBox box = findListBox(dto,
										attributeListboxes);
								if (box != null) {
									selectBoxItem(dto.getMetricId().toString(),
											box);
								}

							}
						}
					}
				});
	}

	/**
	 * iterates over contents of a list box and selects the item in it if it
	 * matches the value
	 * 
	 * @param val
	 * @param box
	 */
	private void selectBoxItem(String val, ListBox box) {
		for (int i = 0; i < box.getItemCount(); i++) {
			if (box.getValue(i) != null && box.getValue(i).equals(val)) {
				box.setItemSelected(i, true);
				break;
			}
		}
	}

	/**
	 * finds a list box in the map passed in using the composite key formed from
	 * the dto
	 * 
	 * @param dto
	 * @param boxMap
	 * @return
	 */
	private ListBox findListBox(SurveyMetricMappingDto dto,
			HashMap<String, ListBox> boxMap) {
		return boxMap.get(dto.getSurveyId() + ":" + dto.getSurveyQuestionId());
	}
}
