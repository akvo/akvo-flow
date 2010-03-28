package org.waterforpeople.mapping.app.gwt.client.user;

import java.io.Serializable;

public class UserDto implements Serializable{

	private String userName;
	private String emailAddress;

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
