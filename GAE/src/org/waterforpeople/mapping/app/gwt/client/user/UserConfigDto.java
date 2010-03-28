package org.waterforpeople.mapping.app.gwt.client.user;

import java.io.Serializable;

public class UserConfigDto implements Serializable {

	private String dashboardConfig;

	public String getDashboardConfig() {
		return dashboardConfig;
	}

	public void setDashboardConfig(String dashboardConfig) {
		this.dashboardConfig = dashboardConfig;
	}
	
}
