package org.waterforpeople.mapping.app.gwt.client.config;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * this service allows us to expose server-side configuration properties to the
 * UI
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("configrpcservice")
public interface ConfigurationService extends RemoteService {	
	public ConfigurationItemDto getConfigurationItem(String key);
}
