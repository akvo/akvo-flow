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

package org.waterforpeople.mapping.app.web.rest.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import com.gallatinsystems.user.dao.UserAuthorizationDao;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;

public class RequestUriVoter implements AccessDecisionVoter<Object> {

    private static final Logger log = Logger.getLogger(RequestUriVoter.class.getName());

    @Inject
    private UserRoleDao userRoleDao;

    @Inject
    private UserAuthorizationDao userAuthorizationDao;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication, Object object,
            Collection<ConfigAttribute> attributes) {

        FilterInvocation securedObject = (FilterInvocation) object;
        String requestUri = securedObject.getRequestUrl();
        String httpMethod = securedObject.getHttpRequest().getMethod();

        Long userId = null; // retrieve from principal

        // for now we only vote for request access on project folders and forms
        if (!requestUri.startsWith(Permission.PROJECT_FOLDER_CREATE.getUriPrefix())
                || !requestUri.startsWith(Permission.FORM_CREATE.getUriPrefix())) {
            return ACCESS_ABSTAIN;
        }

        // retrieve the resource path from the payload
        String resourcePath = retrieveResourcePath(requestUri);
        if (resourcePath == null) {
            return ACCESS_ABSTAIN;
        }

        // retrieve user authorisations containing resource paths that make up this one
        List<UserAuthorization> authorizations = userAuthorizationDao.listByObjectPath(userId,
                resourcePath);
        if (authorizations.isEmpty()) {
            return ACCESS_DENIED;
        }

        List<Long> authorizedRoleIds = new ArrayList<Long>();
        for (UserAuthorization auth : authorizations) {
            authorizedRoleIds.add(auth.getRoleId());
        }

        List<UserRole> authorizedRoles = userRoleDao.listByKeys((Long[]) authorizedRoleIds
                .toArray());

        Permission permission = Permission.lookup(httpMethod, requestUri);
        for (UserRole role : authorizedRoles) {
            if (role.getPermissions().contains(permission)) {
                return ACCESS_GRANTED;
            }
        }

        return ACCESS_DENIED;
    }

    private String retrieveResourcePath(String requestUri) {
        return null; // TODO
    }
}
