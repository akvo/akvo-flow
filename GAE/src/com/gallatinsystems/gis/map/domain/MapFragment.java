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

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class MapFragment extends BaseDomain {

    /**
	 * 
	 */
    private static final long serialVersionUID = -5481653620580874640L;
    private String code = null;
    private String displayName = null;
    private String displayDesc = null;
    private String countryCode = null;
    private Blob blob = null;

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTechnologyType() {
        return technologyType;
    }

    public void setTechnologyType(String technologyType) {
        this.technologyType = technologyType;
    }

    private String technologyType = null;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayDesc() {
        return displayDesc;
    }

    public void setDisplayDesc(String displayDesc) {
        this.displayDesc = displayDesc;
    }

    public FRAGMENTTYPE getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(FRAGMENTTYPE fragmentType) {
        this.fragmentType = fragmentType;
    }

    public Text getFragmentValue() {
        return fragmentValue;
    }

    public void setFragmentValue(Text fragmentValue) {
        this.fragmentValue = fragmentValue;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    private FRAGMENTTYPE fragmentType = null;
    private Text fragmentValue = null;
    private Long parentId = null;

    public enum FRAGMENTTYPE {
        COUNTRY_INDIVIDUAL_PLACEMARK, COUNTRY_TECH_PLACEMARK_LIST, COUNTRY_ALL_PLACEMARKS, GLOBAL_ALL_PLACEMARKS, PLACEMARK
    }

}
