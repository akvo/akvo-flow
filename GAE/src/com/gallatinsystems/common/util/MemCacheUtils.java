/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.gallatinsystems.surveyal.app.web.SurveyalRestServlet;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

public class MemCacheUtils {
	
	// initialize the memcache
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Cache initCache(Integer expirySeconds){
		Cache cache = null;
		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, expirySeconds);
		props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (Exception e) {
			Logger log = Logger
					.getLogger(SurveyalRestServlet.class.getName());
			log.log(Level.SEVERE,
					"Couldn't initialize cache: " + e.getMessage(), e);
		}
		return cache;
	}
	
	// try to store a value in the cache
	public static void putObject(Cache cache, Object key, Object value){
		try{
			if (cache != null){
				cache.put(key, value);
			}
		} catch (Exception e) {
			Logger log = Logger
					.getLogger(SurveyalRestServlet.class.getName());
			log.log(Level.SEVERE,
					"Failed to store value in memcache: " + e.getMessage(), e);
		}
	}
}