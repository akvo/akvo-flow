package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.gallatinsystems.framework.gwt.portlet.client.TreeDragController;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * portlet that can be used to assign surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentPortlet extends Portlet implements ClickHandler,
		OpenHandler<TreeItem> {
	public static final String NAME = "Survey Assignment Portlet";
	public static final String DESCRIPTION = "Assigns surveys to devices";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final String DUMMY = "DUMMY";
	private static final String PLEASE_WAIT = "Loading...";
	private static final int MAX_ITEMS = 20;
	private static final int HEIGHT = 1600;
	private static final int WIDTH = 900;
	private static final DateTimeFormat DATE_FMT = DateTimeFormat
			.getShortDateFormat();
	private Tree deviceRoot;
	private Tree surveyRoot;
	private Label statusLabel;
	private TextBox eventName;
	private ListBox language;
	private ListBox selectedDevices;
	private ListBox selectedSurveys;
	private DateBox effectiveStartDate;
	private DateBox effectiveEndDate;
	private DockPanel contentPanel;
	private TreeDragController deviceDragController;
	private TreeDragController surveyDragController;
	private SurveyServiceAsync surveyService;
	private SurveyAssignmentServiceAsync surveyAssignmentService;
	private Button saveButton;
	private Button resetButton;
	private Button editButton;
	private Button deleteButton;
	private Button createButton;
	private DockPanel inputPanel;
	private VerticalPanel gridPanel;
	private SurveyAssignmentDto[] currentDtoList;
	private int currentSelection = -1;

	private HashMap<String, ArrayList<DeviceDto>> devices;
	private HashMap<SurveyGroupDto, ArrayList<SurveyDto>> surveys;

	private Map<Widget, BaseDto> deviceMap;
	private Map<Widget, BaseDto> surveyMap;

	public SurveyAssignmentPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		inputPanel = new DockPanel();
		contentPanel = new DockPanel();
		inputPanel = new DockPanel();
		statusLabel = new Label();
		statusLabel.setVisible(false);
		inputPanel.add(statusLabel, DockPanel.NORTH);
		inputPanel.add(createInputControls(), DockPanel.NORTH);

		deviceMap = new HashMap<Widget, BaseDto>();
		surveyMap = new HashMap<Widget, BaseDto>();

		deviceRoot = new Tree();
		selectedDevices = new ListBox();
		HorizontalPanel treeHost = new HorizontalPanel();
		deviceDragController = installTreeSelector("Devices", deviceRoot,
				selectedDevices, treeHost, deviceMap);

		surveyRoot = new Tree();
		surveyRoot.addOpenHandler(this);
		selectedSurveys = new ListBox();
		surveyDragController = installTreeSelector("Surveys", surveyRoot,
				selectedSurveys, treeHost, surveyMap);

		inputPanel.add(treeHost, DockPanel.CENTER);

		surveyAssignmentService = GWT.create(SurveyAssignmentService.class);

		resetButton = new Button("Clear");
		resetButton.addClickHandler(this);

		saveButton = new Button("Save");
		saveButton.addClickHandler(this);

		HorizontalPanel masterButtonPanel = new HorizontalPanel();
		deleteButton = new Button("Delete Selected");
		deleteButton.addClickHandler(this);
		editButton = new Button("Edit Assignment");
		editButton.addClickHandler(this);
		createButton = new Button("Create Assignment");
		createButton.addClickHandler(this);

		masterButtonPanel.add(createButton);
		masterButtonPanel.add(editButton);
		masterButtonPanel.add(deleteButton);

		HorizontalPanel inputButtonPanel = new HorizontalPanel();
		inputButtonPanel.add(resetButton);
		inputButtonPanel.add(saveButton);
		inputPanel.add(inputButtonPanel, DockPanel.SOUTH);
		inputPanel.setVisible(false);
		contentPanel.add(inputPanel, DockPanel.NORTH);
		gridPanel = new VerticalPanel();
		contentPanel.add(gridPanel, DockPanel.CENTER);
		contentPanel.add(masterButtonPanel, DockPanel.SOUTH);
		setContent(contentPanel);

		getDevices();
		getSurveyGroups();
		getAssignments(null);
	}

	// TODO: paginate!
	private void getAssignments(String cursor) {
		surveyAssignmentService
				.listSurveyAssignments(new AsyncCallback<SurveyAssignmentDto[]>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog("Error",
								"Cannot load assignment");
						errDia.showRelativeTo(gridPanel);
					}

					@Override
					public void onSuccess(SurveyAssignmentDto[] result) {
						currentDtoList = result;
						updateDataGrid();
					}
				});
	}

	private void updateDataGrid() {
		gridPanel.clear();
		if (currentDtoList != null) {
			Grid grid = new Grid(currentDtoList.length + 1, 4);
			grid.addClickHandler(this);
			// build headers
			grid.setText(0, 0, "Event");
			grid.setText(0, 1, "Language");
			grid.setText(0, 2, "Start");
			grid.setText(0, 3, "End");
			setGridRowStyle(grid, 0, false);
			for (int i = 1; i < currentDtoList.length + 1; i++) {
				grid
						.setWidget(i, 0, new Label(currentDtoList[i - 1]
								.getName()));

				grid.setWidget(i, 1, new Label(currentDtoList[i - 1]
						.getLanguage()));
				grid
						.setWidget(
								i,
								2,
								currentDtoList[i - 1].getStartDate() != null ? new Label(
										DATE_FMT.format(currentDtoList[i - 1]
												.getStartDate()))
										: new Label(""));
				grid.setWidget(i, 3,
						currentDtoList[i - 1].getEndDate() != null ? new Label(
								DATE_FMT.format(currentDtoList[i - 1]
										.getEndDate())) : new Label(""));
				setGridRowStyle(grid, i, false);
			}
			gridPanel.add(grid);
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
		selectedDevices.clear();
		selectedSurveys.clear();
		deviceRoot.clear();
		surveyRoot.clear();

		populateDeviceTree();
		populateSurveyTree(null);

		language.setSelectedIndex(0);
		eventName.setText("");
		effectiveEndDate.setValue(null);
		effectiveStartDate.setValue(null);
	}

	private void getSurveyGroups() {
		surveyService = GWT.create(SurveyService.class);
		surveyService.listSurveyGroups(null,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// no-op
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						surveys = new HashMap<SurveyGroupDto, ArrayList<SurveyDto>>();
						if (result != null) {

							for (SurveyGroupDto group : result) {
								surveys.put(group, null);
							}
							populateSurveyTree(null);
						}
					}
				});
		/*
		 * // Set up the callback object. AsyncCallback<SurveyDto[]>
		 * surveyCallback = new AsyncCallback<SurveyDto[]>() { public void
		 * onFailure(Throwable caught) { // no-op }
		 * 
		 * public void onSuccess(SurveyDto[] result) {
		 * 
		 * if (result != null) { surveys = result; populateSurveyTree(); } } };
		 * surveyService.listSurvey(surveyCallback);
		 */
	}

	private void getDevices() {
		DeviceServiceAsync deviceService = GWT.create(DeviceService.class);
		// Set up the callback object.
		AsyncCallback<HashMap<String, ArrayList<DeviceDto>>> deviceCallback = new AsyncCallback<HashMap<String, ArrayList<DeviceDto>>>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(HashMap<String, ArrayList<DeviceDto>> result) {
				if (result != null) {
					devices = result;
					populateDeviceTree();
				}
			}
		};
		deviceService.listDeviceByGroup(deviceCallback);
	}

	private void populateDeviceTree() {
		if (devices != null) {
			for (Entry<String, ArrayList<DeviceDto>> entry : devices.entrySet()) {
				TreeItem group = new TreeItem(new Label(entry.getKey()));
				deviceDragController.makeDraggable(group.getWidget());
				if (entry.getValue() != null) {
					for (DeviceDto dto : entry.getValue()) {
						TreeItem phoneItem = new TreeItem(new Label(dto
								.getPhoneNumber()));
						deviceMap.put(phoneItem.getWidget(), dto);
						deviceDragController.makeDraggable(phoneItem
								.getWidget());
						group.addItem(phoneItem);
					}
				}
				deviceRoot.addItem(group);
			}
		}
	}

	private void populateSurveyTree(TreeItem root) {
		if (root == null) {
			for (Entry<SurveyGroupDto, ArrayList<SurveyDto>> groupEntry : surveys
					.entrySet()) {
				TreeItem groupItem = new TreeItem(groupEntry.getKey().getCode());
				groupItem.setUserObject(groupEntry.getKey());
				if (groupEntry.getValue() == null) {
					TreeItem dummyItem = new TreeItem(PLEASE_WAIT);
					dummyItem.setUserObject(DUMMY);
					groupItem.addItem(dummyItem);
				} else {
					for (SurveyDto survey : groupEntry.getValue()) {
						TreeItem surveyItem = new TreeItem(new Label(
								getSurveyName(survey)));
						surveyMap.put(surveyItem.getWidget(), survey);
						surveyDragController.makeDraggable(surveyItem
								.getWidget());
						groupItem.addItem(surveyItem);
					}
				}
				surveyRoot.addItem(groupItem);
			}
		} else {
			// remove the dummy
			if (root.getChildCount() > 0
					&& root.getChild(0).getUserObject().equals(DUMMY)) {
				root.removeItem(root.getChild(0));
			}
			ArrayList<SurveyDto> surveyList = surveys.get((SurveyGroupDto) root
					.getUserObject());
			if (surveyList != null) {
				for (int i = 0; i < surveyList.size(); i++) {
					TreeItem surveyItem = new TreeItem(new Label(
							getSurveyName(surveyList.get(i))));
					surveyMap.put(surveyItem.getWidget(), surveyList.get(i));
					surveyDragController.makeDraggable(surveyItem.getWidget());
					root.addItem(surveyItem);
				}
			}
		}
	}

	private String getSurveyName(SurveyDto survey) {
		String name = survey.getName();
		if (name == null || name.trim().length() == 0) {
			name = survey.getKeyId().toString();
		}
		name = name + " - v." + survey.getVersion();
		return name;
	}

	/**
	 * creates the controls for header-level information (dates, names, etc)
	 * 
	 * @return
	 */
	private Widget createInputControls() {
		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(new Label("Trip Name: "));
		eventName = new TextBox();
		labelPanel.add(eventName);
		labelPanel.add(new Label("Start: "));
		effectiveStartDate = new DateBox();
		effectiveStartDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		labelPanel.add(effectiveStartDate);
		labelPanel.add(new Label("End: "));
		effectiveEndDate = new DateBox();
		effectiveEndDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		labelPanel.add(effectiveEndDate);
		labelPanel.add(new Label("Language: "));
		language = new ListBox();
		language.addItem("English", "English");
		language.addItem("French", "French");
		language.addItem("Spanish", "Spanish");
		labelPanel.add(language);
		return labelPanel;
	}

	/**
	 * creates the tree used to select items and sets up the drag and drop
	 * facilities. the controls are then installed in the host dock panel. it
	 * will return an initialized drag controller
	 * 
	 * @return
	 */
	private TreeDragController installTreeSelector(String typeDisplayName,
			final Tree sourceTree, final ListBox targetBox,
			InsertPanel hostPanel, final Map<Widget, BaseDto> dtoMap) {
		HorizontalPanel widgetPanel = new HorizontalPanel();

		VerticalPanel availPanel = new VerticalPanel();
		availPanel.add(new Label("Available " + typeDisplayName));

		ScrollPanel scrollPanel = new ScrollPanel();

		scrollPanel.setWidth("200px");
		scrollPanel.setHeight("200px");
		scrollPanel.add(sourceTree);
		availPanel.add(scrollPanel);

		VerticalPanel selectedPanel = new VerticalPanel();
		selectedPanel.add(new Label("Assigned " + typeDisplayName));

		targetBox.setVisibleItemCount(5);
		targetBox.setWidth("250px");
		selectedPanel.add(targetBox);

		widgetPanel.add(availPanel);
		widgetPanel.add(selectedPanel);

		// now set up drag and drop for devices

		TreeDragController deviceDragController = new TreeDragController(
				RootPanel.get());
		DropController deviceDropController = new SimpleDropController(
				targetBox);
		deviceDragController.registerDropController(deviceDropController);

		// define custom events that occur when dragging
		DragHandler handler = new DragHandlerAdapter() {

			@Override
			public void onDragEnd(DragEndEvent event) {

				if (event.getSource() instanceof Label) {
					if (event.getContext().finalDropController != null) {
						for (Widget selectedItem : event.getContext().selectedWidgets) {
							if (selectedItem instanceof Label) {
								Label lbl = (Label) selectedItem;
								addUniqueItemToList(lbl, dtoMap, sourceTree,
										targetBox);
							}
						}
					}
				}
			}
		};
		deviceDragController.addDragHandler(handler);
		hostPanel.add(widgetPanel);
		return deviceDragController;
	}

	/**
	 * this method will find the selectedItem in the sourceTree and add it to
	 * the targetBox if it is not already in there. If the selectedItem is at
	 * the "group" level in the tree (i.e. it has children) all of its children
	 * will be added
	 * 
	 * @param selectedItem
	 * @param sourceTree
	 * @param targetBox
	 */
	private void addUniqueItemToList(Label selectedLabel,
			Map<Widget, BaseDto> valueMap, Tree sourceTree, ListBox targetBox) {
		boolean found = false;
		String selectedText = selectedLabel.getText();
		for (int i = 0; i < sourceTree.getItemCount(); i++) {
			if (selectedText.equals(sourceTree.getItem(i).getText())) {
				found = true;
				for (int j = 0; j < sourceTree.getItem(i).getChildCount(); j++) {
					addUniqueItemToList(sourceTree.getItem(i).getChild(j)
							.getText(), valueMap.get(
							sourceTree.getItem(i).getChild(j).getWidget())
							.getKeyId().toString(), targetBox);
				}
			}
		}
		if (!found) {
			// if we didn't find the label in the Group
			// level, then it must be a device so just
			// add it
			addUniqueItemToList(selectedText, valueMap.get(selectedLabel)
					.getKeyId().toString(), targetBox);

		}
	}

	/**
	 * adds an item to a list box if it is not already present
	 * 
	 * @param item
	 * @param list
	 */
	private void addUniqueItemToList(String item, String value, ListBox list) {
		boolean found = false;
		for (int i = 0; i < list.getItemCount(); i++) {
			if (item.equals(list.getItemText(i))) {
				found = true;
				break;
			}
		}
		if (!found) {
			list.addItem(item);
			int count = list.getItemCount();
			list.setValue(count - 1, value);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * populates a dto using the values currently set in the UI widgets and
	 * persists it to the server
	 */
	private void saveAssignment() {
		SurveyAssignmentDto dto = new SurveyAssignmentDto();
		ArrayList<DeviceDto> dtoList = new ArrayList<DeviceDto>();
		for (int i = 0; i < selectedDevices.getItemCount(); i++) {
			DeviceDto devDto = new DeviceDto();
			devDto.setKeyId(Long.parseLong(selectedDevices.getValue(i)));
			dtoList.add(devDto);
		}
		ArrayList<SurveyDto> surveyDtos = new ArrayList<SurveyDto>();
		for (int i = 0; i < selectedSurveys.getItemCount(); i++) {
			SurveyDto sDto = new SurveyDto();
			sDto.setKeyId(Long.parseLong(selectedSurveys.getValue(i)));
			surveyDtos.add(sDto);
		}
		if (currentSelection > 0) {
			dto.setKeyId(currentDtoList[currentSelection - 1].getKeyId());
		}
		dto.setDevices(dtoList);
		dto.setSurveys(surveyDtos);
		dto.setEndDate(effectiveEndDate.getValue());
		dto.setStartDate(effectiveStartDate.getValue());
		dto.setName(eventName.getValue());
		dto.setLanguage(language.getValue(language.getSelectedIndex()));
		ArrayList<String> errors = dto.getErrorMessages();
		if (errors.size() == 0) {
			surveyAssignmentService.saveSurveyAssignment(dto,
					new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							statusLabel.setText("Assignment Saved");
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
			SurveyAssignmentDto currentDto = currentDtoList[currentSelection - 1];
			if (currentDto != null) {
				eventName.setText(currentDto.getName());
				effectiveStartDate.setValue(currentDto.getStartDate());
				effectiveEndDate.setValue(currentDto.getEndDate());
				for (int i = 0; i < language.getItemCount(); i++) {
					if (language.getValue(i).equalsIgnoreCase(
							currentDto.getLanguage())) {
						language.setSelectedIndex(i);
					}
				}
				if (currentDto.getDevices() != null) {
					for (DeviceDto dev : currentDto.getDevices()) {
						selectedDevices.addItem(dev.getPhoneNumber(), dev
								.getKeyId().toString());
						removeFromTree(dev.getKeyId(), deviceRoot, deviceMap);
					}
				}
				if (currentDto.getSurveys() != null) {
					for (SurveyDto s : currentDto.getSurveys()) {
						selectedSurveys.addItem(s.getName(), s.getKeyId()
								.toString());
						removeFromTree(s.getKeyId(), surveyRoot, surveyMap);
					}
				}
			}
		}
	}

	/**
	 * removes the item that represents the object with a key == keyId from the
	 * tree passed in. This will recurse through all levels of the tree until a
	 * match is found or all nodes have been visited.
	 * 
	 * @param keyId
	 * @param tree
	 * @param dtoMap
	 */
	private void removeFromTree(Long keyId, Tree tree,
			Map<Widget, BaseDto> dtoMap) {
		if (keyId != null && tree != null) {
			for (int i = 0; i < tree.getItemCount(); i++) {
				if (removeTreeItem(keyId, tree.getItem(i), dtoMap)) {
					break;
				}
			}
		}
	}

	/**
	 * checks an individual item to see if it matches the id of the key passed
	 * in. If the item has children, this method will recurse to evaluate all
	 * children.
	 * 
	 * @param keyId
	 * @param treeItem
	 * @param dtoMap
	 * @return
	 */
	private boolean removeTreeItem(Long keyId, TreeItem treeItem,
			Map<Widget, BaseDto> dtoMap) {

		BaseDto dto = dtoMap.get(treeItem.getWidget());
		if (dto != null && dto.getKeyId().equals(keyId)) {
			treeItem.remove();
			return true;
		} else if (treeItem.getChildCount() > 0) {
			for (int i = 0; i < treeItem.getChildCount(); i++) {
				if (removeTreeItem(keyId, treeItem.getChild(i), dtoMap)) {
					return true;
				}
			}
		}
		return false;
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
			saveAssignment();
		} else if (event.getSource() == deleteButton) {

		} else if (event.getSource() == editButton) {
			populateInputPanelFromSelection();
			inputPanel.setVisible(true);
		} else if (event.getSource() == createButton) {
			reset();
			currentSelection = -1;
			inputPanel.setVisible(true);
		} else if (event.getSource() instanceof Grid) {
			Grid grid = (Grid) event.getSource();

			// if we already had a selection, deselect it
			if (currentSelection > 0) {
				setGridRowStyle(grid, currentSelection, false);
			}
			Cell clickedCell = grid.getCellForEvent(event);
			// the click may not have been in a cell
			if (clickedCell != null) {
				int newSelection = clickedCell.getRowIndex();
				// if the clicked row is already selected, deselect it
				if (currentSelection == newSelection) {
					currentSelection = -1;
					setGridRowStyle(grid, currentSelection, false);
				} else {
					currentSelection = newSelection;
				}
				// if the clicked cell is the header (row 0), don't change the
				// style
				if (currentSelection > 0) {
					setGridRowStyle(grid, currentSelection, true);
				} else {
					currentSelection = -1;
				}
			}
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
	public void onOpen(OpenEvent<TreeItem> event) {
		if (event.getTarget() instanceof TreeItem) {
			final TreeItem item = (TreeItem) event.getTarget();
			if (item.getUserObject() instanceof SurveyGroupDto) {
				SurveyGroupDto sg = (SurveyGroupDto) item.getUserObject();
				// if we haven't yet loaded the surveys, load them
				if (item.getChildCount() == 1
						&& item.getChild(0).getUserObject() != null
						&& item.getChild(0).getUserObject().equals(DUMMY)) {
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
							// no-op
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							surveys.put((SurveyGroupDto) item.getUserObject(),
									result);
							populateSurveyTree(item);
						}
					};
					surveyService.listSurveysByGroup(sg.getKeyId().toString(),
							surveyCallback);
				}
			}
		}

	}
}
