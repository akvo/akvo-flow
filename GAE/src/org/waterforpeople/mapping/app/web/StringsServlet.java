/*
 *  Copyright (C) 2013-2016 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Change the locale setting of the current user to a requested one.
 */
public class StringsServlet extends HttpServlet {

    private static final long serialVersionUID = -5814616069972956097L;
    private static final Logger log = Logger.getLogger(StringsServlet.class
            .getClass().getName());
    final Set<String> enabledLocales = new HashSet<String>(
            Arrays.asList("en", "es", "fr", "pt"));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // if specific locale is requested we simply end
        String locale = req.getParameter("locale");
        if (locale == null || !isAllowedLocale(locale.trim())) {
            return;
        }

        // reset user locale
        final com.google.appengine.api.users.User currentGoogleUser = UserServiceFactory
                .getUserService().getCurrentUser();
        if (currentGoogleUser == null || currentGoogleUser.getEmail() == null) {
            return;
        }

        final UserDao uDao = new UserDao();
        final User currentUser = uDao.findUserByEmail(currentGoogleUser.getEmail());
        currentUser.setLanguage(locale);
        uDao.save(currentUser);
        log.info("Changed locale setting for user to '" + locale.toUpperCase() + "'");
    }

    private boolean isAllowedLocale(String locale) {
        return enabledLocales.contains(locale);
    }
}
