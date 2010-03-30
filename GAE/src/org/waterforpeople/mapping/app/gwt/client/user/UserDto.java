package org.waterforpeople.mapping.app.gwt.client.user;

import java.io.Serializable;
import java.util.List;

public class UserDto implements Serializable {

	private static final long serialVersionUID = -61713350825542379L;
	
	private String userName;
	private String emailAddress;
	private List<UserConfigDto> config;
	
	
	public List<UserConfigDto> getConfig() {
		return config;
	}

	public void setConfig(List<UserConfigDto> config) {
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
