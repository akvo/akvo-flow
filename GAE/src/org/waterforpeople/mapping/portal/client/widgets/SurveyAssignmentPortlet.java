package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private TextBox eventName;
	private ListBox selectedDevices;
	private DateBox effectiveStartDate;
	private DateBox effectiveEndDate;
	private DockPanel contentPanel;
	private DragController deviceDragController;
	private DropController deviceDropController;

	public SurveyAssignmentPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		contentPanel = new DockPanel();
		contentPanel.add(createHeaderControls(), DockPanel.NORTH);
		contentPanel.add(createDeviceSelector(), DockPanel.WEST);
		ScrollPanel surveyScroll = new ScrollPanel();
		surveyRoot = new Tree();
		surveyScroll.add(surveyRoot);
		contentPanel.add(surveyScroll, DockPanel.WEST);
		setContent(contentPanel);

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
		return labelPanel;
	}

	private Widget createDeviceSelector() {
		VerticalPanel devPanel = new VerticalPanel();
		devPanel.add(new Label("Select Device(s)"));
		HorizontalPanel hPanel = new HorizontalPanel();
		ScrollPanel deviceScroll = new ScrollPanel();
		deviceRoot = new Tree();
		deviceScroll.add(deviceRoot);
		hPanel.add(deviceScroll);
		selectedDevices = new ListBox();
		selectedDevices.setVisibleItemCount(5);
		hPanel.add(selectedDevices);
		devPanel.add(hPanel);
		deviceDragController = new PickupDragController(RootPanel.get(), true);
		deviceDropController = new SimpleDropController(selectedDevices);
		return devPanel;
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
