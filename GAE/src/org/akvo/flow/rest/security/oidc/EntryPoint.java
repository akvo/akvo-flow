package org.akvo.flow.rest.security.oidc;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.auth0.AuthenticationController;

public class EntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = Logger.getLogger(EntryPoint.class.getName());

    private final AppConfig appConfig;
    private final AuthenticationController controller;

    public EntryPoint(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.controller = this.appConfig.authenticationController();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        logger.fine("Performing login");
        String redirectUri = request.getScheme() + "://" + request.getServerName();
        if ((request.getScheme().equals("http") && request.getServerPort() != 80) || (request.getScheme().equals("https") && request.getServerPort() != 443)) {
            redirectUri += ":" + request.getServerPort();
        }
        redirectUri += "/callback";
        String authorizeUrl = controller.buildAuthorizeUrl(request, redirectUri)
                .withAudience(String.format("https://%s/userinfo", appConfig.getDomain()))
                .withScope("openid profile email")
                .withParameter("prompt", "select_account")
                .build();
        response.sendRedirect(response.encodeRedirectURL(authorizeUrl));

    }
}
