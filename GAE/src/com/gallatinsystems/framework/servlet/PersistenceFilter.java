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

package com.gallatinsystems.framework.servlet;

import java.io.IOException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Handles binding a persistence manager to a thread-local variable (thereby giving us a single
 * persistenceManager per request) <code>
 * Adapted from: http://appengine-cookbook.appspot.com/recipe/persistencefilter/?id=ahJhcHBlbmdpbmUtY29va2Jvb2tyigELEgtSZWNpcGVJbmRleCI2YWhKaGNIQmxibWRwYm1VdFkyOXZhMkp2YjJ0eUVnc1NDRU5oZEdWbmIzSjVJZ1JLWVhaaERBDAsSBlJlY2lwZSI3YWhKaGNIQmxibWRwYm1VdFkyOXZhMkp2YjJ0eUVnc1NDRU5oZEdWbmIzSjVJZ1JLWVhaaERBMAw
 * </code>
 * 
 * @author Christopher Fagiani
 */
public final class PersistenceFilter implements Filter {
    private static final PersistenceManagerFactory persistenceManagerFactory = JDOHelper
            .getPersistenceManagerFactory("transactions-optional");

    private static PersistenceManagerFactory factory() {
        return persistenceManagerFactory;
    }

    private static ThreadLocal<PersistenceManager> currentManager = new ThreadLocal<PersistenceManager>();

    /**
     * check if this thread already has a manager and that it isn't closed
     * 
     * @return
     */
    public static synchronized PersistenceManager getManager() {
        if (currentManager.get() == null || currentManager.get().isClosed()) {
            currentManager.set(factory().getPersistenceManager());
        }
        return currentManager.get();
    }

    /**
     * gets a new manager then delegates the call to the next filter in the chain (or the servlet
     * itself) and then closes the persistence manager
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        PersistenceManager manager = null;
        try {
            manager = getManager();
            chain.doFilter(req, res);
        } finally {
            if (manager != null) {
                manager.flush();
                manager.close();
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
