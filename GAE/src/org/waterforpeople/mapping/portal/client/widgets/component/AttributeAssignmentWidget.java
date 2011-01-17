package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
 * Widget used to assign survey questions to access point attributes.
 * 
 *  TODO: add in the save/reset buttons
 * 
 * @author Christopher Fagiani
 *
 */
public class AttributeAssignmentWidget extends Composite implements
		ContextAware, ChangeHandler, ClickHandler {

	private static final String MAP_TARGET_OBJECT_NAME = "org.waterforpeople.mapping.domain.AccessPoint";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";

	private VerticalPanel contentPanel;
	private VerticalPanel gridPanel;

	private SurveyDto currentSurveySelection;
	private Map<String, Object> bundle;

	private CaptionPanel selectorPanel;
	private ListBox surveyGroup;
	private ListBox questionGroupListbox;
	private ListBox surveyListbox;

	private SurveyServiceAsync surveyService;
	private SurveyAttributeMappingServiceAsync mappingService;
	private Label statusLabel;

	private HashMap<String, ListBox> attributeListboxes;
	private HashMap<String, ListBox> typeListboxes;

	private TreeMap<String, String> attributes;
	private List<QuestionGroupDto> currentQuestionGroupDtoList;
	private ArrayList<QuestionDto> currentQuestionList;	
	private QuestionGroupDto currentQuestionGroupSelection;
	private List<SurveyDto> currentSurveyDtoList;
	private HashMap<String, ArrayList<SurveyDto>> surveys;

	public AttributeAssignmentWidget() {
		contentPanel = new VerticalPanel();

		selectorPanel = new CaptionPanel();
		HorizontalPanel tempPanel = new HorizontalPanel();
		statusLabel = new Label();
		contentPanel.add(statusLabel);
		surveyGroup = new ListBox();
		surveyListbox = new ListBox();
		questionGroupListbox = new ListBox();
		contentPanel.add(selectorPanel);
		addFieldPair(surveyGroup, "Survey Group", tempPanel);
		addFieldPair(surveyListbox, "Survey", tempPanel);
		addFieldPair(questionGroupListbox, "Question Group", tempPanel);
		gridPanel = new VerticalPanel();
		selectorPanel.add(tempPanel);
		mappingService = GWT.create(SurveyAttributeMappingService.class);
		surveyService = GWT.create(SurveyService.class);
		surveys = new HashMap<String, ArrayList<SurveyDto>>();
		contentPanel.add(gridPanel);
		
		loadAttributes();
		initWidget(contentPanel);

	}

	private void addFieldPair(ListBox list, String labelText, HasWidgets target) {
		Label l = new Label(labelText);
		target.add(l);
		target.add(list);
		list.addChangeHandler(this);
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		saveMapping();
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		
		this.bundle = bundle;
		currentSurveySelection = (SurveyDto) bundle
				.get(BundleConstants.SURVEY_KEY);
		renderInitialState();

	}

	protected void renderInitialState() {
		toggleLoading(true);
		loadSurveyGroups();

	}

	protected void toggleLoading(boolean isLoading) {
		if (isLoading) {
			statusLabel.setText("Loading...");
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
						if(done){
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
									"Cannot list surveys",
									"The application encountered an error: "
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
						"Please select a group",
						"You must select a survey group first");
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
		statusLabel.setText("Loading survey question groups. Please wait...");
		statusLabel.setVisible(true);
		AsyncCallback<ArrayList<QuestionGroupDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionGroupDto>>() {
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog("Cannot load survey",
						"The application encountered an error: "
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
		statusLabel.setText("Loading survey questions. Please wait...");
		statusLabel.setVisible(true);
		AsyncCallback<ArrayList<QuestionDto>> questionCallback = new AsyncCallback<ArrayList<QuestionDto>>() {
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog("Cannot load survey",
						"The application encountered an error: "
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
			typeListboxes = new HashMap<String, ListBox>();

			Grid grid = new Grid(questions.size() + 1, 3);
			// build headers
			grid.setText(0, 0, "Question Text");
			grid.setText(0, 1, "Attribute");
			grid.setText(0, 2, "Point Type");
			setGridRowStyle(grid, 0, false);

			int count = 1;

			if (questions != null) {
				for (QuestionDto q : questions) {

					grid.setWidget(count, 0, new Label(q.getText()));
					ListBox attrListbox = new ListBox();
					attrListbox.addItem("", "");
					for (Entry<String, String> entry : attributes.entrySet()) {
						String text = entry.getValue();
						if (text == null || text.trim().length() == 0) {
							text = entry.getKey();
						}
						attrListbox.addItem(text, entry.getKey());
					}
					grid.setWidget(count, 1, attrListbox);
					attributeListboxes.put(currentSurveySelection.getKeyId()
							+ ":" + q.getKeyId(), attrListbox);
					ListBox typeListbox = new ListBox(true);
					for (int i = 0; i < AccessPointType.values().length; i++) {
						String val = AccessPointType.values()[i].toString();
						typeListbox.addItem(val, val);
					}
					grid.setWidget(count, 2, typeListbox);
					typeListboxes.put(currentSurveySelection.getKeyId() + ":"
							+ q.getKeyId(), typeListbox);
					setGridRowStyle(grid, count, false);
					count++;
				}
			}

			gridPanel.add(grid);
		} else {
			gridPanel.add(new Label("No Questions"));
		}
	}

	private void loadAttributes() {
		mappingService.listObjectAttributes(MAP_TARGET_OBJECT_NAME,
				new AsyncCallback<TreeMap<String, String>>() {

					@Override
					public void onSuccess(TreeMap<String, String> result) {
						if (result != null) {
							attributes = result;
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Application Error", "Cannot load attributes");
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
				surveyListbox.addItem(survey.getName() != null ? survey
						.getName() : "Survey " + survey.getKeyId().toString(),
						survey.getKeyId().toString());
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
	private void saveMapping() {

		if (currentSurveySelection != null && currentQuestionList != null) {
			ArrayList<SurveyAttributeMappingDto> mappingDtoList = new ArrayList<SurveyAttributeMappingDto>();

			for (QuestionDto q : currentQuestionList) {
				SurveyAttributeMappingDto dto = new SurveyAttributeMappingDto();
				dto
						.setQuestionGroupId(currentQuestionGroupSelection
								.getKeyId());
				dto.setSurveyId(currentSurveySelection.getKeyId());
				dto.setSurveyQuestionId(q.getKeyId().toString());
				ListBox attrBox = findListBox(dto, attributeListboxes);
				if (attrBox != null) {
					String val = attrBox.getValue(attrBox.getSelectedIndex());
					if (val != null && val.trim().length() > 0) {

						dto.setObjectName(MAP_TARGET_OBJECT_NAME);
						dto.setAttributeName(val);
						mappingDtoList.add(dto);
					}
					ListBox typeBox = findListBox(dto, typeListboxes);
					if (typeBox != null) {
						List<String> typeList = new ArrayList<String>();
						for (int i = 0; i < typeBox.getItemCount(); i++) {
							if (typeBox.isItemSelected(i)) {
								typeList.add(typeBox.getValue(i));
							}
						}
						dto.setApTypes(typeList);
					}
				}
			}

			mappingService.saveMappings(currentQuestionGroupSelection
					.getKeyId(), mappingDtoList,
					new AsyncCallback<ArrayList<SurveyAttributeMappingDto>>() {

						@Override
						public void onSuccess(
								ArrayList<SurveyAttributeMappingDto> result) {
							statusLabel.setText("Mapping Saved");
							statusLabel.setVisible(true);
						}

						@Override
						public void onFailure(Throwable caught) {
							statusLabel.setText("Error: "
									+ caught.getLocalizedMessage());
							statusLabel.setVisible(true);
						}
					});
		}
	}

	/**
	 * handles all the button clicks for this portlet
	 */
	@Override
	public void onClick(ClickEvent event) {
	/*	if (event.getSource() == resetButton) {
			reset();
			currentSurveySelection = null;
		} else if (event.getSource() == saveButton) {
			saveMapping();
		}*/
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
			currentQuestionGroupSelection = currentQuestionGroupDtoList
					.get(questionGroupListbox.getSelectedIndex() - 1);
			loadQuestions(currentQuestionGroupSelection.getKeyId());
		} /*else if (event.getSource() == saveButton) {
			saveMapping();
		} else if (event.getSource() == resetButton) {
			reset();
		}*/
	}

	private void loadExistingMappings(final Long id) {
		mappingService.listMappingsBySurvey(id,
				new AsyncCallback<ArrayList<SurveyAttributeMappingDto>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Cannot load mappings",
								"The application encountered an error: "
										+ caught.getLocalizedMessage());
						errDia.showCentered();
					}

					@Override
					public void onSuccess(
							ArrayList<SurveyAttributeMappingDto> result) {
						if (result != null) {
							for (SurveyAttributeMappingDto dto : result) {
								ListBox box = findListBox(dto,
										attributeListboxes);
								if (box != null) {
									selectBoxItem(dto.getAttributeName(), box);
								}
								if (dto.getApTypes() != null
										&& dto.getApTypes().size() > 0) {
									ListBox typeBox = findListBox(dto,
											typeListboxes);
									if (typeBox != null) {
										for (String type : dto.getApTypes()) {
											selectBoxItem(type, typeBox);
										}
									}
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
	private ListBox findListBox(SurveyAttributeMappingDto dto,
			HashMap<String, ListBox> boxMap) {
		return boxMap.get(dto.getSurveyId() + ":" + dto.getSurveyQuestionId());
	}
}
