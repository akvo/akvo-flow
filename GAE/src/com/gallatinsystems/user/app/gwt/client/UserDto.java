/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.user.domain.UserRole;

/**
 * dto representing the user objects. When returned, this object usually will have a set of
 * UserConfigDtos enumerating the per-user configuration as well as a list of UserPermissionDtos
 *
 * @author Christopher Fagiani
 */
public class UserDto extends BaseDto {

    private static final long serialVersionUID = -61713350825542379L;

    private String userName;
    private String emailAddress;
    private Map<String, Set<UserConfigDto>> config;
    private String logoutUrl;
    private boolean hasAccess = true;
    private boolean admin = false;
    private boolean superAdmin = false;
    private String permissionList;
    private String accessKey;
    private List<UserRole> roles;

    public String getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(String permissionList) {
        this.permissionList = permissionList;
    }

    public boolean isAdmin() {

        return hasPermission(PermissionConstants.ADMIN);
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean isSuper) {
        this.superAdmin = isSuper;
    }

    public void setAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }

    public boolean hasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public Map<String, Set<UserConfigDto>> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Set<UserConfigDto>> config) {
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

    public boolean hasPermission(String permission) {
        if (admin) {
            // admins automatically get all permissions
            return true;
        } else {
            if (permissionList != null) {
                return (permissionList.contains(permission) || permissionList
                        .contains(PermissionConstants.ADMIN));
            } else {
                return false;
            }
        }
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }
}
