package com.gallatinsystems.security.authorization.app.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.security.authorization.utility.TokenUtility;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.util.AuthenticationException;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void processToken(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession(true);
		if (session.getValue("sessionToken") == null) {
			log.info("QueryString: " + req.getQueryString());
			String singleUseToken = AuthSubUtil.getTokenFromReply(req
					.getQueryString());
			log.info("singleUseToken: " + singleUseToken);

			TokenUtility tk = new TokenUtility();
			try {
				if (session.getValue("sessionToken") == null) {
					String sessionToken = tk
							.generateSessionTokenFromSingleUse(singleUseToken);
					session.putValue("sessionToken", sessionToken);
				}
				if (session.getValue("privateKey") == null) {
					PrivateKey privateKey = tk.getPrivateKey();
					session.putValue("privateKey", privateKey);
				}
			} catch (AuthenticationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (GeneralSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			((HttpServletResponse) resp)
					.sendRedirect("/SpreadsheetMapping.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
