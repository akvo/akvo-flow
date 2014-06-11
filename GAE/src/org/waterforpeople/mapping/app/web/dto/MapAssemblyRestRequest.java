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

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class MapAssemblyRestRequest extends RestRequest {
    private final String countryCodeParam = "countryCode";
    private final String techTypeCodeParam = "techType";

    private String countryCode = null;
    private String techType = null;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTechType() {
        return techType;
    }

    public void setTechType(String techType) {
        this.techType = techType;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 96808403833612464L;

    @Override
    protected void populateErrors() {

    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(countryCodeParam) != null)
            setCountryCode(req.getParameter(countryCodeParam));
        if (req.getParameter(techTypeCodeParam) != null)
            setTechType(req.getParameter(techTypeCodeParam));
    }

}
