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

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;

import com.gallatinsystems.survey.device.util.GeoUtil;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * base class for map overlays that supports finding the nearest point to
 * another point
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoOverlay extends Overlay {
	private static int MAX_ZOOM = 20;
	private static final int MAXIMUM_DISTANCE = 400;

	private static int vertexRadius = 7;

	private ArrayList<GeoPoint> verticies;
	private Paint fgPaint;
	private Paint bgPaint;
	private boolean connectPoints;

	public GeoOverlay() {
		verticies = new ArrayList<GeoPoint>();

		bgPaint = new Paint();
		bgPaint.setARGB(200, 200, 200, 200);
		bgPaint.setAntiAlias(true);

		fgPaint = new Paint();
		fgPaint.setARGB(255, 10, 10, 255);
		fgPaint.setAntiAlias(true);
		fgPaint.setFakeBoldText(true);
		connectPoints = false;
	}
	
	public GeoOverlay(boolean useLines){
		this();
		connectPoints = useLines;
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

				if (lastPoint != null && connectPoints) {
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
	 * returns the geo point nearest to the point passed in as long as it is
	 * within the MAX_DISTANCE from the point (normalized for the zoom level).
	 * If no point is within that radius, null is returned.
	 * 
	 * @param point
	 * @return
	 */
	public GeoPoint getClosestPoint(GeoPoint point, int currentZoom) {
		// find the closest point to the tap location
		GeoPoint closest = null;
		double distance = 9999999d;
		for (int i = 0; i < verticies.size(); i++) {
			if (closest == null) {
				closest = verticies.get(i);
				distance = GeoUtil.computeDistance(verticies.get(i),
							point);
			} else {
				double newDist = GeoUtil.computeDistance(verticies.get(i),
						point);
				if (newDist <= distance) {
					distance = newDist;
					closest = verticies.get(i);
				}
			}
		}
		if (distance < (MAXIMUM_DISTANCE * (MAX_ZOOM - currentZoom))) {
			return closest;
		} else {
			return null;
		}
	}

	/**
	 * returns the index of the point closest to the point argument passed (as
	 * long as it's within the MAX_DISTANCE). If nothing matches, then -1 is
	 * returned.
	 * 
	 * @param point
	 * @param currentZoom
	 * @return
	 */
	public int getClosestPointIndex(GeoPoint point, int currentZoom) {
		int idx = -1;
		GeoPoint pt = getClosestPoint(point, currentZoom);
		if (pt != null) {
			idx = verticies.indexOf(pt);
		}
		return idx;
	}

	/**
	 * removes a point from the internal list of points and returns the index in
	 * the point used to reside at in the internal list.
	 * 
	 * @param pt
	 */
	public int removePoint(GeoPoint pt) {
		int formerIndex = -1;
		if (pt != null && verticies != null) {
			formerIndex = verticies.indexOf(pt);
			verticies.remove(pt);
		}
		return formerIndex;
	}

	/**
	 * adds a point to the internal list
	 * 
	 * @param loc
	 */
	public void addLocation(GeoPoint loc) {
		verticies.add(loc);
	}

	/**
	 * adds a point to the internal list
	 * 
	 * @param loc
	 */
	public void addLocation(Location loc) {
		Double latitude = loc.getLatitude() * 1E6;
		Double longitude = loc.getLongitude() * 1E6;
		verticies.add(new GeoPoint(latitude.intValue(), longitude.intValue()));
	}

}
