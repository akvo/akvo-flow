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

							dialog.dismiss();
						}
					});
			builder.show();
			return true;

		}
		return false;
	}
}
