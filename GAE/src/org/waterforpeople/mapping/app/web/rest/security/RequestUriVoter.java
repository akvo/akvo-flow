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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;

public class RequestUriVoter implements AccessDecisionVoter<FilterInvocation> {

    private static final Logger log = Logger.getLogger(RequestUriVoter.class.getName());

    private static String projectFolderUriPrefix = Permission.PROJECT_FOLDER_CREATE.getUriPrefix();

    private static String formUriPrefix = Permission.FORM_CREATE.getUriPrefix();

    private static final Pattern URI_PATTERN = Pattern.compile("(" + projectFolderUriPrefix + "|"
            + formUriPrefix + ")(/(\\d*))?");

    @Inject
    private UserRoleDao userRoleDao;

    @Inject
    private UserAuthorizationDAO userAuthorizationDao;

    @Inject
    private SurveyGroupDAO surveyGroupDao;

    @Inject
    private SurveyDAO surveyDao;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(FilterInvocation.class);
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation securedObject,
            Collection<ConfigAttribute> attributes) {

        HttpServletRequest httpRequest = securedObject.getHttpRequest();
        String requestUri = securedObject.getRequestUrl();
        String httpMethod = httpRequest.getMethod();

        // do not filter super admin requests
        if (authentication.getAuthorities().contains(AppRole.SUPER_ADMIN)) {
            return ACCESS_ABSTAIN;
        }

        // for now we only vote for request access on project folders and forms
        if (!URI_PATTERN.matcher(requestUri).find()) {
            return ACCESS_ABSTAIN;
        }

        String resourcePath = retrieveResourcePath(securedObject);
        if (resourcePath == null) {
            // enables listing folders and forms
            return ACCESS_ABSTAIN;
        }

        Long userId = (Long) authentication.getCredentials();

        // retrieve user authorizations containing resource paths that make up this one
        List<UserAuthorization> authorizations = userAuthorizationDao.listByObjectPath(userId,
                resourcePath);
        if (authorizations.isEmpty()) {
            throw new AccessDeniedException("Access is Denied. Insufficient permissions");
        }

        List<Long> authorizedRoleIds = new ArrayList<Long>();
        for (UserAuthorization auth : authorizations) {
            authorizedRoleIds.add(auth.getRoleId());
        }

        List<UserRole> authorizedRoles = userRoleDao.listByKeys(authorizedRoleIds
                .toArray(new Long[0]));

        Permission permission = Permission.lookup(httpMethod, requestUri);
        for (UserRole role : authorizedRoles) {
            if (role.getPermissions().contains(permission)) {
                return ACCESS_GRANTED;
            }
        }

        throw new AccessDeniedException("Access is Denied. Insufficient permissions");
    }

    /**
     * Retrieve the resource path for a secured object that is being accessed. The path is either
     * retrieved from the posted payload or from the datastore. Throws an @{link
     * AccessDeniedException} in cases where a path should have been found but is missing
     *
     * @param securedObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String retrieveResourcePath(FilterInvocation securedObject)
            throws AccessDeniedException {

        HttpServletRequest httpRequest = securedObject.getHttpRequest();
        String httpMethod = httpRequest.getMethod();
        String requestUri = securedObject.getRequestUrl();
        String resourcePath = null;

        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            if ((httpRequest.getContentType() != null
                    && !httpRequest.getContentType().startsWith("application/json"))
                    || (httpRequest.getContentLength() == 0)) {
                return null; // if not JSON payload, conversion exception will be thrown later
            }

            ObjectMapper mapper = new ObjectMapper();
            Map payload = null;
            try {
                payload = mapper.readValue(httpRequest.getInputStream(), Map.class);
            } catch (IOException e) {
                log.severe(e.toString());
            }

            Map content = null;
            if (payload.containsKey("survey_group")) {
                content = (Map) payload.get("survey_group");
            } else {
                content = (Map) payload.get("survey");
            }

            if (content == null || !content.containsKey("path")) {
                throw new AccessDeniedException("Access is Denied. Unable to identify object path");
            }
            resourcePath = (String) content.get("path");
        } else {
            Matcher matcher = URI_PATTERN.matcher(requestUri);
            if (matcher.find()) {
                String objectIdStr = matcher.group(3);
                Long objectId = null;
                if (objectIdStr == null && httpRequest.getParameter("surveyGroupId") == null) {
                    return null; // enable return of top level list of project folders
                } else if (objectIdStr != null) {
                    objectId = Long.valueOf(objectIdStr);
                } else {
                    objectId = Long.valueOf(httpRequest.getParameter("surveyGroupId"));
                }

                if (requestUri.contains("surveys") && objectIdStr != null) {
                    Survey s = surveyDao.getByKey(objectId);
                    if (s != null) {
                        resourcePath = s.getPath();
                    }
                } else {
                    SurveyGroup sg = surveyGroupDao.getByKey(objectId);
                    if (sg != null) {
                        resourcePath = sg.getPath();
                    }
                }

                // expecting object with valid path
                if (resourcePath == null) {
                    throw new AccessDeniedException(
                            "Access is Denied. Unable to identify object path");
                }

            }
        }
        return resourcePath;
    }
}
