package com.gallatinsystems.auth.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Domain to capture authorization tokens used for various web activities.
 * Authorizations are token-based and can be either 1-time or multiple use.
 * Authorizations may also be given an expiration date, after which they are no
 * longer valid.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class WebActivityAuthorization extends BaseDomain {

	private static final long serialVersionUID = -8359553595104751266L;

	private Long userId;
	private Long maxUses;
	private String token;
	private String authType;
	private Date expirationDate;
	private Long usageCount;
	private String webActivityName;

	public WebActivityAuthorization() {
		usageCount = new Long(0);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMaxUses() {
		return maxUses;
	}

	public void setMaxUses(Long maxUses) {
		this.maxUses = maxUses;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Long getUsageCount() {
		return usageCount;
	}

	public void setUsageCount(Long usageCount) {
		this.usageCount = usageCount;
	}

	public String getWebActivityName() {
		return webActivityName;
	}

	public void setWebActivityName(String webActivityName) {
		this.webActivityName = webActivityName;
	}

	/**
	 * checks that this authorization object is neither expired nor fully used
	 * 
	 * @return
	 */
	public boolean isValidForAuth() {
		if (getExpirationDate() != null
				&& getExpirationDate().after(new Date())) {
			return false;
		}
		if (getMaxUses() != null && getUsageCount() != null) {
			if (getUsageCount() >= getMaxUses()) {
				return false;
			}
		}
		return true;
	}
}
