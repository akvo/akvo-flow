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

public class AuthenticationSubscriptionServlet extends HttpServlet {
	private static final Logger log = Logger
			.getLogger(AuthenticationSubscriptionServlet.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 8839978412963370603L;

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

	public final static String FORWARD_URL_PROP = "next_url";
	public final static String GOOGLE_REQUEST_SCOPE = "google_scope";

	private void getToken(HttpServletResponse resp) {
		PropertyUtil propUtil = new PropertyUtil();
		String nextUrl = propUtil.getProperty(FORWARD_URL_PROP);

		String scope = propUtil.getProperty(GOOGLE_REQUEST_SCOPE);

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
