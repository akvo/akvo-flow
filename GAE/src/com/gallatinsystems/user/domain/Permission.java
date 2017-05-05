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

/**
 * Predefined set of permissions that can be assigned to a user. A permission consists of a name - a
 * short code that identifies the permission. this is unique in the set of permissions. The name
 * (prepended with an underscore) is used as a place holder for the longer textual description of
 * the permission, action - the HTTP method (action) with which the permission is associated,
 * resourceURI - the URI identifying the resource being accessed.
 *
 * @author Christopher Fagiani
 */
public enum Permission {

    PROJECT_FOLDER_CREATE("POST", "/rest/survey_groups"),
    PROJECT_FOLDER_READ("GET", "/rest/survey_groups"),
    PROJECT_FOLDER_UPDATE("PUT", "/rest/survey_groups"),
    PROJECT_FOLDER_DELETE("DELETE", "/rest/survey_groups"),

    FORM_CREATE("POST", "/rest/surveys"),
    FORM_READ("GET", "/rest/surveys"),
    FORM_UPDATE("PUT", "/rest/surveys"),
    FORM_DELETE("DELETE", "/rest/surveys"),

    // manage data approvals i.e. define and assign to surveys
    DATA_APPROVE_MANAGE("GET", "/rest/approval_groups"),

    // there is no GAE URI for data export / import as this is handled by FLOW services so we use
    // empty strings as placeholders
    DATA_CLEANING("", ""),
    DATA_READ("GET", "/rest/survey_instances"),
    DATA_UPDATE("PUT", "/rest/survey_instances"),
    DATA_DELETE("DELETE", "/rest/survey_instances"),

    DEVICE_MANAGE("GET", "/rest/devices"),
    CASCADE_MANAGE("GET", "/rest/cascade_resources");

    private final String httpMethod;
    private final String uriPrefix;

    Permission(String method, String uri) {
        this.httpMethod = method;
        this.uriPrefix = uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUriPrefix() {
        return uriPrefix;
    }

    public static Permission lookup(String httpMethod, String requestUri) {
        if (httpMethod == null || requestUri == null) {
            return null;
        }

        for (Permission permission : Permission.values()) {
            if (permission.getHttpMethod().equals(httpMethod)
                    && requestUri.startsWith(permission.getUriPrefix())) {
                return permission;
            }
        }
        return null;
    }
}
