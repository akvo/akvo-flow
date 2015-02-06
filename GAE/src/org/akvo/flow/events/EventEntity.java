/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.events;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;

public class EventEntity {
    public enum EventType {
        SURVEY, FORM, QUESTION_GROUP, QUESTION, DATAPOINT, FORM_INSTANCE, ANSWER
    };

    private EventType type;
	private Long id;
    private Map<String, Object> data = null;

    public EventEntity(EventType type, Long id) {
        this.setType(type);
        this.setId(id);
    }

    public EventType getType() {
		return type;
	}

    public void setType(EventType type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    @JsonAnyGetter
	public Map<String,Object> getData() {
		return data;
	}

	public void setData(Map<String,Object> data) {
		this.data = data;
	}
}