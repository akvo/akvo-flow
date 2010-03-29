package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Data structure for user configuration/personalization information.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class UserConfig extends BaseDomain {
	
	private static final long serialVersionUID = 515991819240493160L;
	@Persistent
	private String dashboardConfig;

	public String getDashboardConfig() {
		return dashboardConfig;
	}

	public void setDashboardConfig(String dashboardConfig) {
		this.dashboardConfig = dashboardConfig;
	}
}
