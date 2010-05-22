package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;

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
	public static final String DESCRIPTION = "Displays last known location of all devices";
	public static final String NAME = "Device Location";
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
	 * constructs a marker and it's corresponding infoWindow
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
				info.open(marker, new InfoWindowContent("<b>Phone #:</b> "
						+ device.getPhoneNumber() + "<br><b>Last Updated:</b> "
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
