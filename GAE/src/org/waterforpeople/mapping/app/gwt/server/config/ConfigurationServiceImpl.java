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

package org.waterforpeople.mapping.app.gwt.server.config;

import java.util.HashSet;
import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationItemDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationService;

import com.gallatinsystems.common.util.PropertyUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * service that exposes appengine-web.xml properties to the UI. To prevent the
 * inadvertent exposure of "private" config values, this service relies on a
 * specific property "exportedProperties" in the appengine config file. This
 * property is a comma-separated list of config keys and only keys that appear
 * in that list will be returned from the service.
 * 
 * TODO: extend to use database-backed config store?
 * 
 * @author Christopher Fagiani
 * 
 */
public class ConfigurationServiceImpl extends RemoteServiceServlet implements
		ConfigurationService {
	public static final String EXPORTED_PROP_KEY = "exportedProperties";
	private static final long serialVersionUID = -2793826636292576588L;	
	private Set<String> authorizedKeys;

	public ConfigurationServiceImpl() {
		authorizedKeys = new HashSet<String>();
		String keys = PropertyUtil.getProperty(EXPORTED_PROP_KEY);
		if (keys != null) {
			String[] keyArray = keys.split(",");
			for (int i = 0; i < keyArray.length; i++) {
				authorizedKeys.add(keyArray[i].trim());
			}
		}
	}

	/**
	 * looks up the key in the propertyUtil and returns the value if the key is
	 * in the authorized key set
	 */
	@Override
	public ConfigurationItemDto getConfigurationItem(String key) {
		if (key != null) {
			if (authorizedKeys.contains(key)) {
				String val = PropertyUtil.getProperty(key);
				ConfigurationItemDto item = new ConfigurationItemDto();
				item.setName(key);
				item.setValue(val);
				return item;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
