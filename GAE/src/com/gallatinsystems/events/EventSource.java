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

package com.gallatinsystems.events;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;

public class EventSource {

    public enum EventSourceType {
        USER, DEVICE, SENSOR, WEBFORM, API
    };

    private EventSourceType type;
    private Long id;
    private Map<String, Object> data = null;

    public EventSource(EventSourceType type, Long id) {
        this.setType(type);
        this.setId(id);
    }

    public EventSourceType getType() {
        return type;
    }

    public void setType(EventSourceType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}