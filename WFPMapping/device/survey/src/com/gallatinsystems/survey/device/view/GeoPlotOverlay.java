package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.RegionPlotActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This overlay is used to display all the way points for a region plot
 * connected by a thin line. When the user taps a point, a confirmation box will
 * appear asking the user if they'd like to delete that point.
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoPlotOverlay extends Overlay {
	private static int vertexRadius = 7;
	private static int MAX_ZOOM = 20;
	private static final int MAXIMUM_DISTANCE = 400;
	private ArrayList<GeoPoint> verticies;
	private Paint fgPaint;
	private Paint bgPaint;
	private RegionPlotActivity plotActivity;

	public GeoPlotOverlay(RegionPlotActivity ctx) {
		super();
		plotActivity = ctx;
		verticies = new ArrayList<GeoPoint>();

		bgPaint = new Paint();
		bgPaint.setARGB(200, 200, 200, 200);
		bgPaint.setAntiAlias(true);

		fgPaint = new Paint();
		fgPaint.setARGB(255, 10, 10, 255);
		fgPaint.setAntiAlias(true);
		fgPaint.setFakeBoldText(true);
	}

	/**
	 * draw each point in the order they were added connected by lines.
	 */
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// Get the map projection to convert lat/long to screen coordinates
		Projection projection = mapView.getProjection();
		// Draw the region
		Point lastPoint = null;
		if (shadow == false) {

			for (int i = 0; i < verticies.size(); i++) {

				GeoPoint geopoint = verticies.get(i);
				Point point = new Point();
				projection.toPixels(geopoint, point);

				RectF oval = new RectF(point.x - vertexRadius, point.y
						- vertexRadius, point.x + vertexRadius, point.y
						+ vertexRadius);

				if (lastPoint != null) {
					canvas.drawLine(lastPoint.x, lastPoint.y, point.x, point.y,
							fgPaint);
				}

				canvas.drawOval(oval, bgPaint);
				oval.inset(2, 2);
				canvas.drawOval(oval, fgPaint);
				lastPoint = point;
			}
		}
		super.draw(canvas, mapView, shadow);
	}

	/**
	 * when the user taps the overlay, determine which point is closest. If the
	 * closest point is within MINIMUM_DISTANCE (normalized for the zoom level)
	 * then prompt the user for an action (delete or cancel). If they select
	 * delete, remove the point from the plot.
	 */
	@Override
	public boolean onTap(GeoPoint point, final MapView mapView) {
		// find the closest point to the tap location
		GeoPoint closest = null;
		double distance = 9999999d;
		for (int i = 0; i < verticies.size(); i++) {
			if (closest == null) {
				closest = verticies.get(i);
			} else {
				double newDist = Math.sqrt(Math.pow(verticies.get(i)
						.getLatitudeE6()
						- point.getLatitudeE6(), 2d)
						+ Math.pow(verticies.get(i).getLongitudeE6()
								- point.getLongitudeE6(), 2d));
				if (newDist <= distance) {
					distance = newDist;
					closest = verticies.get(i);
				}
			}
		}
		final int evictionCandidate = verticies.indexOf(closest);
		if (distance < (MAXIMUM_DISTANCE * (MAX_ZOOM - mapView.getZoomLevel()))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mapView
					.getContext());
			TextView tipText = new TextView(mapView.getContext());
			tipText.setText(R.string.deletepointdialog);
			builder.setView(tipText);
			builder.setPositiveButton(R.string.okbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							verticies.remove(evictionCandidate);
							plotActivity.deletePoint(evictionCandidate);
							mapView.invalidate();
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

	public void addLocation(GeoPoint loc) {
		verticies.add(loc);
	}

	public void addLocation(Location loc) {
		Double latitude = loc.getLatitude() * 1E6;
		Double longitude = loc.getLongitude() * 1E6;
		verticies.add(new GeoPoint(latitude.intValue(), longitude.intValue()));
	}

}
