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

package com.gallatinsystems.gis.map.domain;

import java.util.ArrayList;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Geometry extends BaseDomain {

    /**
	 * 
	 */
    private static final long serialVersionUID = -694616882164561459L;
    private GeometryType type = null;
    private ArrayList<Coordinate> coordinates = null;
    private Text wktText = null;
    private Double centroidLat = null;
    private Double centroidLon = null;
    @NotPersistent
    private com.vividsolutions.jts.geom.Coordinate[] boundingBox = null;

    public Double getCentroidLat() {
        return centroidLat;
    }

    public void setCentroidLat(Double centroidLat) {
        this.centroidLat = centroidLat;
    }

    public Double getCentroidLon() {
        return centroidLon;
    }

    public void setCentroidLon(Double centroidLon) {
        this.centroidLon = centroidLon;
    }

    public GeometryType getType() {
        return type;
    }

    public void setType(GeometryType type) {
        this.type = type;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(Double x, Double y) {
        if (coordinates == null)
            coordinates = new ArrayList<Coordinate>();
        Coordinate coordinate = new Coordinate(x, y);
        coordinates.add(coordinate);
    }

    public void setWktText(String wktText) {
        this.wktText = new Text(wktText);
    }

    public String getWktText() {
        if (wktText != null)
            return wktText.getValue();
        else
            return null;
    }

    public void setBoundingBox(com.vividsolutions.jts.geom.Coordinate[] boundingBox) {
        this.boundingBox = boundingBox;
    }

    public com.vividsolutions.jts.geom.Coordinate[] getBoundingBox() {
        return boundingBox;
    }

    public enum GeometryType {
        MULITPOLYGON, POLYGON, POINT, RECTANGLE
    }
}
