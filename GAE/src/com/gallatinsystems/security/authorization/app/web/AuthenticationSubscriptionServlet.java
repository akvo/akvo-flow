package com.gallatinsystems.security.authorization.app.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	private void getToken(HttpServletResponse resp) {
		 String nextUrl = "http://watermapmonitordev.appspot.com/authsub";
		//String nextUrl = "http://localhost:8888/authsub";
		String scope = "http://spreadsheets.google.com/feeds/";
		boolean secure = true; // set secure=true to request secure AuthSub
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
