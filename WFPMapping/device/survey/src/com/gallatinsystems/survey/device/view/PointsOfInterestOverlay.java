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

package com.gallatinsystems.survey.device.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.activity.PointOfInterestMapActivity;
import com.gallatinsystems.survey.device.domain.PointOfInterest;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * Overlay class for displaying point of interest information
 * 
 * @author Christopher Fagiani
 * 
 */
public class PointsOfInterestOverlay extends GeoOverlay {

	private PointOfInterestMapActivity pointActivity;

	public PointsOfInterestOverlay(PointOfInterestMapActivity pointActivity) {
		super(false);
		this.pointActivity = pointActivity;

	}

	/**
	 * when the user taps the overlay, determine which point is closest. If the
	 * closest point is within MINIMUM_DISTANCE (normalized for the zoom level)
	 * then display the point information
	 */
	@Override
	public boolean onTap(GeoPoint point, final MapView mapView) {

		final int matchingPointIndex = getClosestPointIndex(point, mapView
				.getZoomLevel());
		if (matchingPointIndex > -1) {
			Context ctx = mapView.getContext();
			PointOfInterest pointDetails = pointActivity
					.getPoint(matchingPointIndex);
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle(R.string.pointofinterestitle);
			LinearLayout layout = new LinearLayout(ctx);
			layout.setOrientation(LinearLayout.VERTICAL);
			TextView nameText = new TextView(ctx);
			
			nameText.setText(pointDetails.getName());
			layout.addView(nameText);
			TextView typeText = new TextView(ctx);
			typeText.setText(pointDetails.getType());
			layout.addView(typeText);

			builder.setView(layout);
			builder.setPositiveButton(R.string.okbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							if(dialog != null){
								dialog.dismiss();
							}
						}
					});
			builder.show();
			return true;

		}
		return false;
	}
}
