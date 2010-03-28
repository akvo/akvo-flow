package org.waterforpeople.mapping.app.gwt.client.user;

import java.io.Serializable;

public class UserConfigDto implements Serializable {
	private static final long serialVersionUID = 4515497143926759239L;
	private String dashboardConfig;

	public String getDashboardConfig() {
		return dashboardConfig;
	}

	public void setDashboardConfig(String dashboardConfig) {
		this.dashboardConfig = dashboardConfig;
	}
	
}
