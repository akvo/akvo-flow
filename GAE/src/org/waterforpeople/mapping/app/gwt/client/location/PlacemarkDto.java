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
import java.util.Date;

public class PlacemarkDto implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3151596501944117022L;
    private Double latitude = null;
    private Double longitude = null;
    private Long altitude = null;
    private String placemarkContents = null;
    private String iconUrl = null;
    private String communityCode = null;
    private String markType = null;
    private Date collectionDate = null;
    private String pinStyle = null;

    public String getPinStyle() {
        return pinStyle;
    }

    public void setPinStyle(String pinStyle) {
        this.pinStyle = pinStyle;
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

    public Long getAltitude() {
        return altitude;
    }

    public void setAltitude(Long altitude) {
        this.altitude = altitude;
    }

    public String getPlacemarkContents() {
        return placemarkContents;
    }

    public void setPlacemarkContents(String placemarkContents) {
        this.placemarkContents = placemarkContents;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    public String getCommunityCode() {
        return communityCode;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public String getMarkType() {
        return markType;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

}
