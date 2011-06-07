package org.waterforpeople.mapping.app.gwt.client.config;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConfigurationServiceAsync {

	void getConfigurationItem(String key,
			AsyncCallback<ConfigurationItemDto> callback);

}
