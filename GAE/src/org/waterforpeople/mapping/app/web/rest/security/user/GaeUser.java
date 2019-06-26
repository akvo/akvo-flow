
package org.waterforpeople.mapping.app.web.rest.security.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.waterforpeople.mapping.app.web.rest.security.AppRole;

/**
 * Custom user object for the application.
 *
 * @author Luke Taylor
 */
public class GaeUser implements Serializable {
    private static final long serialVersionUID = -381882633758542764L;
    private final String email;
    private final String userName;
    private final Set<AppRole> authorities;
    private final boolean enabled;
    private Long userId;
    private boolean authByGAE;

    /**
     * Pre-registration constructor. Assigns the user the "ROLE_NEW_USER" role only.
     */
    public GaeUser(boolean authByGAE, String userName, String email) {
        this.authorities = EnumSet.of(AppRole.ROLE_NEW_USER);
        this.userName = userName;
        this.email = email;
        this.enabled = true;
        this.authByGAE = authByGAE;
    }

    /**
     * Post-registration constructor
     */
    public GaeUser(String userName, String email, Long userId, Set<AppRole> authorities,
            boolean enabled, boolean authByGAE) {
        this.userName = userName;
        this.email = email;
        this.authorities = authorities;
        this.enabled = enabled;
        this.userId = userId;
        this.authByGAE = authByGAE;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isAuthByGAE() {return authByGAE;}

    public Collection<AppRole> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "GaeUser{email = '" + email + "' " +
                ", userName='" + userName + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
