package org.waterforpeople.mapping.app.web.rest.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Luke Taylor
 */
public enum AppRole implements GrantedAuthority {
    NEW_USER (-1),
    ADMIN (0),
    PROJECT_ADMIN(2),
    USER (3);

    private final int level;

    /**
     * Creates an authority with a specific bit representation. It's important that this doesn't
     * change as it will be used in the database. The enum ordinal is less reliable as the enum may be
     * reordered or have new roles inserted which would change the ordinal values.
     *
     * @param bit the permission bit which will represent this authority in the datastore.
     */
    AppRole(int bit) {
        this.level = bit;
    }

    public int getLevel() {
        return level;
    }

    public String getAuthority() {
        return toString();
    }
}
