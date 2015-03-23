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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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

    private static String PROJECT_FOLDER_URI_PREFIX = Permission.PROJECT_FOLDER_CREATE
            .getUriPrefix();

    private static String FORM_URI_PREFIX = Permission.FORM_CREATE.getUriPrefix();

    private static String SURVEY_RESPONSE_URI_PREFIX = Permission.DATA_DELETE.getUriPrefix();

    private static String URI_SUFFIX = "/(\\d*)";

    private static final Pattern URI_PATTERN = Pattern.compile("(" + PROJECT_FOLDER_URI_PREFIX
            + "|" + FORM_URI_PREFIX + "|" + SURVEY_RESPONSE_URI_PREFIX + ")(" + URI_SUFFIX + ")?");

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
        String requestUri = securedObject.getRequestUrl();

        // abstain from voting
        if (abstainVote(authentication, securedObject)) {
            return ACCESS_ABSTAIN;
        }

        if (requestUri.startsWith(PROJECT_FOLDER_URI_PREFIX)
                || requestUri.startsWith(FORM_URI_PREFIX)) {
            return voteFolderSurveyUri(authentication, securedObject);
        } else if (requestUri.startsWith(SURVEY_RESPONSE_URI_PREFIX)) {
            return voteSurveyResponseUri(authentication, securedObject);
        }

        // catchall access denied
        return ACCESS_DENIED;
    }

    /**
     * Checks the secured object passed in via the request and determines whether the
     * RequestUriVoter will abstain from voting for an access decision
     *
     * @param securedObject
     * @return
     */
    private boolean abstainVote(Authentication authentication, FilterInvocation securedObject) {
        // requester is a super admin user no need to control access or
        // request URL does not match the URI patterns we consider for voting
        return authentication.getAuthorities().contains(AppRole.SUPER_ADMIN)
                || !URI_PATTERN.matcher(securedObject.getRequestUrl()).find();
    }

    /**
     * Vote access decision for requests to the projects/folders/surveys URIs
     *
     * @param authentication
     * @param securedObject
     * @return
     */
    private int voteFolderSurveyUri(Authentication authentication, FilterInvocation securedObject) {
        HttpServletRequest httpRequest = securedObject.getHttpRequest();
        String httpMethod = securedObject.getHttpRequest().getMethod();
        String requestUri = securedObject.getRequestUrl();
        String resourcePath = null;

        if ("GET".equals(httpMethod)) {
            if (requestUri.equals(PROJECT_FOLDER_URI_PREFIX) || requestUri.equals(FORM_URI_PREFIX)) {
                // if no specific object (id) requested, abstain from voting. filtering is done
                // via the UserAuthorizationDao.listByUserAuthorization()
                return ACCESS_ABSTAIN;
            }
            resourcePath = retrieveResourcePathFromDataStore(securedObject);
        } else if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            resourcePath = retrieveResourcePathFromPayload(httpRequest);
        } else if ("DELETE".equals(httpMethod)) {
            resourcePath = retrieveResourcePathFromDataStore(securedObject);
        }

        return checkUserAuthorization(authentication, securedObject, resourcePath);
    }

    /**
     * Take the request URI and parse to extract the object Id and the entity kind of the survey or
     * survey group being accessed
     *
     * @param requestUri
     * @return
     */
    private Map<String, Object> parseSecuredObject(FilterInvocation securedObject) {
        String requestUri = securedObject.getRequestUrl();
        HttpServletRequest httpRequest = securedObject.getHttpRequest();
        String objectIdStr = null;
        Map<String, Object> params = new HashMap<String, Object>();

        // first check for relevant parameters if they are present in request starting with survey
        // id which is more specific, then attempt match object id in request URI for surveys and
        // survey_groups requests
        if (httpRequest.getParameter("surveyId") != null) {
            objectIdStr = httpRequest.getParameter("surveyId");
            params.put("entityKind", "Survey");
        } else if (httpRequest.getParameter("surveyGroupId") != null) {
            objectIdStr = httpRequest.getParameter("surveyGroupId");
            params.put("entityKind", "SurveyGroup");
        } else if (requestUri.startsWith(PROJECT_FOLDER_URI_PREFIX)
                || requestUri.startsWith(FORM_URI_PREFIX)) {
            Matcher formFolderMatcher = URI_PATTERN.matcher(requestUri);
            if (formFolderMatcher.matches()) {
                objectIdStr = formFolderMatcher.group(2);
                String entityKind = requestUri.startsWith(PROJECT_FOLDER_URI_PREFIX) ? "SurveyGroup"
                        : "Survey";
                params.put("entityKind", entityKind);

            }
        }

        if (StringUtils.isNotBlank(objectIdStr)) {
            try {
                Long objectId = Long.valueOf(objectIdStr);
                params.put("objectId", objectId);
            } catch (NumberFormatException e) {
                log.fine("Not able to convert to  objectId string: " + objectIdStr);
            }
        }
        return params;
    }

    /**
     * Check the authorization of a user based on an identified path
     *
     * @param authentication
     * @param resourcePath
     * @return
     */
    private int checkUserAuthorization(Authentication authentication,
            FilterInvocation securedObject, String resourcePath) {
        Long userId = (Long) authentication.getCredentials();

        if (resourcePath == null || userId == null) {
            // no path found
            throw new AccessDeniedException(
                    "Access is Denied. Unable to identify object path or user");
        }

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

        Permission permission = Permission.lookup(securedObject.getHttpRequest().getMethod(),
                securedObject.getRequestUrl());
        for (UserRole role : authorizedRoles) {
            if (role.getPermissions().contains(permission)) {
                return ACCESS_GRANTED;
            }
        }

        // catchall access denied
        return ACCESS_DENIED;
    }

    /**
     * Vote access decision for requests to survey response URIs
     *
     * @param authentication
     * @param securedObject
     * @return
     */
    private int voteSurveyResponseUri(Authentication authentication, FilterInvocation securedObject) {
        String httpMethod = securedObject.getHttpRequest().getMethod();
        String requestUri = securedObject.getRequestUrl();
        String resourcePath = null;

        if ("GET".equals(httpMethod)) {
            if (requestUri.equals(SURVEY_RESPONSE_URI_PREFIX)) {
                // if no specific id is requested, abstain from voting. filtering is done
                // via the UserAuthorizationDao.listByUserAuthorization()
                return ACCESS_ABSTAIN;
            }
            resourcePath = retrieveResourcePathFromDataStore(securedObject);
        } else if ("DELETE".equals(httpMethod)) {
            resourcePath = retrieveResourcePathFromDataStore(securedObject);
        } else if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            // no post or put for survey instances is allowed via rest API at the moment
            log.info("A POST or PUT of survey responses is not a supported operation at the moment");
            return ACCESS_DENIED;
        }

        return checkUserAuthorization(authentication, securedObject, resourcePath);
    }

    /**
     * Retrieve the resource path for a secured object that is being accessed. The path is retrieved
     * from the posted payload. Throws an @{link AccessDeniedException} in cases where a path should
     * have been found but is missing.
     *
     * @param securedObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String retrieveResourcePathFromPayload(HttpServletRequest httpRequest)
            throws AccessDeniedException {

        if (httpRequest.getContentType() == null
                || !httpRequest.getContentType().startsWith("application/json")
                || httpRequest.getContentLength() == 0) {
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

        return (String) content.get("path");
    }

    /**
     * Retrieve the resource path for a secured object that is being accessed. The path is retrieved
     * from the datastore. Throws an @{link AccessDeniedException} in cases where a path should have
     * been found but is missing.
     *
     * @param securedObject
     * @return
     */
    private String retrieveResourcePathFromDataStore(FilterInvocation securedObject) {
        String resourcePath = null;

        Map<String, Object> requestParameters = parseSecuredObject(securedObject);
        String entityKind = (String) requestParameters.get("entityKind");
        Long objectId = (Long) requestParameters.get("objectId");

        if (entityKind == null || objectId == null) {
            return null;
        }

        if (entityKind.equals("Survey")) {
            Survey s = surveyDao.getByKey(objectId);
            if (s != null) {
                resourcePath = s.getPath();
            }
        } else if (entityKind.equals("SurveyGroup")) {
            SurveyGroup sg = surveyGroupDao.getByKey(objectId);
            if (sg != null) {
                resourcePath = sg.getPath();
            }
        } else {
            return null;
        }

        // expecting object with valid path
        if (resourcePath == null) {
            throw new AccessDeniedException(
                    "Access is Denied. Unable to identify object path");
        }

        return resourcePath;
    }
}
