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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;

public class MemCacheUtils {

	private static Logger log = Logger.getLogger(MemCacheUtils.class.getName());

	/**
	 * Initialize a Cache object with a expiration delta defined in seconds
	 *
	 * @param expirySeconds
	 *            Expiration delta defined in seconds
	 * @return A Cache object or <b>null</b> when the runtime couldn't
	 *         initialize the object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Cache initCache(Integer expirySeconds) {
		Cache cache = null;

		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, expirySeconds);
		props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);

		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Couldn't initialize cache: " + e.getMessage(), e);
		}
		return cache;
	}

	/**
	 * Puts an object in the cache, the expiration is already defined in the
	 * cache object.<br>
	 * The current implementation of Cache.put always return null, therefore
	 * this method doesn't return any value. <b>NOTE:</b> A failed put operation will get
	 * logged but also fail silently to the executing program
	 *
	 * @param cache
	 *            An initialized Cache object
	 * @param key
	 *            The key (must implement java.io.Serializable)
	 * @param value
	 *            The value (must implement java.io.Serializable)
	 */
	public static void putObject(Cache cache, Object key, Object value) {
		try {
			if (cache != null) {
				cache.put(key, value);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to store value in memcache: " + e.getMessage(), e);
		}
	}
}