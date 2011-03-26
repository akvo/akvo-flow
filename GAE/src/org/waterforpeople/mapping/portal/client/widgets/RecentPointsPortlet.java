package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.Status;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Displays most recent access point information
 * 
 * @author Christopher Fagiani
 * 
 */
public class RecentPointsPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String DESCRIPTION = TEXT_CONSTANTS.recentPointsPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.recentPointsPortletTitle();
	private static final int WIDTH = 500;
	private static final int HEIGHT = 500;
	private MapWidget map;

	@SuppressWarnings("deprecation")
	public RecentPointsPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);

		// Set up the callback object.
		setContent(constructMap());
		AccessPointManagerServiceAsync apService = GWT
				.create(AccessPointManagerService.class);
		AccessPointSearchCriteriaDto searchDto = new AccessPointSearchCriteriaDto();
		// only look at the last 1 year of points
		Date date = new java.util.Date();
		date.setYear(date.getYear() - 1);
		searchDto.setCollectionDateFrom(date);
		searchDto.setOrderBy("collectionDate");
		searchDto.setOrderByDir("desc");

		apService.listAccessPoints(searchDto, null,
				new AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());								
						errDia.showRelativeTo(RecentPointsPortlet.this);
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<AccessPointDto>> result) {
						// setAccessPointCursor(result());
						if (result.getPayload() != null) {
							for (AccessPointDto dto : result.getPayload()) {
								map.addOverlay(createMarker(dto));
							}
						}
					}

				});

	}

	/**
	 * constructs a marker and its corresponding infoWindow
	 * 
	 * @param point
	 * @param device
	 * @return
	 */
	private Marker createMarker(final AccessPointDto dto) {
		Icon icon = selectIcon(dto.getPointType(), dto.getPointStatus());
		final Marker marker = new Marker(LatLng.newInstance(dto.getLatitude(),
				dto.getLongitude()), MarkerOptions.newInstance(icon));

		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.open(marker, new InfoWindowContent("<b>"+TEXT_CONSTANTS.pointType()+":</b> "
						+ dto.getPointType().toString() + "<br><b>"+TEXT_CONSTANTS.status()+"</b> "
						+ dto.getPointStatus().toString()));
				
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

	/**
	 * returns the correct icon based on point type and status
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	private Icon selectIcon(AccessPointType type, Status status) {
		String prefix = "water-";
		if (type == AccessPointType.SANITATION_POINT) {
			prefix = "san-";
		} else if (type == AccessPointType.SCHOOL) {
			prefix = "school-water-";
		} else if (type == AccessPointType.PUBLIC_INSTITUTION) {
			prefix = "other-water-";
		}
		String statusString = "green-";
		if (Status.FUNCTIONING_OK == status) {
			statusString = "yellow-";
		} else if (Status.FUNCTIONING_WITH_PROBLEMS == status) {
			statusString = "red-";
		} else if (Status.NO_IMPROVED_SYSTEM == status) {
			statusString = "black-";
		}else if (Status.OTHER == status){
			statusString = "black-";
		}

		Icon i = Icon.newInstance("/images/map/" + prefix + statusString
				+ "1.png");
		i.setIconSize(Size.newInstance(18, 40));
		i.setIconAnchor(Point.newInstance(6, 40));
		i.setInfoWindowAnchor(Point.newInstance(6,40));
		return i;
	}
}
