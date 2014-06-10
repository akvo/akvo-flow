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

package com.gallatinsystems.common.data.spreadsheet.domain;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class RowContainer extends BaseDomain {

    private static final long serialVersionUID = -5434469059154017308L;

    private ArrayList<ColumnContainer> ColumnContainersList;

    public void setColumnContainersList(
            ArrayList<ColumnContainer> ColumnContainersList) {
        this.ColumnContainersList = ColumnContainersList;
    }

    public ArrayList<ColumnContainer> getColumnContainersList() {
        return ColumnContainersList;
    }

    public void addColumnContainer(ColumnContainer col) {
        if (ColumnContainersList == null) {
            ColumnContainersList = new ArrayList<ColumnContainer>();
        }
        ColumnContainersList.add(col);
    }

}
