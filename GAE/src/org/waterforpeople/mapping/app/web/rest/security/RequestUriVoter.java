/*
 *  Copyright (C) 2014-2015 Stichting Akvo (Akvo Foundation)
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
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import org.akvo.flow.domain.SecuredObject;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
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

    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("("
            + PROJECT_FOLDER_URI_PREFIX + "|" + FORM_URI_PREFIX + "|" + SURVEY_RESPONSE_URI_PREFIX
            + ")(" + URI_SUFFIX + ")");

    private static final int PREFIX_GROUP = 1;

    private static final int OBJECT_ID_GROUP = 3;

    @Inject
    private UserRoleDao userRoleDao;

    @Inject
    private UserAuthorizationDAO userAuthorizationDao;

    @Inject
    private SurveyGroupDAO surveyGroupDao;

    @Inject
    private SurveyDAO surveyDao;

    @Inject
    private SurveyInstanceDAO surveyInstanceDao;

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
        if (authentication.getCredentials() == null) {
            // immediately deny access if unable to identify user
            throw new AccessDeniedException(
                    "Access is Denied. Unable to identify user");
        }

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
        String httpMethod = securedObject.getHttpRequest().getMethod();
        String requestUri = securedObject.getRequestUrl();
        List<Long> ancestorIds = new ArrayList<Long>();

        if ("GET".equals(httpMethod)) {
            // get specific object (id), otherwise abstain from voting. filtering is done
            // via the BaseDAO.filterByUserAuthorizationObjectId()
            Long objectId = parseObjectId(requestUri);
            if (objectId == null) {
                return ACCESS_ABSTAIN;
            }
            ancestorIds.addAll(retrieveAncestorIdsFromDataStore(securedObject,
                    parseRequestPrefix(requestUri), objectId));
        } else if ("PUT".equals(httpMethod)) {
            ancestorIds.addAll(retrieveAncestorIdsFromDataStore(securedObject,
                    parseRequestPrefix(requestUri), parseObjectId(requestUri)));
        } else if ("POST".equals(httpMethod)) {
            ancestorIds.addAll(null);
        } else if ("DELETE".equals(httpMethod)) {
            ancestorIds.addAll(retrieveAncestorIdsFromDataStore(securedObject,
                    parseRequestPrefix(requestUri), parseObjectId(requestUri)));
        }
        // TODO: add survey/form id as well
        return checkUserAuthorization(authentication, securedObject, ancestorIds);
    }

    /**
     * Check the authorization of a user based on the ids of object being accessed or one of its
     * ancestors.
     *
     * @param authentication
     * @param resourcePath
     * @return
     */
    private int checkUserAuthorization(Authentication authentication,
            FilterInvocation securedObject, List<Long> objectIds) {
        Long userId = (Long) authentication.getCredentials();

        if (objectIds == null || objectIds.isEmpty()) {
            // no path found
            throw new AccessDeniedException(
                    "Access is Denied. Unable to identify object(s)");
        }

        // retrieve user authorizations containing resource paths that make up this one
        List<UserAuthorization> authorizations = userAuthorizationDao.listByObjectIds(userId,
                objectIds);
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

        List<Long> ancestorIds = new ArrayList<Long>();

        if ("GET".equals(httpMethod)) {
            Long objectId = parseObjectId(requestUri);
            if (objectId == null) {
                // if no specific id is requested, abstain from voting. filtering is done
                // via the BaseDAO.filterByUserAuthorizationObjectId()
                return ACCESS_ABSTAIN;
            }
            ancestorIds = retrieveAncestorIdsFromDataStore(securedObject,
                    parseRequestPrefix(requestUri), objectId);
        } else if ("DELETE".equals(httpMethod)) {
            ancestorIds = retrieveAncestorIdsFromDataStore(securedObject,
                    parseRequestPrefix(requestUri), parseObjectId(requestUri));
        } else if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            // no post or put for survey instances is allowed via rest API at the moment
            log.warning("A POST or PUT of survey responses is not a supported operation at the moment");
            return ACCESS_DENIED;
        }

        return checkUserAuthorization(authentication, securedObject, ancestorIds);
    }

    /**
     * Parse request prefix from the requestUri
     *
     * @param requestUri
     * @return
     */
    private String parseRequestPrefix(String requestUri) {
        Matcher requestUriMatcher = URI_PATTERN.matcher(requestUri);
        if (requestUriMatcher.find()) {
            return requestUriMatcher.group(PREFIX_GROUP);
        }
        return null;
    }

    /**
     * Identify requested object id from requestUri
     *
     * @param requestUri
     * @return
     */
    private Long parseObjectId(String requestUri) {
        Matcher objectIdMatcher = OBJECT_ID_PATTERN.matcher(requestUri);
        if (objectIdMatcher.find()) {
            String objectIdStr = objectIdMatcher.group(OBJECT_ID_GROUP);
            return Long.parseLong(objectIdStr);
        }
        return null;
    }

    /**
     * Retrieve the ancestorIds for a secured object from the datastore
     *
     * @param securedObject
     * @param requestPrefix
     * @param objectId
     * @return
     */
    private List<Long> retrieveAncestorIdsFromDataStore(FilterInvocation securedObject,
            String requestPrefix, Long objectId) {

        if (requestPrefix == null || objectId == null) {
            log.warning("Failed to identify request parameters requestPrefix=" + requestPrefix
                    + "; object=" + objectId);
            return Collections.emptyList();
        }

        List<Long> ancestorIds = new ArrayList<Long>();
        SecuredObject obj = null;
        if (SURVEY_RESPONSE_URI_PREFIX.equals(requestPrefix)) {
            obj = surveyInstanceDao.getByKey(objectId);
        } else if (FORM_URI_PREFIX.equals(requestPrefix)) {
            obj = surveyDao.getByKey(objectId);
        } else if (PROJECT_FOLDER_URI_PREFIX.equals(requestPrefix)) {
            obj = surveyGroupDao.getByKey(objectId);
        }

        if (obj != null) {
            ancestorIds.addAll(obj.listAncestorIds());
        }

        return ancestorIds;
    }
}
