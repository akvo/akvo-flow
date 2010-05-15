package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
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
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final int MAX_ITEMS = 20;
	private static final int HEIGHT = 1600;
	private static final int WIDTH = 800;

	private ListBox surveyGroup;
	private ListBox surveyListbox;

	private ArrayList<ListBox> attributeListboxes;

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
	private int currentSelection = -1;

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
		mappingService.listObjectAttributes(
				"org.waterforpeople.mapping.domain.AccessPoint",
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
		surveyService.listSurveyGroups(null,
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
			ArrayList<SurveyQuestionDto> allQuestions = new ArrayList<SurveyQuestionDto>();
			for(QuestionGroupDto qGroup :survey.getQuestionGroupList()){
				
				//allQuestions.addAll(qGroup.get)
			}
			survey.getQuestionGroupList();
		/*	Grid grid = new Grid(currentDtoList.length + 1, 3);
			grid.addClickHandler(this);
			// build headers
			grid.setText(0, 0, "Question Group");
			grid.setText(0, 1, "Question Text");
			grid.setText(0, 2, "Attribute");
			setGridRowStyle(grid, 0, false);
			for (int i = 1; i < currentDtoList.length + 1; i++) {
				grid
						.setWidget(i, 0, new Label(currentDtoList[i - 1]
								.getName()));

				grid.setWidget(i, 1, new Label(currentDtoList[i - 1]
						.getLanguage()));*/
				/*
				 * grid .setWidget( i, 2, currentDtoList[i - 1].getStartDate()
				 * != null ? new Label( DATE_FMT.format(currentDtoList[i - 1]
				 * .getStartDate())) : new Label("")); grid.setWidget(i, 3,
				 * currentDtoList[i - 1].getEndDate() != null ? new Label(
				 * DATE_FMT.format(currentDtoList[i - 1] .getEndDate())) : new
				 * Label(""));
				 
				setGridRowStyle(grid, i, false);
			}
			gridPanel.add(grid);*/
		} else {
			gridPanel.add(new Label("No Assignments"));
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

	}

	private void getSurveys() {

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
				surveyService.listSurveysByGroup(selectedGroupId, surveyCallback);
			}
		} else {
			MessageDialog errDia = new MessageDialog("Please select a group",
					"You must select a survey group first");
			errDia.showRelativeTo(saveButton);
		}
	}

	private void populateSurveyList(ArrayList<SurveyDto> surveyItems) {
		currentDtoList = surveyItems;
		if (surveyItems != null) {
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(survey.getName(), survey.getKeyId()
						.toString());
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
		SurveyAssignmentDto dto = new SurveyAssignmentDto();
		ArrayList<DeviceDto> dtoList = new ArrayList<DeviceDto>();

		if (currentSelection > 0) {
		//	dto.setKeyId(currentDtoList[currentSelection - 1].getKeyId());
		}

		ArrayList<String> errors = dto.getErrorMessages();
		if (errors.size() == 0) {
			// TODO: actually pass data
			mappingService.saveMappings(null,
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
		} else {
			StringBuilder builder = new StringBuilder("Invalid input:\n");
			for (String msg : errors) {
				builder.append(msg).append("<br>");
			}
			MessageDialog errDia = new MessageDialog("Cannot save assignment",
					builder.toString());
			errDia.showRelativeTo(saveButton);
		}
	}

	/**
	 * uses the values of the currently selected dto (from the grid) to populate
	 * the input panel widgets
	 */
	private void populateInputPanelFromSelection() {
		if (currentSelection > 0) {
			reset();

		}

	}

	/**
	 * handles all the button clicks for this portlet
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == resetButton) {
			reset();
			currentSelection = -1;
		} else if (event.getSource() == saveButton) {
			saveMapping();
		}
	}

	@Override
	protected boolean getReadyForRemove() {
		return true;
	}

	@Override
	protected void handleConfigClick() {
		// no-op

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// no-op
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroup) {
			getSurveys();
		} else if (event.getSource() == surveyListbox) {
			
			updateDataGrid(currentDtoList.get(surveyListbox.getSelectedIndex()));
		}

	}
}
