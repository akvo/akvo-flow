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
    FORM_UPDATE("PUT", "/survey/update"),
    FORM_DELETE("DELETE", "/rest/surveys");

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

    public String getAction() {
        return httpMethod;
    }

    public static Permission lookup(String httpMethod, String requestUri) {
        for (Permission permission : Permission.values()) {
            if (permission.getHttpMethod().equals(httpMethod)
                    && permission.getUriPrefix().equals(requestUri)) {
                return permission;
            }
        }
        return null;
    }
}
