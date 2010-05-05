package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.gallatinsystems.framework.gwt.portlet.client.TreeDragController;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
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
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * portlet that can be used to assign surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentPortlet extends Portlet {
	public static final String NAME = "Survey Assignment Portlet";
	public static final String DESCRIPTION = "Assigns surveys to devices";
	private static final int HEIGHT = 1600;
	private static final int WIDTH = 800;
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
	private Map<Widget, BaseDto> deviceMap;
	private Map<Widget, BaseDto> surveyMap;

	public SurveyAssignmentPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		contentPanel = new DockPanel();
		statusLabel = new Label();
		statusLabel.setVisible(false);
		contentPanel.add(statusLabel, DockPanel.NORTH);
		contentPanel.add(createHeaderControls(), DockPanel.NORTH);

		deviceMap = new HashMap<Widget, BaseDto>();
		surveyMap = new HashMap<Widget, BaseDto>();

		deviceRoot = new Tree();
		selectedDevices = new ListBox();
		HorizontalPanel treeHost = new HorizontalPanel();
		deviceDragController = installTreeSelector("Devices", deviceRoot,
				selectedDevices, treeHost, deviceMap);

		surveyRoot = new Tree();
		selectedSurveys = new ListBox();
		surveyDragController = installTreeSelector("Surveys", surveyRoot,
				selectedSurveys, treeHost, surveyMap);

		contentPanel.add(treeHost, DockPanel.CENTER);

		surveyAssignmentService = GWT.create(SurveyAssignmentService.class);

		resetButton = new Button("Clear");
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		saveButton = new Button("Save");
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SurveyAssignmentDto dto = new SurveyAssignmentDto();
				ArrayList<DeviceDto> dtoList = new ArrayList<DeviceDto>();
				for (int i = 0; i < selectedDevices.getItemCount(); i++) {
					DeviceDto devDto = new DeviceDto();
					devDto
							.setKeyId(Long.parseLong(selectedDevices
									.getValue(i)));
					dtoList.add(devDto);
				}
				ArrayList<SurveyDto> surveyDtos = new ArrayList<SurveyDto>();
				for (int i = 0; i < selectedSurveys.getItemCount(); i++) {
					SurveyDto sDto = new SurveyDto();
					sDto.setKeyId(Long.parseLong(selectedSurveys.getValue(i)));
					surveyDtos.add(sDto);
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
					StringBuilder builder = new StringBuilder(
							"Invalid input:\n");
					for (String msg : errors) {
						builder.append(msg).append("<br>");
					}
					MessageDialog errDia = new MessageDialog(
							"Cannot save assignment", builder.toString());
					errDia.showRelativeTo(saveButton);
				}
			}
		});
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(resetButton);
		buttonPanel.add(saveButton);
		contentPanel.add(buttonPanel, DockPanel.SOUTH);
		setContent(contentPanel);

		getDevices();
		getSurveys();

	}

	private void reset() {
		selectedDevices.clear();
		selectedSurveys.clear();
		deviceRoot.clear();
		surveyRoot.clear();

		// this is inefficient but is ok for now. Can refactor to not fetch from
		// server later.
		getDevices();
		getSurveys();

		language.setSelectedIndex(0);
		eventName.setText("");
		effectiveEndDate.setValue(null);
		effectiveStartDate.setValue(null);
	}

	private void getSurveys() {
		surveyService = GWT.create(SurveyService.class);
		// Set up the callback object.
		AsyncCallback<SurveyDto[]> surveyCallback = new AsyncCallback<SurveyDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(SurveyDto[] result) {
				TreeItem ungroupedSurvey = new TreeItem("Ungrouped");
				surveyRoot.addItem(ungroupedSurvey);
				if (result != null) {

					for (int i = 0; i < result.length; i++) {
						TreeItem item = new TreeItem(new Label(result[i]
								.getName()));
						surveyMap.put(item.getWidget(), result[i]);
						surveyDragController.makeDraggable(item.getWidget());
						ungroupedSurvey.addItem(item);
					}
				}
			}
		};
		surveyService.listSurvey(surveyCallback);
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
					for (Entry<String, ArrayList<DeviceDto>> entry : result
							.entrySet()) {
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
		};
		deviceService.listDeviceByGroup(deviceCallback);
	}

	/**
	 * creates the controls for header-level information (dates, names, etc)
	 * 
	 * @return
	 */
	private Widget createHeaderControls() {
		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(new Label("Trip Name: "));
		eventName = new TextBox();
		labelPanel.add(eventName);
		labelPanel.add(new Label("Start: "));
		effectiveStartDate = new DateBox();
		labelPanel.add(effectiveStartDate);
		labelPanel.add(new Label("End: "));
		effectiveEndDate = new DateBox();
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

		scrollPanel.setWidth("150px");
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

}
