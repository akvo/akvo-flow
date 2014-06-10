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
import java.util.List;

public class PlacemarkDtoResponse implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -2442244967159768511L;

    private List<PlacemarkDto> dtoList = null;
    private String cursor = null;

    public List<PlacemarkDto> getDtoList() {
        return dtoList;
    }

    public void setDtoList(List<PlacemarkDto> dtoList) {
        this.dtoList = dtoList;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
