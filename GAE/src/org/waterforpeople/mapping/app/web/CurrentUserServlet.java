/*
 *  Copyright (C) 2012-2016, 2018-2019 Stichting Akvo (Akvo Foundation)
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akvo.flow.rest.security.user.GaeUser;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gallatinsystems.framework.dao.ForkAndCompareAuthzDao;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;

public class CurrentUserServlet extends HttpServlet {

    private static final long serialVersionUID = -430515593814261770L;
    private static final Logger log = Logger.getLogger(CurrentUserServlet.class
            .getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (getCurrentUser() == null) {
            return;
        }

        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");
        try {
            engine.init();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not initialize velocity", e);
        }

        Template t = null;
        try {
            t = engine.getTemplate("CurrentUser.vm");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not get the template `CurrentUser`", e);
            return;
        }

        final VelocityContext context = new VelocityContext();
        final User currentUser = getCurrentUser();

        context.put("user", currentUser);
        String permissionsMap = new ForkAndCompareAuthzDao().getPermissionsMap(currentUser);
        context.put("permissions", permissionsMap);

        final StringWriter writer = new StringWriter();
        t.merge(context, writer);

        resp.setContentType("application/javascript;charset=UTF-8");

        final PrintWriter pw = resp.getWriter();
        pw.println(writer.toString());
        pw.close();
    }

    public static User getCurrentUser() {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof GaeUser) {
            GaeUser user = (GaeUser) principal;

            if (user.getEmail() == null) {
                return null;
            }

            final String currentUserEmail = user.getEmail().toLowerCase();
            // We can use the GaeUser directly as downstream only the email and permission list is used
            // See CurrentUser.vm
            final UserDao uDao = new UserDao();
            return uDao.findUserByEmail(currentUserEmail);

        } else {
            return null;
        }
    }
}
