package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class GeoPlotOverlay extends Overlay {
	private static int vertexRadius = 7;
	private ArrayList<GeoPoint> verticies;
	private Paint fgPaint;
	private Paint bgPaint;

	public GeoPlotOverlay(Context ctx) {
		super();
		verticies = new ArrayList<GeoPoint>();

		bgPaint = new Paint();
		bgPaint.setARGB(200, 200, 200, 200);
		bgPaint.setAntiAlias(true);

		fgPaint = new Paint();
		fgPaint.setARGB(255, 10, 10, 255);
		fgPaint.setAntiAlias(true);
		fgPaint.setFakeBoldText(true);
	}

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

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
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
