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
public class SpreadsheetContainer extends BaseDomain {

    private static final long serialVersionUID = -6799010214926782777L;

    private String spreadsheetName;
    private Integer RowContainerCount;
    private Integer colCount;
    private ArrayList<RowContainer> RowContainerList;

    public String getSpreadsheetName() {
        return spreadsheetName;
    }

    public void setSpreadsheetName(String spreadsheetName) {
        this.spreadsheetName = spreadsheetName;
    }

    public Integer getRowContainerCount() {
        return RowContainerCount;
    }

    public void setRowContainerCount(Integer RowContainerCount) {
        this.RowContainerCount = RowContainerCount;
    }

    public Integer getColCount() {
        return colCount;
    }

    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    public void setRowContainerList(ArrayList<RowContainer> RowContainerList) {
        this.RowContainerList = RowContainerList;
    }

    public ArrayList<RowContainer> getRowContainerList() {
        return RowContainerList;
    }

    public void addRowContainer(RowContainer RowContainer) {
        if (RowContainerList == null) {
            RowContainerList = new ArrayList<RowContainer>();
        }
        RowContainerList.add(RowContainer);
    }

}
