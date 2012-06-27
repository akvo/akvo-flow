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

package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;
import java.util.List;
public class MapBalloonItemDefinitionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7213375365770068948L;
	/**
	 * 
	 */
	private Long keyId = null;
	private List<MapBalloonRowDefinitionDto> rows = null;
	private Integer displayOrder = null;
	private Long parentId = null;
	
	public List<MapBalloonRowDefinitionDto> getRows() {
		return rows;
	}
	public void setRows(List<MapBalloonRowDefinitionDto> row) {
		this.rows = row;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public Long getKeyId() {
		return keyId;
	}

}
