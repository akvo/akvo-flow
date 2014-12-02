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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.user.domain.UserAuthorization;

/**
 * Wrapper and DTO class to hide the internals of @{link com.gallatinsystems.user.UserAuthorization}
 * class and pass data to and from REST requests
 *
 * @author emmanuel
 */
public class UserAuthorizationPayload extends BaseDto {

    private static final long serialVersionUID = -2483907342483369954L;

    private UserAuthorization userAuthorization;

    public UserAuthorizationPayload(UserAuthorization userAuthorization) {
        this.userAuthorization = userAuthorization;
        if (userAuthorization.getKey() != null) {
            this.setKeyId(userAuthorization.getKey().getId());
        }
    }

    public UserAuthorizationPayload() {
        this.userAuthorization = new UserAuthorization();
    }

    public Long getUserId() {
        return userAuthorization.getUserId();
    }

    public void setUserId(Long userId) {
        this.userAuthorization.setUserId(userId);
    }

    public Long getRoleId() {
        return userAuthorization.getRoleId();
    }

    public void setRoleId(Long roleId) {
        this.userAuthorization.setRoleId(roleId);
    }

    public String getObjectPath() {
        return userAuthorization.getObjectPath();
    }

    public void setObjectPath(String objectPath) {
        if (StringUtils.isBlank(objectPath)) {
            throw new IllegalArgumentException("No path specified in authorization");
        }
        this.userAuthorization.setObjectPath(objectPath);
    }

    @JsonIgnore
    public UserAuthorization getUserAuthorisation() {
        return userAuthorization;
    }
}
