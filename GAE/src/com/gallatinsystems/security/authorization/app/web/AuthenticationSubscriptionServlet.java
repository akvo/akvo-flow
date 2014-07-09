/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.security.authorization.app.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.security.authorization.utility.TokenUtility;
import com.google.gdata.client.http.AuthSubUtil;

/**
 * servlet used to authorize the user with via his/her google account.
 */
public class AuthenticationSubscriptionServlet extends HttpServlet {
    private static final Logger log = Logger
            .getLogger(AuthenticationSubscriptionServlet.class.getName());

    private static final long serialVersionUID = 8839978412963370603L;
    public final static String FORWARD_URL_PROP = "next_url";
    public final static String GOOGLE_REQUEST_SCOPE = "google_scope";

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("token") == null) {
            getToken(resp);
        } else {
            processToken(req, resp);
        }
    }

    @SuppressWarnings("deprecation")
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        if (req.getParameter("token") == null
                && req.getSession().getValue("sessionToken") == null) {
            getToken(resp);
        } else {
            processToken(req, resp);
        }
    }

    private void getToken(HttpServletResponse resp) {
        String nextUrl = PropertyUtil.getProperty(FORWARD_URL_PROP);

        String scope = PropertyUtil.getProperty(GOOGLE_REQUEST_SCOPE);

        boolean secure = false; // set secure=true to request secure AuthSub
        // tokens
        boolean session = true;
        String authSubUrl = AuthSubUtil.getRequestUrl(nextUrl, scope, secure,
                session);
        try {
            ((HttpServletResponse) resp).sendRedirect(authSubUrl);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not redirect", e);
        }
    }

    private void processToken(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession(true);
        // if (session.getValue("sessionToken") == null) {
        if (true) {
            log.info("QueryString: " + req.getQueryString());
            String singleUseToken = AuthSubUtil.getTokenFromReply(req
                    .getQueryString());
            try {
                singleUseToken = URLDecoder.decode(singleUseToken, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.warning("Could not decode token" + e);
            }
            log.info("singleUseToken: " + singleUseToken);

            TokenUtility tk = new TokenUtility();
            try {
                if (session.getAttribute("sessionToken") == null) {
                    log.log(Level.INFO, "About to generateSessionToken");
                    String sessionToken = tk
                            .generateSessionTokenFromSingleUse(singleUseToken);
                    log.log(Level.INFO, "Generated Session Token");
                    session.setAttribute("sessionToken", sessionToken);
                }
                if (session.getAttribute("privateKey") == null) {
                    log.log(Level.INFO, "About to get PK");
                    PrivateKey privateKey = tk.getPrivateKey();
                    log.log(Level.INFO, "Got PK");
                    session.setAttribute("privateKey", privateKey);
                    log.log(Level.INFO, "Set PK");
                }
            } catch (Exception e1) {
                log.log(Level.SEVERE, "Could not authenticate", e1);
            }
        }
        try {
            ((HttpServletResponse) resp).sendRedirect("/Dashboard.html");
            // ((HttpServletResponse)resp).sendRedirect("/Dashboard.html?gwt.codesvr=127.0.0.1:9997");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not redirect", e);
        }
    }
}
