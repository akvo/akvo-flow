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
import com.gallatinsystems.user.dao.UserAuthorizationDao;
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
    private UserAuthorizationDao userAuthorizationDao;

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
        Long userId = null; // TODO: retrieve from principal

        // for now we only vote for request access on project folders and forms
        if (!URI_PATTERN.matcher(requestUri).matches()) {
            return ACCESS_ABSTAIN;
        }

        String resourcePath = retrieveResourcePath(securedObject);
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

    /**
     * Retrieve the resource path for a secured object that is being accessed. The path is either
     * retrieved from the posted payload or from the datastore. Throws an @{link
     * AccessDeniedException} in cases where a path should have been found but is missing
     *
     * @param securedObject
     * @return
     */
    private String retrieveResourcePath(FilterInvocation securedObject)
            throws AccessDeniedException {

        HttpServletRequest httpRequest = securedObject.getHttpRequest();
        String httpMethod = httpRequest.getMethod();
        String requestUri = securedObject.getRequestUrl();
        String resourcePath = null;

        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            if (!"application/json".equals(httpRequest.getContentType())
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

            if (!payload.containsKey("path")) {
                log.info("object:  " + payload.toString());
                throw new AccessDeniedException("Access is Denied. Unable to identify object path");
            }
            resourcePath = (String) payload.get("path");
        } else {
            Matcher matcher = URI_PATTERN.matcher(requestUri);
            if (matcher.matches()) {
                String objectIdStr = matcher.group(3);
                if (objectIdStr == null) {
                    return null;
                }
                Long objectId = Long.valueOf(objectIdStr);
                if (requestUri.contains("survey_groups")) {
                    SurveyGroup sg = surveyGroupDao.getByKey(objectId);
                    if (sg != null) {
                        resourcePath = sg.getPath();
                    }
                } else {
                    Survey s = surveyDao.getByKey(objectId);
                    if (s != null) {
                        resourcePath = s.getPath();
                    }
                }
            }
        }
        return resourcePath;
    }
}
