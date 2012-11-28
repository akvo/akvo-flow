/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Displays last known location of all devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class DeviceLocationPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String DESCRIPTION = TEXT_CONSTANTS.deviceLocationPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.deviceLocatoinPortletTitle();
	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;
	private MapWidget map;

	public DeviceLocationPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);

		DeviceServiceAsync deviceService = GWT.create(DeviceService.class);
		// Set up the callback object.
		setContent(constructMap());
		AsyncCallback<DeviceDto[]> deviceCallback = new AsyncCallback<DeviceDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(DeviceDto[] result) {
				if (result != null && map != null) {
					for (int i = 0; i < result.length; i++) {
						if (result[i].getLastKnownLat() != null
								&& result[i].getLastKnownLon() != null) {
							map.addOverlay(createMarker(LatLng.newInstance(
									result[i].getLastKnownLat(), result[i]
											.getLastKnownLon()), result[i]));
						}
					}
				}
			}
		};
		deviceService.listDevice(deviceCallback);
	}

	/**
	 * constructs a marker and its corresponding infoWindow
	 * 
	 * @param point
	 * @param device
	 * @return
	 */
	private Marker createMarker(LatLng point, final DeviceDto device) {
		final Marker marker = new Marker(point);

		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.open(marker, new InfoWindowContent("<b>"+TEXT_CONSTANTS.phoneNum()+":</b> "
						+ device.getPhoneNumber() + "<br><b>"+TEXT_CONSTANTS.lastUpdated()+":</b> "
						+ device.getLastPositionDate()));
			}
		});

		return marker;
	}

	private MapWidget constructMap() {
		map = new MapWidget();
		map.setSize(WIDTH + "px", (HEIGHT - 20) + "px");
		// add zoom control
		map.addControl(new LargeMapControl());
		return map;
	}

	public String getName() {
		return NAME;
	}

}
