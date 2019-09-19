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

package org.akvo.flow.rest.security.oidc;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.auth0.AuthenticationController;

public class EntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = Logger.getLogger(EntryPoint.class.getName());

    private final AppConfig appConfig;
    private final AuthenticationController controller;

    final Set<String> enabledLocales = new HashSet<String>(
            Arrays.asList("en", "es", "fr", "pt", "id", "vi"));

    public EntryPoint(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.controller = this.appConfig.authenticationController();
    }
    private boolean isAllowedLocale(String locale) {
        return enabledLocales.contains(locale);
    }

    private String getLocale(String reqPararameter){
	if(isAllowedLocale(reqPararameter)){
	    return reqPararameter;
	} else {
	    return "en";
	}
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
	String locale = getLocale(request.getParameter("locale"));
        logger.fine("Performing login. Locale: "+ locale);
        String redirectUri = request.getScheme() + "://" + request.getServerName();
        if ((request.getScheme().equals("http") && request.getServerPort() != 80) || (request.getScheme().equals("https") && request.getServerPort() != 443)) {
            redirectUri += ":" + request.getServerPort();
        }
        redirectUri += "/callback";
        String authorizeUrl = controller.buildAuthorizeUrl(request, redirectUri)
	    .withAudience(String.format("https://%s/userinfo", appConfig.getDomain()))
	    .withScope("openid profile email")
	    .withParameter("prompt", "select_account")
	    .withParameter("ui_locales", locale)
	    .build();
        response.sendRedirect(response.encodeRedirectURL(authorizeUrl));

    }
}
