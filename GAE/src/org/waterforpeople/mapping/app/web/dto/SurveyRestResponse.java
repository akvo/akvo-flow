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

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * response class for the survey rest servlet. This response object can contain a list of objects
 * that are somehow related to the survey tree. The list will always be homogeneous but the actual
 * type of the objects in the list depend on the method called.
 * 
 * @author Christopher Fagiani
 */
public class SurveyRestResponse extends RestResponse {

    private static final long serialVersionUID = -3851323551471422767L;
    private String cursor = null;
    private String url;
    private List<? extends BaseDto> dtoList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<? extends BaseDto> getDtoList() {
        return dtoList;
    }

    public void setDtoList(List<? extends BaseDto> dtoList) {
        this.dtoList = dtoList;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public String getCursor() {
        return cursor;
    }
}
