package org.akvo.flow.rest.security;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.akvo.flow.rest.security.oidc.AppConfig;
import org.akvo.flow.rest.security.user.GaeUser;

import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("unused")
@Controller
public class LogoutController implements LogoutSuccessHandler {

    private AppConfig appConfig;

    public LogoutController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException {
        logger.fine("Performing logout");
        invalidateSession(req);

        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof GaeUser) {
                GaeUser principal = (GaeUser) authentication.getPrincipal();
                if (!principal.isAuthByGAE()) {
                    logoutFromOIDC(req, res);
                } else {
                    logoutFromGAE(res);
                }
            }
        }
    }

    private void logoutFromGAE(HttpServletResponse res) throws IOException {
        res.sendRedirect(UserServiceFactory.getUserService().createLogoutURL(
                "/"));
    }

    private void logoutFromOIDC(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String returnTo = req.getScheme() + "://" + req.getServerName();
        if ((req.getScheme().equals("http") && req.getServerPort() != 80) || (req.getScheme().equals("https") && req.getServerPort() != 443)) {
            returnTo += ":" + req.getServerPort();
        }
        returnTo += "/";
        String logoutUrl = String.format(
                "https://%s/v2/logout?client_id=%s&returnTo=%s",
                appConfig.getDomain(),
                appConfig.getClientId(),
                returnTo);
        res.sendRedirect(logoutUrl);
    }

    private void invalidateSession(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
    }

}
