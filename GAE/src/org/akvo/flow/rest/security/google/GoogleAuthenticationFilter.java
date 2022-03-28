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

package org.akvo.flow.rest.security.google;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import org.akvo.flow.rest.security.user.GaeUser;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author Luke Taylor
 */
public class GoogleAuthenticationFilter extends GenericFilterBean {
    private static final Logger logger = Logger.getLogger(GoogleAuthenticationFilter.class.getName());

    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> ads = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager;
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User googleUser = UserServiceFactory.getUserService().getCurrentUser();

        if (shouldClearSession(authentication, googleUser)) {
            SecurityContextHolder.clearContext();
            authentication = null;
            ((HttpServletRequest) request).getSession().invalidate();
        }

        if (authentication == null) {
            if (googleUser != null) {
                logger.log(Level.INFO, "Currently logged on to GAE as user " + googleUser);
                logger.log(Level.INFO, "Authenticating to Spring Security");
                // User has returned after authenticating via GAE. Need to authenticate through
                // Spring Security.
                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                        googleUser, null);
                token.setDetails(ads.buildDetails((HttpServletRequest) request));

                try {
                    authentication = authenticationManager.authenticate(token);
                    final SecurityContext securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authentication);
                } catch (AuthenticationException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    failureHandler.onAuthenticationFailure((HttpServletRequest) request,
                            (HttpServletResponse) response, e);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    public static boolean shouldClearSession(Authentication authentication, User googleUser) {
        if (authentication == null) {
            return false;
        }

        GaeUser gaeUser = (GaeUser) authentication.getPrincipal();

        if (!gaeUser.isAuthByGAE()) {
            return false;
        }

        if (googleUser == null) {
            // User has logged out of GAE but is still logged into application
            return true;
        }

        return !gaeUser.getEmail().equals(googleUser.getEmail());

    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager, "AuthenticationManager must be set");
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
}
