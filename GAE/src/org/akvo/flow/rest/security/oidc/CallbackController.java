package org.akvo.flow.rest.security.oidc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.akvo.flow.rest.security.google.GoogleAccountsAuthenticationProvider;

import com.auth0.AuthenticationController;
import com.auth0.IdentityVerificationException;
import com.auth0.Tokens;
import com.auth0.jwt.JWT;
import com.gallatinsystems.user.dao.UserDao;

@SuppressWarnings("unused")
@Controller
public class CallbackController {

    private static final Logger log = Logger.getLogger(CallbackController.class.getName());

    private final AuthenticationController controller;
    private final String redirectOnFail;
    private final String redirectOnSuccess;

    public CallbackController(AppConfig appConfig) {
        this.controller = appConfig.authenticationController();
        this.redirectOnFail = "/"; // Where to redirect?
        this.redirectOnSuccess = "/admin";
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    protected void getCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        handle(req, res);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    protected void postCallback(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {
        handle(req, res);
    }

    private void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Tokens tokens = controller.handle(req);
            TokenAuthentication tokenAuth = new TokenAuthentication(JWT.decode(tokens.getIdToken()));
            Authentication authentication = GoogleAccountsAuthenticationProvider.getAuthentication(
                    false, new UserDao(),
                    tokenAuth,
                    tokenAuth.getClaims().get("email").asString(),
                    tokenAuth.getClaims().get("nickname").asString()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            res.sendRedirect(redirectOnSuccess);
        } catch (AuthenticationException | IdentityVerificationException e) {
            log.log(Level.SEVERE, e.getMessage());
            SecurityContextHolder.clearContext();
            res.sendRedirect(redirectOnFail);
        }
    }

}
