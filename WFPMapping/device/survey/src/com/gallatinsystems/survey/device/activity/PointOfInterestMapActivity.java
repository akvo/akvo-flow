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

package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Window;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.GeoUtil;
import com.gallatinsystems.survey.device.view.PointsOfInterestOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * This activity is used for displaying 1 or more Points of Interest on a map
 * with info-bubble overlays that show the point information.
 * 
 * @author Christopher Fagiani
 * 
 */
public class PointOfInterestMapActivity extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private static final int INITIAL_ZOOM_LEVEL = 14;
	private ArrayList<PointOfInterest> points;
	private PointsOfInterestOverlay overlay;

	@Override
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pointmap);

		mapView = (MapView) findViewById(R.id.pointmapview);

		mapController = mapView.getController();
		mapController.setZoom(INITIAL_ZOOM_LEVEL);
		// turn on zoom controls
		mapView.setBuiltInZoomControls(true);
		points = (ArrayList<PointOfInterest>) getIntent().getExtras()
				.getSerializable(ConstantUtil.POINTS_KEY);
		initializeOverlay();

	}

	/**
	 * initializes and installs the point overlay using the data loaded into the
	 * points arrayList
	 */
	private void initializeOverlay() {
		GeoPoint firstPoint = null;
		if (points != null) {
			overlay = new PointsOfInterestOverlay(this);
			for (int i = 0; i < points.size(); i++) {
				PointOfInterest dto = points.get(i);
				if (dto.getLatitude() != null && dto.getLongitude() != null) {
					if (firstPoint == null) {
						firstPoint = GeoUtil.convertToPoint(dto.getLatitude(),
								dto.getLongitude());
					}
					overlay.addLocation(GeoUtil.convertToPoint(dto
							.getLatitude(), dto.getLongitude()));
				}
			}
			List<Overlay> overlays = mapView.getOverlays();
			overlays.add(overlay);
		}
		if (firstPoint != null) {
			mapController.setCenter(firstPoint);
		}
	}

	/**
	 * returns the point stored at the index passed in. Null is returned if the
	 * index is invalid
	 * 
	 * @param idx
	 * @return
	 */
	public PointOfInterest getPoint(int idx) {
		if (idx > -1 && points != null && points.size() > idx) {
			return points.get(idx);
		} else {
			return null;
		}
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

}
