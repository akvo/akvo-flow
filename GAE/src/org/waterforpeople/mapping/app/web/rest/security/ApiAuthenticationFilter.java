package org.waterforpeople.mapping.app.web.rest.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.waterforpeople.mapping.app.web.rest.security.user.ApiUser;

public class ApiAuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
	    FilterChain chain) throws IOException, ServletException {

	HttpServletRequest httpRequest = (HttpServletRequest) request;
	Map<String, String> details = new HashMap<String, String>();

	details.put("HTTP-Verb", "GET");
	details.put("Date", httpRequest.getHeader("Date"));
	details.put("Authorization", httpRequest.getHeader("Authorization"));
	details.put("Resource", httpRequest.getRequestURI());

	try {
	    Authentication authentication = authenticationManager
		    .authenticate(new ApiUserAuthentication(new ApiUser(),
			    details));
	    // Successful authentication
	    SecurityContextHolder.getContext()
		    .setAuthentication(authentication);
	} catch (AuthenticationException e) {
	    // Unsuccessful authentication
	}

	chain.doFilter(request, response);
    }

    public AuthenticationManager getAuthenticationManager() {
	return authenticationManager;
    }

    public void setAuthenticationManager(
	    AuthenticationManager authenticationManager) {
	this.authenticationManager = authenticationManager;
    }
}
