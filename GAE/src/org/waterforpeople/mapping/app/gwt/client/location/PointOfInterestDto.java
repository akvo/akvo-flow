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

package org.waterforpeople.mapping.app.gwt.client.location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * general dto that can hold arbitrary data tied to a location
 * 
 * @author Christopher Fagiani
 */
public class PointOfInterestDto implements Serializable {

    private static final long serialVersionUID = -8505831823416882347L;
    private String type;
    private String name;
    private String country;
    private Double latitude;
    private Double longitude;
    private Long id;
    private List<String> propertyNames;
    private List<String> propertyValues;

    public PointOfInterestDto() {
        propertyNames = new ArrayList<String>();
        propertyValues = new ArrayList<String>();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public List<String> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<String> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void setPropertyNames(ArrayList<String> propertyNames) {
        this.propertyNames = propertyNames;
    }

    public void addProperty(String name, String value) {
        propertyNames.add(name);
        propertyValues.add(value != null ? value : "");
    }
}
