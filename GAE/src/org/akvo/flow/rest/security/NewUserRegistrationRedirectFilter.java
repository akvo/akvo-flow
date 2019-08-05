/*
 * Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo FLOW.
 *
 * Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 * the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 * either version 3 of the License or any later version.
 *
 * Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License included below for more details.
 *
 * The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.rest.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.akvo.flow.rest.security.user.GaeUser;

import com.google.appengine.api.users.UserServiceFactory;

public class NewUserRegistrationRedirectFilter extends GenericFilterBean {
    private static final String REGISTRATION_URL = "/register.html";
    private static final Logger logger = Logger.getLogger(NewUserRegistrationRedirectFilter.class.getName());

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (authentication != null) {

            if (authentication.getAuthorities().contains(AppRole.ROLE_NEW_USER)
                    && !httpRequest.getRequestURI().startsWith("/remote_api")
                    && !httpRequest.getRequestURI().startsWith(REGISTRATION_URL)) {

                GaeUser principal = getGaeUser(authentication);
                if (principal == null || !principal.isAuthByGAE()) {
                    redirectToRegistrationPage((HttpServletResponse) response);
                    return;
                } else {
                    String logoutUrl = UserServiceFactory.getUserService().createLogoutURL("");
                    if (!logoutUrl.startsWith(httpRequest.getRequestURI())) {
                        redirectToRegistrationPage((HttpServletResponse) response);
                        return;
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    private void redirectToRegistrationPage(HttpServletResponse response) throws IOException {
        logger.log(Level.INFO, "New user authenticated. Redirecting to registration page");
        response.sendRedirect(REGISTRATION_URL);
    }

    private GaeUser getGaeUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof GaeUser) {
            return (GaeUser) principal;
        } else {
            return null;
        }
    }
}
