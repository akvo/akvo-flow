package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class UserConfig extends BaseDomain {

	@Persistent
	private String dashboardConfig;

	public String getDashboardConfig() {
		return dashboardConfig;
	}

	public void setDashboardConfig(String dashboardConfig) {
		this.dashboardConfig = dashboardConfig;
	}
}
