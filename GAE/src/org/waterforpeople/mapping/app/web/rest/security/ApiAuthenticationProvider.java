/*
 *  Copyright (C) 2014,2017 Stichting Akvo (Akvo Foundation)
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

import java.util.Date;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.waterforpeople.mapping.app.web.rest.security.user.ApiUser;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;

public class ApiAuthenticationProvider implements AuthenticationProvider {

    private UserDao userDao = new UserDao();

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) authentication
                .getDetails();
        String[] credentials = parseCredentials(details.get("Authorization"));
        String accessKey = credentials[0];
        String clientSignature = credentials[1];

        ApiUser apiUser = findUser(accessKey);

        if (apiUser == null) {
            throw new BadCredentialsException("Authorization Required");
        }

        Date date = parseDate(details.get("Date"));
        long clientTime = date.getTime();
        long serverTime = new Date().getTime();
        long timeDelta = 600000; // +/- 10 minutes

        if (serverTime - timeDelta < clientTime
                && clientTime < serverTime + timeDelta) {
            String payload = buildPayload(details.get("HTTP-Verb"), date,
                    details.get("Resource"));
            String serverSignature = MD5Util.generateHMAC(payload,
                    apiUser.getSecret());
            if (clientSignature.equals(serverSignature)) {
                // Successful authentication
                return new ApiUserAuthentication(apiUser);
            }
        }
        // Unsuccessful authentication
        throw new BadCredentialsException("Authorization Required");
    }

    private ApiUser findUser(String accessKey) {
        User user = userDao.findByAccessKey(accessKey);
        if (user != null) {
            return new ApiUser(user.getUserName(), user.getAccessKey(),
                    user.getSecret(), user.getKey().getId());
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    private String[] parseCredentials(String credentialsString) {
        if (credentialsString == null)
            throw new BadCredentialsException("Authorization required");

        String[] credentials = credentialsString.split(":");

        if (credentials.length != 2) {
            throw new BadCredentialsException("Authorization required");
        }

        credentials[0] = credentials[0].trim();
        credentials[1] = credentials[1].trim();

        return credentials;
    }

    private Date parseDate(String dateString) {
        try {
            // epoch is seconds based. Date constructor is milliseconds based.
            return new Date(Long.parseLong(dateString) * 1000);
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Authorization Required");
        }
    }

    private String buildPayload(String httpVerb, Date date, String resource) {
        // date.getTime() is millisecond based and epoch is seconds based
        return httpVerb + "\n" + String.valueOf(date.getTime() / 1000) + "\n"
                + resource;
    }
}
