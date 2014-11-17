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
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

public class CustomVoter implements AccessDecisionVoter<FilterInvocation> {

    private static final Logger log = Logger.getLogger(CustomVoter.class.getName());

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(FilterInvocation.class);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int vote(Authentication authentication, FilterInvocation fi,
            Collection<ConfigAttribute> attributes) {

        HttpServletRequest req = fi.getHttpRequest();
        ObjectMapper mapper = new ObjectMapper();
        String method = req.getMethod();

        if ("POST".equals(method) || "PUT".equals(method)) {
            try {
                Map payload = mapper.readValue(req.getInputStream(), Map.class);
                log.info(payload.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        log.info(fi.toString() + " - CustomVoter - voting: " + AccessDecisionVoter.ACCESS_ABSTAIN);

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
}
