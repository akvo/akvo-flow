/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.CascadeNodeDto;

public class CascadeNodeBulkPayload implements Serializable {

    private static final long serialVersionUID = 48606518252857075L;
    List<CascadeNodeDto> cascade_nodes = null;

    public List<CascadeNodeDto> getCascade_nodes() {
        return cascade_nodes;
    }

    public void setCascade_nodes(List<CascadeNodeDto> cascade_nodes) {
        this.cascade_nodes = cascade_nodes;
    }
}
