/*
 *  Copyright (C) 2014, 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.akvo.flow.rest.security.user.ApiUser;

public class ApiAuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {

	HttpServletRequest httpRequest = (HttpServletRequest) request;
	Map<String, String> details = new HashMap<String, String>();

	details.put("HTTP-Verb", httpRequest.getMethod());
	details.put("Date", httpRequest.getHeader("Date"));
	details.put("Authorization", httpRequest.getHeader("Authorization"));
	details.put("Resource", httpRequest.getRequestURI());

	try {
	    Authentication authentication = authenticationManager
		    .authenticate(new ApiUserAuthentication(new ApiUser(),
			    details));
	    // Successful authentication
	    SecurityContextHolder.getContext()
		    .setAuthentication(authentication);
	} catch (AuthenticationException e) {
	    // Unsuccessful authentication
	}

	chain.doFilter(request, response);
    }

    public AuthenticationManager getAuthenticationManager() {
	return authenticationManager;
    }

    public void setAuthenticationManager(
	    AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }
}
