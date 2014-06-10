
package org.waterforpeople.mapping.app.web.rest.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

/**
 * @author Luke Taylor
 */
public class GaeAuthenticationFilter extends GenericFilterBean {
    private static final String REGISTRATION_URL = "/register.html";
    private static final Logger logger = Logger.getLogger(GaeAuthenticationFilter.class.getName());

    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> ads = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager;
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User googleUser = UserServiceFactory.getUserService().getCurrentUser();
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (authentication != null && !loggedInUserMatchesGaeUser(authentication, googleUser)) {
            SecurityContextHolder.clearContext();
            authentication = null;
            ((HttpServletRequest) request).getSession().invalidate();
        }

        if (authentication == null) {
            if (googleUser != null) {
                logger.log(Level.INFO, "Currently logged on to GAE as user " + googleUser);
                logger.log(Level.INFO, "Authenticating to Spring Security");
                // User has returned after authenticating via GAE. Need to authenticate through
                // Spring Security.
                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(
                        googleUser, null);
                token.setDetails(ads.buildDetails((HttpServletRequest) request));

                try {
                    authentication = authenticationManager.authenticate(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String logoutUrl = UserServiceFactory.getUserService().createLogoutURL("");

                    if (authentication.getAuthorities().contains(
                            AppRole.NEW_USER)
                            && !logoutUrl.startsWith(httpRequest
                                    .getRequestURI())
                            && !httpRequest.getRequestURI().startsWith(
                                    "/remote_api")) {
                        logger.log(Level.INFO,
                                "New user authenticated. Redirecting to registration page");
                        ((HttpServletResponse) response)
                                .sendRedirect(REGISTRATION_URL);
                        return;
                    }

                } catch (AuthenticationException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    failureHandler.onAuthenticationFailure((HttpServletRequest) request,
                            (HttpServletResponse) response, e);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean loggedInUserMatchesGaeUser(Authentication authentication, User googleUser) {
        assert authentication != null;

        if (googleUser == null) {
            // User has logged out of GAE but is still logged into application
            return false;
        }

        GaeUser gaeUser = (GaeUser) authentication.getPrincipal();

        if (!gaeUser.getEmail().equals(googleUser.getEmail())) {
            return false;
        }

        return true;

    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        Assert.notNull(authenticationManager, "AuthenticationManager must be set");
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
}
