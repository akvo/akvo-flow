/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;
import com.google.appengine.api.users.UserServiceFactory;

public class CurrentUserServlet extends HttpServlet {

    private UserRoleDao userRoleDAO = new UserRoleDao();

    private UserAuthorizationDAO userAuthorizationDAO = new UserAuthorizationDAO();

    private static final long serialVersionUID = -430515593814261770L;
    private static final Logger log = Logger.getLogger(CurrentUserServlet.class
            .getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

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
        final UserDao uDao = new UserDao();
        final String currentUserEmail = UserServiceFactory.getUserService()
                .getCurrentUser().getEmail().toLowerCase();
        final User currentUser = uDao.findUserByEmail(currentUserEmail);

        context.put("user", currentUser);
        context.put("permissions", getPermissionsMap(currentUser));

        final StringWriter writer = new StringWriter();
        t.merge(context, writer);

        resp.setContentType("application/javascript;charset=UTF-8");

        final PrintWriter pw = resp.getWriter();
        pw.println(writer.toString());
        pw.close();
    }

    /**
     * Retrieve a javascript map of the paths and corresponding permissions for the current user
     *
     * @param currentUser
     * @return
     */
    private String getPermissionsMap(User currentUser) {
        List<UserAuthorization> authorizationList = userAuthorizationDAO.listByUser(currentUser
                .getKey().getId());
        Map<Long, UserRole> roleMap = new HashMap<Long, UserRole>();
        for (UserRole role : userRoleDAO.list(Constants.ALL_RESULTS)) {
            roleMap.put(role.getKey().getId(), role);
        }
        Map<String, Set<Permission>> permissions = new HashMap<String, Set<Permission>>();
        for (UserAuthorization auth : authorizationList) {
            UserRole role = roleMap.get(auth.getRoleId());
            if (role != null) {
                permissions.put(auth.getObjectPath(), role.getPermissions());
            }
        }

        addSuperAdminPermissions(currentUser, permissions);

        ObjectMapper jsonObjectMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            jsonObjectMapper.writeValue(writer, permissions);
        } catch (JsonGenerationException e) {
            // ignore
        } catch (JsonMappingException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        }

        return writer.toString();
    }

    /**
     * Enable users designated as superAdmin in the backend complete access to all functionality on
     * the frontend
     *
     * @param currentUser
     * @param permissions
     */
    private void addSuperAdminPermissions(User currentUser, Map<String, Set<Permission>> permissions) {
        if (!currentUser.getPermissionList().equals("0")) {
            return;
        }

        List<Permission> permissionList = Arrays.asList(Permission.values());
        permissions.put("/", new HashSet<Permission>(permissionList));
    }
}
