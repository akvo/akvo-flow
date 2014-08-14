package org.waterforpeople.mapping.app.web.rest.security;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.waterforpeople.mapping.app.web.rest.security.user.ApiUser;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;

public class ApiAuthenticationProvider implements AuthenticationProvider {

	@Inject
	UserDao userDao;
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		Map<String, String> details = (Map<String, String>) authentication.getDetails();
		String[] credentials = parseCredentials(details.get("Authorization"));
		String accessKey = credentials[0];
		String clientSignature = credentials[1];
		
		Date date = parseDate(details.get("Date"));

		ApiUser apiUser = findUser(accessKey);
		
		String payload = buildPayload(date, details.get("Resource"));
		
		if (apiUser == null) {
			throw new BadCredentialsException("Authorization Required");
		} else {
			String serverSignature = MD5Util.generateHMAC(payload, apiUser.getSecret());
			long clientTime = date.getTime();
			long serverTime = new Date().getTime();
			long timeDelta = 600000; // +/- 10 minutes
			
			if (clientSignature.equals(serverSignature) &&
				serverTime - timeDelta < clientTime &&
				clientTime < serverTime + timeDelta) {
				// Successful authentication
				return new ApiUserAuthentication(apiUser);
			} else {
				throw new BadCredentialsException("Authorization Required");
			}
		}
	}
	
	private ApiUser findUser(String accessKey) {
		User user = userDao.findByAccessKey(accessKey);
		if (user != null) {
			return new ApiUser(user.getUserName(), user.getAccessKey(), user.getSecret());
		} else {
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO from GoogleAccountsAuthenticationProvider:
		// PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
		return true;
	}
	
	private String[] parseCredentials(String credentialsString) {
		if (credentialsString == null) 
			throw new BadCredentialsException("Authorization required");
		
		String[] credentials = credentialsString.split(":");
		
		if (credentials.length != 2) 
			throw new BadCredentialsException("Authorization required");
		
		return credentials;
	}
	
	private Date parseDate(String dateString) {
		try {
			// epoch is seconds based. Date constructor is milliseconds based.
			return new Date(Long.parseLong(dateString) * 1000);
		} catch (NumberFormatException e) {
			throw new BadCredentialsException("Authorization Required");
		}
	}
	
	private String buildPayload(Date date, String resource) {
		// date.getTime() is millisecond based and epoch is seconds based
		return "GET\n" + String.valueOf(date.getTime()/1000) + "\n" + resource;
	}
}
