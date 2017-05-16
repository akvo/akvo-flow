
package org.waterforpeople.mapping.app.web.rest.security;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

import com.gallatinsystems.user.dao.UserDao;
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

    private UserDao userDao = new UserDao();

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        User googleUser = (User) authentication.getPrincipal();

        GaeUser user = findUser(googleUser.getEmail());

        if (user == null) {
            // User not in registry. Needs to register
            user = new GaeUser(googleUser.getNickname(), googleUser.getEmail());
        }

        if (!user.isEnabled()) {
            throw new DisabledException("Account is disabled");
        }

        return new GaeUserAuthentication(user, authentication.getDetails());
    }

    private GaeUser findUser(String email) {
        final com.gallatinsystems.user.domain.User user = userDao.findUserByEmail(email);

        if (user == null) {
            return null;
        }

        final int authority = getAuthorityLevel(user);
        final Set<AppRole> roles = EnumSet.noneOf(AppRole.class);

        if (authority == AppRole.NEW_USER.getLevel()) {
            roles.add(AppRole.NEW_USER);
        } else {
            for (AppRole r : AppRole.values()) {
                if (authority <= r.getLevel()) {
                    roles.add(r);
                }
            }
        }

        return new GaeUser(user.getUserName(), user.getEmailAddress(), user.getKey().getId(),
                roles, true);
    }

    private int getAuthorityLevel(com.gallatinsystems.user.domain.User user) {
        if (user.isSuperAdmin()) {
            return AppRole.SUPER_ADMIN.getLevel();
        }
        try {
            final int level = Integer.parseInt(user.getPermissionList());
            return level;
        } catch (Exception e) {
            log.log(Level.WARNING, "Error getting role level, setting USER role", e);
        }
        return AppRole.USER.getLevel();
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
