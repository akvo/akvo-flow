/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.user.domain;

import java.util.List;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * User of the web application. This object also can be used to persist (via a cascade save)
 * UserConfig.
 *
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class User extends BaseDomain {

    private static final long serialVersionUID = -1416095159769575254L;

    private String userName;

    private String emailAddress;
    private String permissionList;
    private Boolean superAdmin;
    @Persistent
    private List<UserConfig> config;
    private String accessKey;
    private String secret;
    private String language;

    public String getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(String permissionList) {
        this.permissionList = permissionList;
    }

    public List<UserConfig> getConfig() {
        return config;
    }

    public void setConfig(List<UserConfig> config) {
        this.config = config;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean isSuperAdmin() {
        return Boolean.TRUE.equals(superAdmin);
    }

    public void setSuperAdmin(Boolean superAdmin) {
        this.superAdmin = superAdmin;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        this.language = lang;
    }

    public String getEmailUserName() {
        if (emailAddress == null || emailAddress.trim().equals("")) {
            return null;
        }

        return emailAddress.substring(0, emailAddress.indexOf('@'));
    }
}
