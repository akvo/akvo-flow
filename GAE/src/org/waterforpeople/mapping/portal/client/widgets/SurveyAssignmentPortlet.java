package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
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
	private DateBox effectiveStartDate;
	private DateBox effectiveEndDate;
	private VerticalPanel contentPanel;

	public SurveyAssignmentPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		contentPanel = new VerticalPanel();
		deviceRoot = new Tree();
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
						TreeItem group = new TreeItem(entry.getKey());
						if (entry.getValue() != null) {
							for (DeviceDto dto : entry.getValue()) {
								group.addItem(dto.getPhoneNumber());
							}
						}
						deviceRoot.addItem(group);
					}
				}
			}
		};
		deviceService.listDeviceByGroup(deviceCallback);
		contentPanel.add(deviceRoot);
		setContent(contentPanel);
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
