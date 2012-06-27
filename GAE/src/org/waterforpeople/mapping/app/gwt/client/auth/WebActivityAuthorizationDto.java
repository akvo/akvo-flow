/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.client.auth;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object for authorization objects.
 * 
 * @author Christopher Fagiani
 * 
 */
public class WebActivityAuthorizationDto extends BaseDto {

	private static final long serialVersionUID = -3441769593324302599L;
	public static final String ANON_TYPE = "ANONYMOUS";
	public static final String USER_TYPE = "USER";
	
	private Long userId;
	private Long maxUses;
	private String token;
	private String authType;
	private Date expirationDate;
	private Long usageCount;
	private String webActivityName;
	private String payload;
	private String name;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
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
				&& getExpirationDate().before(new Date())) {
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
