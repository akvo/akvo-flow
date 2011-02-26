package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentServiceAsync;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * component for editing an assignment of surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentEditWidget extends Composite implements
		ContextAware, ClickHandler {
	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private Panel contentPanel;
	private SurveySelectionWidget surveySelectWidget;
	private Map<String, Object> contextBundle;
	private SurveyAssignmentServiceAsync surveyAssignmentService;
	private DeviceServiceAsync deviceService;
	private ListBox devicePickerListbox;
	private ListBox selectedDevicesListbox;
	private ListBox selectedSurveyListbox;
	private TextBox eventName;
	private DateBox effectiveStartDate;
	private DateBox effectiveEndDate;
	private Button addSelectedButton;
	private Button removeSelectedButton;
	private Button clearButton;

	private List<DeviceDto> allDevices;

	public SurveyAssignmentEditWidget() {
		surveyAssignmentService = GWT.create(SurveyAssignmentService.class);
		deviceService = GWT.create(DeviceService.class);
		allDevices = new ArrayList<DeviceDto>();
		contentPanel = new VerticalPanel();
		contentPanel.add(constructSelectorPanel());
		contentPanel.add(constructDetailsPanel());
		getDevices();
		initWidget(contentPanel);
	}

	private Composite constructSelectorPanel() {
		Panel mainPanel = new VerticalPanel();
		Panel selectorPanel = new HorizontalPanel();
		CaptionPanel selectorPanelCap = new CaptionPanel("Selection Criteria");
		selectorPanelCap.add(mainPanel);
		surveySelectWidget = new SurveySelectionWidget(Orientation.VERTICAL,
				TerminalType.SURVEY);
		selectorPanel.add(surveySelectWidget);
		VerticalPanel devPanel = new VerticalPanel();
		devPanel.add(ViewUtil.initLabel("Devices", LABEL_STYLE));
		devicePickerListbox = new ListBox(true);
		devicePickerListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		devPanel.add(devicePickerListbox);
		selectorPanel.add(devPanel);
		mainPanel.add(selectorPanel);
		addSelectedButton = new Button("Add Selected");
		addSelectedButton.addClickHandler(this);
		mainPanel.add(addSelectedButton);
		return selectorPanelCap;
	}

	private Composite constructDetailsPanel() {
		CaptionPanel detailPanelCap = new CaptionPanel("Assignment Details");
		selectedDevicesListbox = new ListBox(true);
		selectedDevicesListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		selectedSurveyListbox = new ListBox(true);
		selectedSurveyListbox.setVisibleItemCount(DEFAULT_ITEM_COUNT);
		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(ViewUtil.initLabel("Trip Name: ", LABEL_STYLE));
		eventName = new TextBox();
		labelPanel.add(eventName);
		labelPanel.add(ViewUtil.initLabel("Start: ", LABEL_STYLE));
		effectiveStartDate = new DateBox();
		effectiveStartDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		labelPanel.add(effectiveStartDate);
		labelPanel.add(ViewUtil.initLabel("End: ", LABEL_STYLE));
		effectiveEndDate = new DateBox();
		effectiveEndDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat
				.getShortDateFormat()));
		labelPanel.add(effectiveEndDate);
		VerticalPanel main = new VerticalPanel();
		main.add(labelPanel);
		HorizontalPanel selectedItemPanel = new HorizontalPanel();
		selectedItemPanel.add(ViewUtil.initLabel("Selected Surveys",
				LABEL_STYLE));
		selectedItemPanel.add(selectedSurveyListbox);
		selectedItemPanel.add(ViewUtil.initLabel("Selected Devices",
				LABEL_STYLE));
		selectedItemPanel.add(selectedDevicesListbox);
		main.add(selectedItemPanel);
		Panel buttonPanel = new HorizontalPanel();
		removeSelectedButton = new Button("Remove Selected");
		removeSelectedButton.addClickHandler(this);
		buttonPanel.add(removeSelectedButton);
		clearButton = new Button("Undo Changes");
		clearButton.addClickHandler(this);
		buttonPanel.add(clearButton);		
		main.add(buttonPanel);
		detailPanelCap.add(main);
		return detailPanelCap;
	}

	private void getDevices() {
		deviceService
				.listDeviceByGroup(new AsyncCallback<HashMap<String, ArrayList<DeviceDto>>>() {
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog("Error",
								"Could not get devices. Please try again");
						errDia.showCentered();
					}

					public void onSuccess(
							HashMap<String, ArrayList<DeviceDto>> result) {
						if (result != null) {
							for (Entry<String, ArrayList<DeviceDto>> entry : result
									.entrySet()) {
								if (entry.getValue() != null) {
									for (DeviceDto dto : entry.getValue()) {
										allDevices.add(dto);
									}
								}
							}
							populateDeviceControl(null);
						}
					}
				});
	}

	private void populateDeviceControl(List<DeviceDto> deviceList) {
		if (deviceList == null && allDevices != null) {
			deviceList = allDevices;
		}
		if (deviceList != null) {
			devicePickerListbox.clear();
			for (DeviceDto dto : deviceList) {
				devicePickerListbox.addItem(dto.getPhoneNumber()
						+ (dto.getDeviceIdentifier() != null ? " ("
								+ dto.getDeviceIdentifier() + ")" : ""), dto
						.getKeyId().toString());
			}
		}
	}

	@Override
	public void flushContext() {
		if (contextBundle != null) {
			contextBundle.remove(BundleConstants.SURVEY_ASSIGNMENT);
		}

	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (contextBundle == null) {
			contextBundle = new HashMap<String, Object>();
		}
		return contextBundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		/*
		 * surveyAssignmentService.saveSurveyAssignment(dto, new
		 * AsyncCallback<SurveyAssignmentDto>() {
		 * 
		 * @Override public void onSuccess(SurveyAssignmentDto result) {
		 * statusLabel.setText("Assignment Saved");
		 * statusLabel.setVisible(true); currentDto = result; }
		 * 
		 * @Override public void onFailure(Throwable caught) {
		 * statusLabel.setText("Error: " + caught.getLocalizedMessage());
		 * statusLabel.setVisible(true); } });
		 */

	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		if (bundle == null) {
			contextBundle = new HashMap<String, Object>();
		} else {
			contextBundle = bundle;
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

}
