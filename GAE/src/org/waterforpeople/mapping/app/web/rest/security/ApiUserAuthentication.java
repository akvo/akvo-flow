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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.waterforpeople.mapping.app.web.rest.security.user.ApiUser;

public class ApiUserAuthentication implements Authentication {

    private static final long serialVersionUID = 7982388231754113238L;

    private ApiUser principal;
    private Map<String, String> details;
    private boolean authenticated;

    public ApiUserAuthentication(ApiUser principal, Map<String, String> details) {
	this.principal = principal;
	this.details = details;
	this.authenticated = true;
    }

    public ApiUserAuthentication(ApiUser principal) {
	this.principal = principal;
	this.details = null;
	this.authenticated = true;
    }

    @Override
    public String getName() {
	return principal.getUserName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return EnumSet.of(AppRole.USER);
    }

    @Override
    public Object getCredentials() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object getDetails() {
	return details;
    }

    @Override
    public Object getPrincipal() {
	return principal;
    }

    @Override
    public boolean isAuthenticated() {
	return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated)
	    throws IllegalArgumentException {
	this.authenticated = authenticated;
    }
}
