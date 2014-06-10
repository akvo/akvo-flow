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

package com.gallatinsystems.user.app.gwt.client;

import java.io.Serializable;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * Dto for returning userConfig values to the client. Used for representing things like the position
 * of portal widgets.
 * 
 * @author Christopher Fagiani
 */
public class UserConfigDto extends BaseDto implements Serializable {
    private static final long serialVersionUID = 4515497143926759239L;

    private String group;
    private String name;
    private String value;
    private Long userId;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * equality is defined as having the same group, name AND value
     */
    public boolean equals(Object other) {
        if (other instanceof UserConfigDto) {
            if (((UserConfigDto) other).group.equals(group)
                    && ((UserConfigDto) other).name.equals(name)
                    && ((value == null && ((UserConfigDto) other).value == null) || value
                            .equals(((UserConfigDto) other).value))) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
