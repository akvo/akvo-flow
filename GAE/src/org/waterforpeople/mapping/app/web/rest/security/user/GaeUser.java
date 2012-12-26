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
	private final String userId;
    private final String email;
    private final String nickname;
    private final String forename;
    private final String surname;
    private final Set<AppRole> authorities;
    private final boolean enabled;

    /**
     * Pre-registration constructor.
     *
     * Assigns the user the "NEW_USER" role only.
     */
    public GaeUser(String userId, String nickname, String email) {
        this.userId = userId;
        this.nickname = nickname;
        this.authorities = EnumSet.of(AppRole.NEW_USER);
        this.forename = null;
        this.surname = null;
        this.email = email;
        this.enabled = true;
    }

    /**
     * Post-registration constructor
     */
    public GaeUser(String userId, String nickname, String email, String forename, String surname, Set<AppRole> authorities, boolean enabled) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.authorities = authorities;
        this.forename = forename;
        this.surname = surname;
        this.enabled= enabled;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Collection<AppRole> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "GaeUser{" +
                "userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
