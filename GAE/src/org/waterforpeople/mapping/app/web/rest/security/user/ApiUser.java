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

package org.waterforpeople.mapping.app.web.rest.security.user;

import java.io.Serializable;

public class ApiUser implements Serializable {

    private static final long serialVersionUID = -8372182998303162190L;
    private String userName;
    private String accessKey;
    private String secret;
    private Long userId;

    public ApiUser(String userName, String accessKey, String secret, Long userId) {

        this.setUserName(userName);
        this.setAccessKey(accessKey);
        this.setSecret(secret);
        this.userId = userId;
    }

    public ApiUser() {
        this.setUserName("");
        this.setAccessKey("");
        this.setSecret("");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
