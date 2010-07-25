package com.gallatinsystems.survey.device.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.activity.RegionPlotActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * This overlay is used to display all the way points for a region plot
 * connected by a thin line. When the user taps a point, a confirmation box will
 * appear asking the user if they'd like to delete that point.
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoPlotOverlay extends GeoOverlay {

	private RegionPlotActivity plotActivity;

	public GeoPlotOverlay(RegionPlotActivity ctx) {
		super(true);
		plotActivity = ctx;
	}

	/**
	 * when the user taps the overlay, determine which point is closest. If the
	 * closest point is within MINIMUM_DISTANCE (normalized for the zoom level)
	 * then prompt the user for an action (delete or cancel). If they select
	 * delete, remove the point from the plot.
	 */
	@Override
	public boolean onTap(GeoPoint point, final MapView mapView) {

		final GeoPoint evictionCandidate = getClosestPoint(point, mapView
				.getZoomLevel());
		if (evictionCandidate != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mapView
					.getContext());
			TextView tipText = new TextView(mapView.getContext());
			tipText.setText(R.string.deletepointdialog);
			builder.setView(tipText);
			builder.setPositiveButton(R.string.okbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							int index = removePoint(evictionCandidate);
							if (index > -1) {
								plotActivity.deletePoint(index);
								mapView.invalidate();
							}
							dialog.dismiss();
						}
					});
			builder.setNegativeButton(R.string.cancelbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder.show();
			return true;

		}
		return false;
	}
}
