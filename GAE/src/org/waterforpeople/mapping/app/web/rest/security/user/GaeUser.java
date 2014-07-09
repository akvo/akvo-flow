
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

    /**
     * Pre-registration constructor. Assigns the user the "NEW_USER" role only.
     */
    public GaeUser(String userName, String email) {
        this.authorities = EnumSet.of(AppRole.NEW_USER);
        this.userName = userName;
        this.email = email;
        this.enabled = true;
    }

    /**
     * Post-registration constructor
     */
    public GaeUser(String userName, String email, Set<AppRole> authorities, boolean enabled) {
        this.userName = userName;
        this.email = email;
        this.authorities = authorities;
        this.enabled = enabled;
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

    public Collection<AppRole> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "GaeUser{" +
                ", userName='" + userName + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
