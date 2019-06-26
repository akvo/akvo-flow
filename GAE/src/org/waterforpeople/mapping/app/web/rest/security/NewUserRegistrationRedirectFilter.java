
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.waterforpeople.mapping.app.web.rest.security.user.GaeUser;

import com.google.appengine.api.users.UserServiceFactory;

public class NewUserRegistrationRedirectFilter extends GenericFilterBean {
    private static final String REGISTRATION_URL = "/register.html";
    private static final Logger logger = Logger.getLogger(NewUserRegistrationRedirectFilter.class.getName());

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (authentication != null) {

            if (authentication.getAuthorities().contains(AppRole.ROLE_NEW_USER)
                    && !httpRequest.getRequestURI().startsWith("/remote_api")
                    && !httpRequest.getRequestURI().startsWith(REGISTRATION_URL)) {

                GaeUser principal = getGaeUser(authentication);
                if (principal == null || !principal.isAuthByGAE()) {
                    redirectToRegistrationPage((HttpServletResponse) response);
                    return;
                } else {
                    String logoutUrl = UserServiceFactory.getUserService().createLogoutURL("");
                    if (!logoutUrl.startsWith(httpRequest.getRequestURI())) {
                        redirectToRegistrationPage((HttpServletResponse) response);
                        return;
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    private void redirectToRegistrationPage(HttpServletResponse response) throws IOException {
        logger.log(Level.INFO, "New user authenticated. Redirecting to registration page");
        response.sendRedirect(REGISTRATION_URL);
    }

    private GaeUser getGaeUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof GaeUser) {
            return (GaeUser) principal;
        } else {
            return null;
        }
    }
}
