package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * User of the web application. This object also can be used to persist (via a
 * cascade save) UserConfig.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class User extends BaseDomain {
	
	private static final long serialVersionUID = -1416095159769575254L;
	@Persistent
	private String userName;
	@Persistent
	private String emailAddress;

	@Persistent
	private UserConfig config;

	public UserConfig getConfig() {
		return config;
	}

	public void setConfig(UserConfig config) {
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
