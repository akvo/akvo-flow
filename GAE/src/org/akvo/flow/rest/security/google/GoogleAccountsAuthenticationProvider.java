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

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.akvo.flow.rest.security.AppRole;
import org.akvo.flow.rest.security.GaeUserAuthentication;
import org.akvo.flow.rest.security.user.GaeUser;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.gallatinsystems.framework.dao.AuthzDao;
import com.google.appengine.api.users.User;

/**
 * A simple authentication provider which interacts with {@code User} returned by the GAE
 * {@code UserService}, and also the local persistent {@code UserRegistry} to build an application
 * user principal.
 * <p>
 * If the user has been authenticated through google accounts, it will check if they are already
 * registered and either load the existing user information or assign them a temporary identity with
 * limited access until they have registered.
 * <p>
 * If the account has been disabled, a {@code DisabledException} will be raised.
 *
 * @author Luke Taylor
 */
public class GoogleAccountsAuthenticationProvider implements AuthenticationProvider,
        MessageSourceAware {
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private static final Logger log = Logger.getLogger(GoogleAccountsAuthenticationProvider.class
            .getName());

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        User googleUser = (User) authentication.getPrincipal();

        return getAuthentication(true, authentication, googleUser.getEmail(), googleUser.getNickname());
    }

    public static Authentication getAuthentication(boolean authByGAE, Authentication authentication, String email, String nickname) {
        GaeUser user = findUser(email, authByGAE);

        if (user == null) {
            // User not in registry. Needs to register
            user = new GaeUser(authByGAE, nickname, email);
        }

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }

        return new GaeUserAuthentication(user, authentication.getDetails());
    }

    private static GaeUser findUser(String email, boolean authByGAE) {
        final com.gallatinsystems.user.domain.User user = new AuthzDao().findUserByEmail(email);

        if (user == null) {
            return null;
        }

        final int authority = getAuthorityLevel(user);
        final Set<AppRole> roles = EnumSet.noneOf(AppRole.class);

        if (authority == AppRole.ROLE_NEW_USER.getLevel()) {
            roles.add(AppRole.ROLE_NEW_USER);
        } else {
            for (AppRole r : AppRole.values()) {
                if (authority <= r.getLevel()) {
                    roles.add(r);
                }
            }
        }

        return new GaeUser(user.getUserName(), user.getEmailAddress(), user.getKey().getId(),
                roles, true, authByGAE);
    }

    private static int getAuthorityLevel(com.gallatinsystems.user.domain.User user) {
        if (user.isSuperAdmin()) {
            return AppRole.ROLE_SUPER_ADMIN.getLevel();
        }
        try {
            final int level = Integer.parseInt(user.getPermissionList());
            return level;
        } catch (Exception e) {
            log.log(Level.WARNING, "Error getting role level, setting ROLE_USER role", e);
        }
        return AppRole.ROLE_USER.getLevel();
    }

    /**
     * Indicate that this provider only supports PreAuthenticatedAuthenticationToken (sub)classes.
     */
    @Override
    public final boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
