package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * portlet that can be used to map a survey question to a access point attribute
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAttributeMappingPortlet extends Portlet implements
		ChangeHandler, ClickHandler {
	public static final String NAME = "Survey Attribute Mapping Portlet";
	public static final String DESCRIPTION = "Maps survey questions to Access Point attributes";
	private static final String MAP_TARGET_OBJECT_NAME = "org.waterforpeople.mapping.domain.AccessPoint";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final int HEIGHT = 800;
	private static final int WIDTH = 800;

	private ListBox surveyGroup;
	private ListBox surveyListbox;

	private HashMap<String, ListBox> attributeListboxes;

	private TreeMap<String, String> attributes;

	private DockPanel contentPanel;

	private SurveyServiceAsync surveyService;
	private SurveyAttributeMappingServiceAsync mappingService;

	private Button saveButton;
	private Button resetButton;
	private Label statusLabel;

	private DockPanel inputPanel;
	private VerticalPanel gridPanel;
	private ArrayList<SurveyDto> currentDtoList;
	private SurveyDto currentSelection = null;

	private HashMap<String, ArrayList<SurveyDto>> surveys;

	public SurveyAttributeMappingPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		surveys = new HashMap<String, ArrayList<SurveyDto>>();
		inputPanel = new DockPanel();
		contentPanel = new DockPanel();
		inputPanel = new DockPanel();
		statusLabel = new Label();
		statusLabel.setVisible(false);
		inputPanel.add(statusLabel, DockPanel.NORTH);
		inputPanel.add(createInputControls(), DockPanel.NORTH);

		mappingService = GWT.create(SurveyAttributeMappingService.class);
		surveyService = GWT.create(SurveyService.class);
		HorizontalPanel treeHost = new HorizontalPanel();

		inputPanel.add(treeHost, DockPanel.CENTER);

		resetButton = new Button("Clear");
		resetButton.addClickHandler(this);

		saveButton = new Button("Save");
		saveButton.addClickHandler(this);

		HorizontalPanel inputButtonPanel = new HorizontalPanel();
		inputButtonPanel.add(resetButton);
		inputButtonPanel.add(saveButton);
		inputPanel.add(inputButtonPanel, DockPanel.SOUTH);
		contentPanel.add(inputPanel, DockPanel.NORTH);
		gridPanel = new VerticalPanel();
		contentPanel.add(gridPanel, DockPanel.CENTER);
		setContent(contentPanel);

		loadAttributes();
		loadSurveyGroups();

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
						errDia.showRelativeTo(saveButton);

					}
				});
	}

	private void loadSurveyGroups() {
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Application Error",
								"Cannot load survey groups");
						errDia.showRelativeTo(saveButton);
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						surveyGroup.addItem("", "");
						if (result != null) {
							for (SurveyGroupDto dto : result) {
								surveyGroup.addItem(dto.getCode(), dto
										.getKeyId().toString());
							}
						}
					}
				});
	}

	private void updateDataGrid(SurveyDto survey) {
		gridPanel.clear();
		if (survey != null) {

			attributeListboxes = new HashMap<String, ListBox>();
			ArrayList<QuestionDto> allQuestions = new ArrayList<QuestionDto>();
			for (QuestionGroupDto qGroup : survey.getQuestionGroupList()) {
				allQuestions.addAll(qGroup.getQuestionMap().values());
			}

			Grid grid = new Grid(allQuestions.size() + 1, 3);
			// build headers
			grid.setText(0, 0, "Question Group");
			grid.setText(0, 1, "Question Text");
			grid.setText(0, 2, "Attribute");
			setGridRowStyle(grid, 0, false);
			if (survey.getQuestionGroupList() != null) {
				int count = 1;
				for (QuestionGroupDto qGroup : survey.getQuestionGroupList()) {
					if (qGroup.getQuestionMap() != null) {
						for (QuestionDto q : qGroup.getQuestionMap().values()) {
							grid.setWidget(count, 0,
									new Label(qGroup.getCode()));
							grid.setWidget(count, 1, new Label(q.getText()));
							ListBox attrListbox = new ListBox();
							attrListbox.addItem("", "");
							for (Entry<String, String> entry : attributes
									.entrySet()) {
								String text = entry.getValue();
								if (text == null || text.trim().length() == 0) {
									text = entry.getKey();
								}
								attrListbox.addItem(text, entry.getKey());
							}
							grid.setWidget(count, 2, attrListbox);
							attributeListboxes.put(currentSelection.getKeyId()
									+ ":" + q.getKeyId(), attrListbox);
							setGridRowStyle(grid, count, false);
							count++;
						}
					}
				}
			}
			gridPanel.add(grid);
		} else {
			gridPanel.add(new Label("No Questions"));
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

	private void reset() {
		surveyGroup.setSelectedIndex(0);
		surveyListbox.setSelectedIndex(0);
		surveyListbox.clear();
		gridPanel.clear();
		currentSelection = null;
		statusLabel.setVisible(false);

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
							errDia.showRelativeTo(saveButton);
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
				errDia.showRelativeTo(saveButton);
			}
		} else {
			surveyListbox.clear();
		}
	}

	/**
	 * calls the service to load the fully hydrated survey (including questions)
	 * 
	 * @param id
	 * @return
	 */
	private void loadFullSurvey(final Long id) {
		statusLabel.setText("Loading survey. Please wait...");
		statusLabel.setVisible(true);
		AsyncCallback<SurveyDto> surveyCallback = new AsyncCallback<SurveyDto>() {
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog("Cannot load survey",
						"The application encountered an error: "
								+ caught.getLocalizedMessage());
				errDia.showRelativeTo(saveButton);
			}

			public void onSuccess(SurveyDto result) {
				statusLabel.setVisible(false);
				if (result != null) {
					currentSelection = result;
					loadExistingMappings(id);
					updateDataGrid(result);
				}
			}
		};
		surveyService.loadFullSurvey(id, surveyCallback);
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
						errDia.showRelativeTo(saveButton);
					}

					@Override
					public void onSuccess(
							ArrayList<SurveyAttributeMappingDto> result) {
						if (result != null) {
							for (SurveyAttributeMappingDto dto : result) {
								ListBox box = attributeListboxes.get(dto
										.getSurveyId()
										+ ":" + dto.getSurveyQuestionId());
								if (box != null) {
									for (int i = 0; i < box.getItemCount(); i++) {
										if (box.getValue(i) != null
												&& box.getValue(i).equals(
														dto.getAttributeName())) {
											box.setSelectedIndex(i);
											break;
										}
									}
								}
							}
						}
					}
				});
	}

	private void populateSurveyList(ArrayList<SurveyDto> surveyItems) {
		surveyListbox.clear();
		currentDtoList = surveyItems;
		if (surveyItems != null) {
			surveyListbox.addItem("", "");
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(survey.getName() != null ? survey
						.getName() : "Survey " + survey.getKeyId().toString(),
						survey.getKeyId().toString());
			}
		}
	}

	/**
	 * creates the controls for header-level information (dates, names, etc)
	 * 
	 * @return
	 */
	private Widget createInputControls() {
		VerticalPanel controlPanel = new VerticalPanel();

		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(new Label("Survey Group: "));
		surveyGroup = new ListBox();
		labelPanel.add(surveyGroup);
		controlPanel.add(labelPanel);
		surveyGroup.addChangeHandler(this);

		labelPanel = new HorizontalPanel();
		labelPanel.add(new Label("Survey: "));
		surveyListbox = new ListBox();
		surveyListbox.addChangeHandler(this);
		labelPanel.add(surveyListbox);
		controlPanel.add(labelPanel);

		return controlPanel;
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * populates a dto using the values currently set in the UI widgets and
	 * persists it to the server
	 */
	private void saveMapping() {

		if (currentSelection != null) {
			ArrayList<SurveyAttributeMappingDto> mappingDtoList = new ArrayList<SurveyAttributeMappingDto>();
			for (QuestionGroupDto qGroup : currentSelection
					.getQuestionGroupList()) {
				if (qGroup.getQuestionMap() != null) {
					for (QuestionDto q : qGroup.getQuestionMap().values()) {
						ListBox attrBox = attributeListboxes
								.get(currentSelection.getKeyId() + ":"
										+ q.getKeyId());
						if (attrBox != null) {
							String val = attrBox.getValue(attrBox
									.getSelectedIndex());
							if (val != null && val.trim().length() > 0) {
								SurveyAttributeMappingDto dto = new SurveyAttributeMappingDto();
								dto.setSurveyId(currentSelection.getKeyId());
								dto.setObjectName(MAP_TARGET_OBJECT_NAME);
								dto.setAttributeName(val);
								dto
										.setSurveyQuestionId(q.getKeyId()
												.toString());
								mappingDtoList.add(dto);
							}
						}
					}
				}
			}
			mappingService.saveMappings(mappingDtoList,
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
		if (event.getSource() == resetButton) {
			reset();
			currentSelection = null;
		} else if (event.getSource() == saveButton) {
			saveMapping();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroup) {
			getSurveys();
		} else if (event.getSource() == surveyListbox) {
			loadFullSurvey(currentDtoList.get(
					surveyListbox.getSelectedIndex() - 1).getKeyId());
		} else if (event.getSource() == saveButton) {
			saveMapping();
		} else if (event.getSource() == resetButton) {
			reset();
		}
	}
}
