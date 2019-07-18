package org.akvo.flow.rest.security.oidc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenAuthentication extends AbstractAuthenticationToken {

    private final DecodedJWT jwt;
    private boolean invalidated;

    public TokenAuthentication(DecodedJWT jwt) {
        super(readAuthorities(jwt));
        this.jwt = jwt;
    }

    private boolean hasExpired() {
        return jwt.getExpiresAt().before(new Date());
    }

    private static Collection<? extends GrantedAuthority> readAuthorities(DecodedJWT jwt) {
        Claim rolesClaim = jwt.getClaim("https://access.control/roles");
        if (rolesClaim.isNull()) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        String[] scopes = rolesClaim.asArray(String.class);
        for (String s : scopes) {
            SimpleGrantedAuthority a = new SimpleGrantedAuthority(s);
            if (!authorities.contains(a)) {
                authorities.add(a);
            }
        }
        return authorities;
    }


    @Override
    public String getCredentials() {
        return jwt.getToken();
    }

    @Override
    public Object getPrincipal() {
        return jwt.getSubject();
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException("Create a new Authentication object to authenticate");
        }
        invalidated = true;
    }

    @Override
    public boolean isAuthenticated() {
        return !invalidated && !hasExpired();
    }

    /**
     * Gets the claims for this JWT token.
     * <br>
     * For an ID token, claims represent user profile information such as the user's name, profile, picture, etc.
     * <br>
     * @see <a href="https://auth0.com/docs/tokens/id-token">ID Token Documentation</a>
     * @return a Map containing the claims of the token.
     */
    public Map<String, Claim> getClaims() {
        return jwt.getClaims();
    }

}
