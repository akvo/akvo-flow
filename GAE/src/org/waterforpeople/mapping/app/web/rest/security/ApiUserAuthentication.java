package org.waterforpeople.mapping.app.web.rest.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.waterforpeople.mapping.app.web.rest.security.user.ApiUser;

public class ApiUserAuthentication implements Authentication {

	private static final long serialVersionUID = 7982388231754113238L;
	
	private ApiUser principal;
	private Map<String,String> details;
	private boolean authenticated;
	
	public ApiUserAuthentication(ApiUser principal, Map<String,String> details) {
		this.principal = principal;
		this.details = details;
		this.authenticated = true;
	}
	
	public ApiUserAuthentication(ApiUser principal) {
		this.principal = principal;
		this.details = null;
		this.authenticated = true;
	}
	
	@Override
	public String getName() {
		return principal.getUserName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return EnumSet.of(AppRole.USER);
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		return details;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
		this.authenticated = authenticated;
	}
}
