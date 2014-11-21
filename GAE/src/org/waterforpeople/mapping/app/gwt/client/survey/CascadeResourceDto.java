/*  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class CascadeResourceDto extends BaseDto implements NamedObject {
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer version;
	private Boolean published;
	private List<String> levelNames;
	private Integer numLevels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
    public String getDisplayName() {
        return getName();
    }

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public List<String> getLevelNames() {
		return levelNames;
	}

	public void setLevelNames(List<String> levelNames) {
		this.levelNames = levelNames;
	}

	public Integer getNumLevels() {
		return numLevels;
	}

	public void setNumLevels(Integer numLevels) {
		this.numLevels = numLevels;
	}
}