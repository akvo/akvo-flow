
package org.waterforpeople.mapping.app.web.rest.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

/**
 * Authentication object representing a fully-authenticated user.
 *
 * @author Luke Taylor
 */
public class GaeUserAuthentication implements Authentication {
    private static final long serialVersionUID = 7819156935996796844L;
    private final GaeUser principal;
    private final Object details;
    private boolean authenticated;
    private Long userId;

    public GaeUserAuthentication(GaeUser principal, Object details) {
        this.principal = principal;
        this.details = details;
        this.userId = principal.getUserId();
        authenticated = true;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new HashSet<GrantedAuthority>(principal.getAuthorities());
    }

    @Override
    public Object getCredentials() {
        return userId;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.getUserName();
    }

    @Override
    public String toString() {
        return "GaeUserAuthentication{" +
                "principal=" + principal +
                ", details=" + details +
                ", authenticated=" + authenticated +
                '}';
    }
}
