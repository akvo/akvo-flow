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

package com.gallatinsystems.common.util;

import java.io.StringWriter;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Utility for interacting with the Velocity engine.
 * 
 * @author Christopher Fagiani
 */
public class VelocityUtil {

    private static final Logger log = Logger.getLogger(VelocityUtil.class
            .getName());
    private static Cache templateCache;

    public static final String CACHE_KEY_PREFIX = "VELOCITY_TEMPLATE/";

    static {
        Velocity.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");
        try {
            Velocity.init();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not initialize velocity", e);
        }
        try {
            templateCache = CacheManager.getInstance().getCacheFactory()
                    .createCache(Collections.EMPTY_MAP);
        } catch (CacheException e) {
            log.log(Level.SEVERE, "Could not initialize cache", e);

        }
    }

    /**
     * merges a hydrated context with a template identified by the templateName passed in.
     * 
     * @param context
     * @param templaet
     * @return
     * @throws Exception
     */
    public static String mergeContext(VelocityContext context,
            String templateName) throws Exception {
        return mergeContext(context, templateName, null);
    }

    /**
     * merges a hydrated context with a template identified by the template name. The template will
     * be resolved in the following order: the Cache will be checked. If not found, the backingStore
     * will be queried (if backingStore is not null) then, if still not found, it will look for a
     * file with the name matching the template file.
     * 
     * @param context
     * @param templateName
     * @param backingStore
     * @return
     * @throws Exception
     */
    public static String mergeContext(VelocityContext context,
            String templateName, TemplateCacheBackingStore backingStore)
            throws Exception {
        String templateText = (String) templateCache.get(CACHE_KEY_PREFIX
                + templateName);
        StringWriter writer = new StringWriter();
        if (templateText == null && backingStore != null) {
            templateText = backingStore.getByKey(templateName);
            if (templateText != null) {
                templateCache
                        .put(CACHE_KEY_PREFIX + templateName, templateText);
            }
        }
        if (templateText == null) {
            Template t = Velocity.getTemplate(templateName);
            t.merge(context, writer);
        } else {
            Velocity.evaluate(context, writer, templateName, templateText);
        }
        return writer.toString();
    }

    /**
     * Implementors of this interface know how to look up the content identified by the key passed
     * in in the persistent store (to handle a cache miss).
     * 
     * @author Christopher Fagiani
     */
    public interface TemplateCacheBackingStore {
        public String getByKey(String key);
    }

}
