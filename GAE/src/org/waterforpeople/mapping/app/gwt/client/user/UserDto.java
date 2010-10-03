package org.waterforpeople.mapping.app.gwt.client.user;

import java.util.Map;
import java.util.Set;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class UserDto extends BaseDto {

	private static final long serialVersionUID = -61713350825542379L;

	private String userName;
	private String emailAddress;
	private Map<String, Set<UserConfigDto>> config;
	private String logoutUrl;
	private boolean hasAccess = true;
	private boolean admin = false;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean isAdmin) {
		this.admin = isAdmin;
	}

	public boolean hasAccess() {
		return hasAccess;
	}

	public void setHasAccess(boolean hasAccess) {
		this.hasAccess = hasAccess;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public Map<String, Set<UserConfigDto>> getConfig() {
		return config;
	}

	public void setConfig(Map<String, Set<UserConfigDto>> config) {
		this.config = config;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
}
