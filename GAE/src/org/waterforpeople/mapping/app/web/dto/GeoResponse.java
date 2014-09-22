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

package org.waterforpeople.mapping.app.web.dto;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;

import com.gallatinsystems.framework.rest.RestResponse;

/**
 * responses from GeoServlet
 * 
 * @author Christopher Fagiani
 */
public class GeoResponse extends RestResponse {

    private static final long serialVersionUID = -4053566733640591308L;
    private List<CountryDto> countries;
    private List<CommunityDto> communities;

    public List<CountryDto> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryDto> countries) {
        this.countries = countries;
    }

    public List<CommunityDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunityDto> communities) {
        this.communities = communities;
    }

}
